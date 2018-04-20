package net.north101.android.ghplayertracker

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.north101.android.ghplayertracker.data.CharacterClass

class ClassListAdapter : RecyclerView.Adapter<ClassViewHolder>() {
    private val items = ArrayList<CharacterClass>()
    private var listener: BaseViewHolder.ClickListener<CharacterClass>? = null

    var onItemClickListener: BaseViewHolder.ClickListener<CharacterClass> = object : BaseViewHolder.ClickListener<CharacterClass>() {
        override fun onItemClick(holder: BaseViewHolder<CharacterClass>) {
            listener!!.onItemClick(holder)
        }

        override fun onItemLongClick(holder: BaseViewHolder<CharacterClass>): Boolean {
            return listener!!.onItemLongClick(holder)
        }
    }

    fun updateItems(items: List<CharacterClass>) {
        val diffCallback = RecyclerListItemsCallback(this.items, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.items.clear()
        this.items.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        return ClassViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(items[position])
        holder.setOnItemClickListener(this.onItemClickListener)
    }

    override fun onViewRecycled(holder: ClassViewHolder) {
        super.onViewRecycled(holder)

        holder.unbind()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnClickListener(listener: BaseViewHolder.ClickListener<CharacterClass>) {
        this.listener = listener
    }
}
