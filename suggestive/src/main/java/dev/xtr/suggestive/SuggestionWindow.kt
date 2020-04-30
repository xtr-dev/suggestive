@file:Suppress("MemberVisibilityCanBePrivate")

package dev.xtr.suggestive

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.*
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView

/**
 * An [EditText] suggestion [PopupWindow] based on [RecyclerView].
 *
 * @property context the context
 * @property anchor the view to anchor to
 * @property view the content view
 * @property onQuery called when the text of [anchor] changes (if it's an [EditText])
 * @property backgroundDrawable the popup background drawable
 * @property preferredPosition the preferred vertical popup position
 * @property popupGravity determines the horizontal alignment of the popup with [anchor]
 * @property hideOnBlur if true the popup will hide itself when [anchor] loses focus
 * @property constrainToAnchorBounds constrains the popup boundaries to the [anchor]'s boundaries if true
 * @property minCharacters the minimum amount of characters required before invoking [onQuery]
 * @property attachTextChangeListener attaches the text change listener that invokes [onQuery], [show] and [dismiss]
 * @property onQueryThrottle the minimum delay in milliseconds between each [onQuery] invocation
 */
open class SuggestionWindow(
    protected val context: Context,
    internal val anchor: View,
    internal val view: View,
    private val onQuery: (query: String) -> Unit,
    private val backgroundDrawable: Drawable,
    internal val preferredPosition: PreferredPosition,
    internal val popupGravity: Int,
    private val hideOnBlur: Boolean,
    internal val constrainToAnchorBounds: Boolean,
    private val minCharacters: Int,
    private val attachTextChangeListener: Boolean,
    private val onQueryThrottle: Long
) : PopupWindow(context) {
    enum class PreferredPosition {
        /**
         * Always show the popup above the anchor.
         */
        ABOVE,

        /**
         * Always show the popup below the anchor.
         */
        BELOW,

        /**
         * Show the popup above or below based on available space.
         */
        BEST_FIT
    }

    /**
     * Animation that loops infinitely and tracks the [anchor]'s screen position to counter-act
     *  vertical translations of the view caused by IME opening.
     */
    inner class FollowAnchorAnimation : Animation() {
        init {
            duration = 1000 / 60
            repeatCount = INFINITE
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            // Only update at the first animation frame at a duration of 1000/60 this should
            //  result in a single call per frame
            if (interpolatedTime == 0f) {
                updatePositionAndSize()
            }
        }
    }

    private val onQueryRunnable = Runnable {
        onQuery((anchor as? TextView)?.text.toString())
    }

    init {
        if (hideOnBlur) {
            anchor.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus && isShowing) {
                    hide()
                }
            }
        }
        if (attachTextChangeListener && anchor is EditText) {
            anchor.addTextChangedListener(onTextChanged = { s: CharSequence?, _: Int, _: Int, _: Int ->
                if (!s.isNullOrBlank() && s.length > minCharacters) {
                    show()
                    if (onQueryThrottle == 0L) {
                        onQuery(s.toString())
                    }
                    else {
                        anchor.removeCallbacks(onQueryRunnable)
                        anchor.postDelayed(onQueryRunnable, onQueryThrottle)
                    }
                }
                else if (s.isNullOrBlank() || s.isEmpty()) {
                    hide()
                }
            })
        }
        isOutsideTouchable = true
    }

    /**
     * Left popup margin, in pixels.
     */
    var marginLeft: Int = 0

    /**
     * Top popup margin, in pixels.
     */
    var marginTop: Int = 0

    /**
     * Right popup margin, in pixels.
     */
    var marginRight: Int = 0

    /**
     * Bottom popup margin, in pixels.
     */

    var marginBottom: Int = 0

    /**
     * Popup vertical safe margin, in pixels.
     */
    var verticalSafeMargin: Int = 0

    /**
     * The window's stable system insets.
     */
    internal val stableInsets = Rect()

    /**
     * The popup window's rect.
     */
    internal val windowRect = Rect()

    /**
     * The [anchor]'s rect.
     */
    internal val anchorRect = Rect()

    /**
     * The [view]'s measures dimensions.
     */
    internal val viewDimensions = Rect()

    /**
     * The window's visible region frame.
     */
    internal val windowVisibleFrame = Rect()

    /**
     * Flag set by [requestLayout] to indicate a relayout request.
     */
    internal var layoutRequested = false

    /**
     * The popup's [PreferredPosition] calculated by [determinePosition].
     * If [preferredPosition] is [PreferredPosition.BEST_FIT] this field will contain either
     *  [PreferredPosition.ABOVE] or [PreferredPosition.BELOW] based on the window's visible region.
     */
    internal var determinedPosition: PreferredPosition = PreferredPosition.ABOVE

    /**
     * Update the positioning and size of the popup.
     */
    protected fun updatePositionAndSize() {
        determinePosition()
        updateWindowRect()
        update(
            windowRect.left,
            windowRect.top,
            windowRect.width(),
            windowRect.height()
        )
    }

    /**
     * Show the popup.
     */
    open fun show() {
        if (isShowing) {
            return
        }

        setBackgroundDrawable(backgroundDrawable)
        elevation = 8f
        (contentView as? ViewGroup)?.removeAllViews()
        contentView = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            clipChildren = false
            clipToPadding = false
            addView(view)
        }

        // First add an inset listener to receive the window's system insets
        //  to properly position the window with afterwards
        anchor.rootView.setOnApplyWindowInsetsListener { v, insets ->
            stableInsets.set(
                insets.stableInsetLeft,
                insets.stableInsetTop,
                insets.stableInsetRight,
                insets.stableInsetBottom
            )
            // Position the window using the insets and then show it
            updatePositionAndSize()
            showAtLocation(anchor, Gravity.TOP or Gravity.START,
                windowRect.left, windowRect.top)
            (contentView.parent as View).clearAnimation()
            (contentView.parent as View).startAnimation(FollowAnchorAnimation())
            insets
        }
        // Request the callback
        anchor.rootView.requestApplyInsets()
    }

    /**
     * Hide the popup.
     */
    open fun hide() {
        dismiss()
    }

    override fun dismiss() {
        (contentView.parent as View).clearAnimation()
        super.dismiss()
    }

    /**
     * Requests a relayout and repositioning of the popup.
     */
    fun requestLayout() {
        layoutRequested = true
        anchor.removeCallbacks(::updatePositionAndSize)
        anchor.post(::updatePositionAndSize)
    }
}