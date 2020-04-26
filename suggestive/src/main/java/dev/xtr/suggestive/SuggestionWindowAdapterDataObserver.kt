package dev.xtr.suggestive

import androidx.recyclerview.widget.RecyclerView

/**
 * Simple [RecyclerView.AdapterDataObserver] that calls [callback] on any method call.
 *
 * @property callback the callback
 */
internal class SuggestionWindowAdapterDataObserver(
    private val callback: () -> Unit
) : RecyclerView.AdapterDataObserver() {
    override fun onChanged() {
        callback()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        callback()
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        callback()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        callback()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        callback()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        callback()
    }
}