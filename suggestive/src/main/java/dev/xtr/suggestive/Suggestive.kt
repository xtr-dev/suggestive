@file:Suppress("MemberVisibilityCanBePrivate")

package dev.xtr.suggestive

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Suggestive suggestion window factory.
 */
object Suggestive {
    /**
     * Create a [SuggestionWindow] containing [view] anchored to [anchor].
     *
     * @param anchor the anchor view
     * @param view the content view
     * @return the popup window
     */
    fun view(anchor: View, view: View,
             onQuery: (query: String) -> Unit = {},
             backgroundDrawable: Drawable = ContextCompat.getDrawable(anchor.context, R.drawable.popup_rounded_bg)
                 ?: ColorDrawable(Color.WHITE),
             preferredPosition: SuggestionWindow.PreferredPosition = SuggestionWindow.PreferredPosition.BEST_FIT,
             hideOnBlur: Boolean = true,
             constrainWidthToAnchorBounds: Boolean = true): SuggestionWindow {
        val window = SuggestionWindow(
            anchor.context,
            anchor,
            view,
            onQuery,
            backgroundDrawable,
            preferredPosition,
            hideOnBlur,
            constrainWidthToAnchorBounds
        )
        if (anchor is TextView) {
            anchor.addTextChangedListener(onTextChanged = { s: CharSequence?, _: Int, _: Int, _: Int ->
                if (!s.isNullOrBlank()) {
                    window.show()
                }
                else {
                    window.hide()
                }
            })
        }
        return window
    }

    /**
     * Create a [SuggestionWindow] containing a [RecyclerView] with [adapter] anchored to [anchor].
     *
     * @param anchor the anchor view
     * @param adapter the recycler view adapter
     * @param onQuery the query callback
     * @return the popup window
     */
    fun recycler(anchor: View, adapter: RecyclerView.Adapter<*>,
                 onQuery: (query: String) -> Unit = {},
                 backgroundDrawable: Drawable = ContextCompat.getDrawable(anchor.context, R.drawable.popup_rounded_bg)
                     ?: ColorDrawable(Color.WHITE),
                 preferredPosition: SuggestionWindow.PreferredPosition = SuggestionWindow.PreferredPosition.BEST_FIT,
                 hideOnBlur: Boolean = true,
                 constrainWidthToAnchorBounds: Boolean = true): SuggestionWindow {
        val context = anchor.context
        val rv = RecyclerView(context)
        rv.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val window = view(
            anchor,
            rv,
            onQuery,
            backgroundDrawable,
            preferredPosition,
            hideOnBlur,
            constrainWidthToAnchorBounds
        )
        rv.adapter = adapter
        adapter.registerAdapterDataObserver(SuggestionWindowAdapterDataObserver {
            window.requestLayout()
        })
        rv.layoutManager = LinearLayoutManager(context)
        return window
    }
}