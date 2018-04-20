package net.north101.android.ghplayertracker

import android.support.v7.util.DiffUtil

class RecyclerListItemsCallback(
        private val oldItems: List<RecyclerItemCompare>,
        private val newItems: List<RecyclerItemCompare>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return when {
            oldItem == newItem -> return true
            oldItem::class == newItem::class -> return oldItem.compareItemId == newItem.compareItemId
            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        return oldItem === newItem
    }
}