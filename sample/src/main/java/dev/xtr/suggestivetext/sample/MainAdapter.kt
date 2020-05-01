package dev.xtr.suggestivetext.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.withStyledAttributes
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.xtr.suggestivetext.R
import dev.xtr.suggestivetext.sample.api.SearchResult

class MainAdapter : ListAdapter<SearchResult, MainAdapter.VH>(callback) {
    companion object {
        val callback = object : DiffUtil.ItemCallback<SearchResult>() {
            override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
                return oldItem.trackName == newItem.trackName && oldItem.artistName == newItem.artistName
            }
        }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text1: TextView = itemView.findViewById(android.R.id.text1)
        val text2: TextView = itemView.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.suggestive_item, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.updateLayoutParams {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        holder.run {
            text1.text = currentList[position].artistName
            text2.text = currentList[position].trackName
            itemView.setOnClickListener {
                Toast.makeText(it.context, "${currentList[position]}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}