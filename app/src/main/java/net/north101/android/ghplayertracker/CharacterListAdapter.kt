package net.north101.android.ghplayertracker

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.north101.android.ghplayertracker.BaseViewHolder.ClickListener
import net.north101.android.ghplayertracker.data.SelectableCharacter
import java.util.*

class CharacterListAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {
    private val items = ArrayList<RecyclerItemCompare>()

    private var listener: ClickListener<SelectableCharacter>? = null
    var onItemClickListener: ClickListener<SelectableCharacter> = object : ClickListener<SelectableCharacter>() {
        override fun onItemClick(holder: BaseViewHolder<SelectableCharacter>) {
            listener!!.onItemClick(holder)
        }

        override fun onItemLongClick(holder: BaseViewHolder<SelectableCharacter>): Boolean {
            return listener!!.onItemLongClick(holder)
        }
    }

    fun updateItems(items: List<SelectableCharacter>) {
        val activeCharacters = ArrayList<SelectableCharacter>()
        val retiredCharacters = ArrayList<SelectableCharacter>()
        for (item in items) {
            if (item.character.retired) {
                retiredCharacters.add(item)
            } else {
                activeCharacters.add(item)
            }
        }

        val newItems = ArrayList<RecyclerItemCompare>()
        if (activeCharacters.isNotEmpty()) {
            newItems.add(TextHeader("Active"))
            newItems.addAll(activeCharacters)
        }
        if (retiredCharacters.isNotEmpty()) {
            newItems.add(TextHeader("Retired"))
            newItems.addAll(retiredCharacters)
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
            CHARACTER_VIEW_TYPE -> CharacterViewHolder.inflate(parent)
            else -> throw RuntimeException(viewType.toString())
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = items[position]
        when (holder) {
            is TextHeaderViewHolder -> holder.bind(item as TextHeader)
            is CharacterViewHolder -> {
                holder.bind(item as SelectableCharacter)
                holder.setOnItemClickListener(this.onItemClickListener)
            }
            else -> throw RuntimeException(holder.toString())
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder<*>) {
        super.onViewRecycled(holder)

        holder.unbind()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item) {
            is TextHeader -> HEADER_VIEW_TYPE
            is SelectableCharacter -> CHARACTER_VIEW_TYPE
            else -> throw RuntimeException(position.toString())
        }
    }

    companion object {
        const val HEADER_VIEW_TYPE = 1
        const val CHARACTER_VIEW_TYPE = 2
    }

    fun setOnClickListener(listener: ClickListener<SelectableCharacter>) {
        this.listener = listener
    }
}
