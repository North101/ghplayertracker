package net.north101.android.ghplayertracker

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.north101.android.ghplayertracker.livedata.*

class CharacterAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {
    private val items = ArrayList<RecyclerItemCompare>()
    lateinit var display: DisplayItems

    enum class DisplayItems(val id: Int) {
        None(1),
        Left(2),
        Right(4),
        Both(Left.id or Right.id)
    }

    data class Stats(val character: CharacterLiveData) : RecyclerItemCompare {
        override val compareItemId: String
            get() = ""
    }

    data class Item(val item: ItemLiveData) : RecyclerItemCompare {
        override val compareItemId: String
            get() = item.name.toString()
    }

    data class Note(val note: InitLiveData<String>) : RecyclerItemCompare {
        override val compareItemId: String
            get() = note.value.toString()
    }

    data class Perk(val perk: PerkLiveData) : RecyclerItemCompare {
        override val compareItemId: String
            get() = perk.perk.text
    }

    data class PerkNote(val index: Int, val perkNote: PerkNoteLiveData) : RecyclerItemCompare {
        override val compareItemId: String
            get() = index.toString()
    }

    var onNumberEditClick: ((String) -> Unit)? = null

    var onItemAddClick: (() -> Unit)? = null
    var onItemEditClick: ((ItemLiveData) -> Unit)? = null
    var onItemDeleteClick: ((ItemLiveData) -> Unit)? = null

    var onNoteAddClick: (() -> Unit)? = null
    var onNoteEditClick: ((CharacterAdapter.Note) -> Unit)? = null
    var onNoteDeleteClick: ((CharacterAdapter.Note) -> Unit)? = null

    fun updateItems(character: CharacterLiveData) {
        val newItems = ArrayList<RecyclerItemCompare>()

        if ((display.id and DisplayItems.Left.id) == DisplayItems.Left.id) {
            newItems.add(TextHeader("Character"))
            newItems.add(Stats(character))

            newItems.add(TextHeaderAdd("Items", {
                onItemAddClick?.invoke()
            }))
            newItems.addAll(character.items.value.sortedBy { it.type.value }.map {
                Item(it)
            })
            newItems.add(TextHeaderAdd("Notes", {
                onNoteAddClick?.invoke()
            }))
            newItems.addAll(character.notes.value.map {
                Note(it)
            })
        }

        if ((display.id and DisplayItems.Right.id) == DisplayItems.Right.id) {
            newItems.add(TextHeader("Perks"))
            newItems.addAll(character.perks.value.map {
                Perk(it)
            })

            newItems.add(TextHeader("Perk Notes"))
            newItems.addAll(character.perkNotes.value.withIndex().map {
                PerkNote(it.index, it.value)
            })
        }

        val diffCallback = RecyclerListItemsCallback(this.items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.items.clear()
        this.items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (CharacterViewType.values()[viewType]) {
            CharacterViewType.Header -> TextHeaderViewHolder.inflate(parent)
            CharacterViewType.HeaderAdd -> TextHeaderAddViewHolder.inflate(parent)
            CharacterViewType.Stats -> CharacterStatsViewHolder.inflate(parent)
            CharacterViewType.Item -> CharacterItemViewHolder.inflate(parent)
            CharacterViewType.Note -> CharacterNoteViewHolder.inflate(parent)
            CharacterViewType.Perk -> CharacterPerkViewHolder.inflate(parent)
            CharacterViewType.PerkNote -> CharacterPerkNoteViewHolder.inflate(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = items[position]
        when (holder) {
            is TextHeaderViewHolder -> holder.bind(item as TextHeader)
            is TextHeaderAddViewHolder -> holder.bind(item as TextHeaderAdd)
            is CharacterStatsViewHolder -> {
                holder.bind((item as Stats).character)
                holder.onNumberClick = {
                    onNumberEditClick?.invoke(it)
                }
            }
            is CharacterItemViewHolder -> {
                holder.bind((item as Item).item)
                holder.onItemEditClick = {
                    onItemEditClick?.invoke(it)
                }
                holder.onItemDeleteClick = {
                    onItemDeleteClick?.invoke(it)
                }
            }
            is CharacterNoteViewHolder -> {
                holder.bind(item as Note)
                holder.onItemEditClick = {
                    onNoteEditClick?.invoke(it)
                }
                holder.onItemDeleteClick = {
                    onNoteDeleteClick?.invoke(it)
                }
            }
            is CharacterPerkViewHolder -> holder.bind((item as Perk).perk)
            is CharacterPerkNoteViewHolder -> holder.bind((item as PerkNote).perkNote)
            else -> throw RuntimeException(holder.toString())
        }
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<*>) {
        super.onViewDetachedFromWindow(holder)

        //holder.unbind()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item) {
            is TextHeaderAdd -> CharacterViewType.HeaderAdd
            is TextHeader -> CharacterViewType.Header
            is Stats -> CharacterViewType.Stats
            is Item -> CharacterViewType.Item
            is Note -> CharacterViewType.Note
            is Perk -> CharacterViewType.Perk
            is PerkNote -> CharacterViewType.PerkNote
            else -> throw RuntimeException(position.toString())
        }.ordinal
    }

    fun getItem(position: Int): Any {
        return items[position]
    }
}

enum class CharacterViewType {
    Header,
    HeaderAdd,
    Stats,
    Item,
    Note,
    Perk,
    PerkNote
}

interface OnItemClick<T> {
    fun onItemClick(item: T)
}