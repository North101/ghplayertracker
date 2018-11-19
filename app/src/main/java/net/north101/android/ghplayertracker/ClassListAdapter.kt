package net.north101.android.ghplayertracker

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.north101.android.ghplayertracker.data.CharacterClass
import net.north101.android.ghplayertracker.data.CharacterClassGroup

class ClassListAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {
    private val items = ArrayList<RecyclerItemCompare>()
    private var listener: BaseViewHolder.ClickListener<CharacterClass>? = null

    var onItemClickListener: BaseViewHolder.ClickListener<CharacterClass> = object : BaseViewHolder.ClickListener<CharacterClass>() {
        override fun onItemClick(holder: BaseViewHolder<CharacterClass>) {
            listener!!.onItemClick(holder)
        }

        override fun onItemLongClick(holder: BaseViewHolder<CharacterClass>): Boolean {
            return listener!!.onItemLongClick(holder)
        }
    }

    fun updateItems(items: List<CharacterClassGroup>) {
        val newItems = ArrayList<RecyclerItemCompare>()
        for (item in items) {
            newItems.add(TextHeader(item.name))
            newItems.addAll(item.classes)
        }

        val diffCallback = RecyclerListItemsCallback(this.items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.items.clear()
        this.items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            HEADER_VIEW_TYPE -> TextHeaderViewHolder.inflate(parent)
            CLASS_VIEW_TYPE -> ClassViewHolder.inflate(parent)
            else -> throw RuntimeException(viewType.toString())
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = items[position]
        when (holder) {
            is TextHeaderViewHolder -> holder.bind(item as TextHeader)
            is ClassViewHolder -> {
                holder.bind(item as CharacterClass)
                holder.setOnItemClickListener(this.onItemClickListener)
            }
            else -> throw RuntimeException(holder.toString())
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item) {
            is TextHeader -> HEADER_VIEW_TYPE
            is CharacterClass -> CLASS_VIEW_TYPE
            else -> throw RuntimeException(position.toString())
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder<*>) {
        super.onViewRecycled(holder)

        holder.unbind()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int): Any {
        return items[position]
    }

    companion object {
        const val HEADER_VIEW_TYPE = 1
        const val CLASS_VIEW_TYPE = 2
    }

    fun setOnClickListener(listener: BaseViewHolder.ClickListener<CharacterClass>) {
        this.listener = listener
    }
}
