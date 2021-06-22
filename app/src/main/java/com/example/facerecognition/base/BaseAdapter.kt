package com.example.facerecognition.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BaseAdapter<T, VH : RecyclerView.ViewHolder>(
    private val onCreateViewHolder: (parent: ViewGroup, viewType: Int) -> VH,
    private val onBindViewHolder: (viewHolder: VH, position: Int, data: T) -> Unit
) : RecyclerView.Adapter<VH>() {
    private var items = mutableListOf<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        onCreateViewHolder.invoke(parent, viewType)

    override fun onBindViewHolder(holder: VH, position: Int) =
        onBindViewHolder.invoke(holder, position, items[position])


    override fun getItemCount() = items.size

    fun addClearItems(list: List<T>?) {
        list?.let {
            items.clear()
            items.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun clear() {
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }
}
