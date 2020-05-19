@file:Suppress("MemberVisibilityCanBePrivate")

package dev.xtr.suggestive

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Suggestive suggestion window factory.
 */
object Suggestive {
    /**
     * Create a [SuggestionWindow] containing [view] anchored to [anchor].
     *
     * @param anchor the view to anchor to
     * @param view the content view
     * @param onQuery called when the text of [anchor] changes (if it's an [EditText])
     * @param backgroundDrawable the popup background drawable
     * @param preferredPosition the preferred popup position
     * @param hideOnBlur if true the popup will hide itself when [anchor] loses focus
     * @param constrainWidthToAnchorBounds constrains the popup width to the [anchor]'s boundaries if true
     * @param minCharacters the minimum amount of characters required before invoking [onQuery]
     * @param attachTextChangeListener attaches the text change listener that invokes [onQuery], [SuggestionWindow.show] and [SuggestionWindow.dismiss]
     * @param onQueryThrottle the minimum delay in milliseconds between each [onQuery] invocation
     * @param dismissOnBackPress dismisses the popup when the back button is pressed (context needs to extend [AppCompatActivity])
     */
    fun view(anchor: View, view: View,
             onQuery: (query: String) -> Unit = {},
             backgroundDrawable: Drawable = ContextCompat.getDrawable(anchor.context, R.drawable.popup_rounded_bg)
                 ?: ColorDrawable(Color.WHITE),
             gravity: Int = Gravity.CENTER,
             preferredPosition: SuggestionWindow.PreferredPosition = SuggestionWindow.PreferredPosition.BEST_FIT,
             hideOnBlur: Boolean = true,
             constrainWidthToAnchorBounds: Boolean = true,
             minCharacters: Int = 0,
             attachTextChangeListener: Boolean = true,
             onQueryThrottle: Long = 0,
             dismissOnBackPress: Boolean = true
    ): SuggestionWindow {
        return SuggestionWindow(
            anchor.context,
            anchor,
            view,
            onQuery,
            backgroundDrawable,
            preferredPosition,
            gravity,
            hideOnBlur,
            constrainWidthToAnchorBounds,
            minCharacters,
            attachTextChangeListener,
            onQueryThrottle,
            dismissOnBackPress
        )
    }

    /**
     * Create a [SuggestionWindow] containing a [RecyclerView] with [adapter] anchored to [anchor].
     *
     * @param anchor the view to anchor to
     * @param adapter the recycler view adapter
     * @param layoutManager the recycler view layout manager
     * @param onQuery called when the text of [anchor] changes (if it's an [EditText])
     * @param backgroundDrawable the popup background drawable
     * @param preferredPosition the preferred popup position
     * @param hideOnBlur if true the popup will hide itself when [anchor] loses focus
     * @param constrainWidthToAnchorBounds constrains the popup width to the [anchor]'s boundaries if true
     * @param minCharacters the minimum amount of characters required before invoking [onQuery]
     * @param attachTextChangeListener attaches the text change listener that invokes [onQuery], [SuggestionWindow.show] and [SuggestionWindow.dismiss]
     * @param onQueryThrottle the minimum delay in milliseconds between each [onQuery] invocation
     * @param dismissOnBackPress dismisses the popup when the back button is pressed (context needs to extend [AppCompatActivity])
     * @return the popup window
     */
    fun recycler(anchor: View,
                 adapter: RecyclerView.Adapter<*>,
                 layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(anchor.context),
                 onQuery: (query: String) -> Unit = {},
                 backgroundDrawable: Drawable = ContextCompat.getDrawable(anchor.context, R.drawable.popup_rounded_bg) ?: ColorDrawable(Color.WHITE),
                 gravity: Int = Gravity.CENTER,
                 preferredPosition: SuggestionWindow.PreferredPosition = SuggestionWindow.PreferredPosition.BEST_FIT,
                 hideOnBlur: Boolean = true,
                 constrainWidthToAnchorBounds: Boolean = true,
                 minCharacters: Int = 0,
                 attachTextChangeListener: Boolean = true,
                 onQueryThrottle: Long = 0,
                 dismissOnBackPress: Boolean = true): SuggestionWindow {
        val context = anchor.context
        val rv = RecyclerView(context)
        rv.layoutManager = layoutManager
        rv.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val window = view(
            anchor,
            rv,
            onQuery,
            backgroundDrawable,
            gravity,
            preferredPosition,
            hideOnBlur,
            constrainWidthToAnchorBounds,
            minCharacters,
            attachTextChangeListener,
            onQueryThrottle,
            dismissOnBackPress
        )
        rv.adapter = adapter
        adapter.registerAdapterDataObserver(SuggestionWindowAdapterDataObserver {
            window.requestLayout()
        })
        rv.layoutManager = LinearLayoutManager(context)
        return window
    }
}