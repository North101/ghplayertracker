package net.north101.android.ghplayertracker

import android.arch.lifecycle.MutableLiveData
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.north101.android.ghplayertracker.data.Ability
import net.north101.android.ghplayertracker.data.Item
import net.north101.android.ghplayertracker.livedata.CharacterLiveData
import net.north101.android.ghplayertracker.livedata.InitLiveData
import net.north101.android.ghplayertracker.livedata.PerkLiveData
import net.north101.android.ghplayertracker.livedata.PerkNoteLiveData

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

    data class Note(val index: Int, val note: InitLiveData<String>) : RecyclerItemCompare {
        override val compareItemId: String
            get() = note.value.toString()
    }

    data class AbilityItem(val index: Int, val ability: MutableLiveData<Ability>) : RecyclerItemCompare {
        val level = index + 2

        override val compareItemId: String
            get() = index.toString()
    }

    data class Perk(val perk: PerkLiveData) : RecyclerItemCompare {
        override val compareItemId: String
            get() = perk.perk.text
    }

    data class PerkNote(val index: Int, val perkNote: PerkNoteLiveData) : RecyclerItemCompare {
        override val compareItemId: String
            get() = index.toString()
    }

    var onNumberEditClick: ((CharacterNumericValue) -> Unit)? = null

    var onAbilityGalleryClick: (() -> Unit)? = null
    var onAbilityEditClick: ((AbilityItem) -> Unit)? = null
    var onAbilityViewClick: ((AbilityItem) -> Unit)? = null

    var onItemAddClick: (() -> Unit)? = null
    var onItemViewClick: ((Item) -> Unit)? = null
    var onItemDeleteClick: ((Item) -> Unit)? = null

    var onNoteAddClick: (() -> Unit)? = null
    var onNoteEditClick: ((CharacterAdapter.Note) -> Unit)? = null
    var onNoteDeleteClick: ((CharacterAdapter.Note) -> Unit)? = null

    fun updateItems(character: CharacterLiveData) {
        val newItems = ArrayList<RecyclerItemCompare>()

        if ((display.id and DisplayItems.Left.id) == DisplayItems.Left.id) {
            newItems.add(TextHeader("Character"))
            newItems.add(Stats(character))

            newItems.add(TextHeaderIcon("Abilities", {
                onAbilityGalleryClick?.invoke()
            }, R.drawable.ic_view_comfy_black_24dp))
            newItems.addAll(
                character.abilities.value.withIndex()
                    .map { AbilityItem(it.index, it.value) }
                    .filter { it.level <= character.level.value }
            )

            newItems.add(TextHeaderIcon("Items", {
                onItemAddClick?.invoke()
            }))
            newItems.addAll(
                character.items.value
                    .also {
                        it.sortWith(compareBy({ it.type }, { it.name }))
                    }
                    .map { it }
            )

            newItems.add(TextHeaderIcon("Notes", {
                onNoteAddClick?.invoke()
            }))
            newItems.addAll(
                character.notes.value.withIndex().map { Note(it.index + 1, it.value) }
            )
        }

        if ((display.id and DisplayItems.Right.id) == DisplayItems.Right.id) {
            newItems.add(TextHeader("Perks"))
            newItems.addAll(
                character.perks.value.map { Perk(it) }
            )

            newItems.add(TextHeader("Perk Notes"))
            newItems.addAll(
                character.perkNotes.value.withIndex().map { PerkNote(it.index, it.value) }
            )
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
            CharacterViewType.HeaderAdd -> TextHeaderIconViewHolder.inflate(parent)
            CharacterViewType.Stats -> CharacterStatsViewHolder.inflate(parent)
            CharacterViewType.Item -> CharacterItemViewHolder.inflate(parent)
            CharacterViewType.Ability -> CharacterAbilityViewHolder.inflate(parent)
            CharacterViewType.Note -> CharacterNoteViewHolder.inflate(parent)
            CharacterViewType.Perk -> CharacterPerkViewHolder.inflate(parent)
            CharacterViewType.PerkNote -> CharacterPerkNoteViewHolder.inflate(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = items[position]
        when (holder) {
            is TextHeaderViewHolder -> holder.bind(item as TextHeader)
            is TextHeaderIconViewHolder -> holder.bind(item as TextHeaderIcon)
            is CharacterStatsViewHolder -> {
                holder.bind((item as Stats).character)
                holder.onNumberClick = {
                    onNumberEditClick?.invoke(it)
                }
            }
            is CharacterItemViewHolder -> {
                holder.bind(item as Item)
                holder.onItemViewClick = {
                    onItemViewClick?.invoke(it)
                }
                holder.onItemDeleteClick = {
                    onItemDeleteClick?.invoke(it)
                }
            }
            is CharacterAbilityViewHolder -> {
                holder.bind(item as AbilityItem)
                holder.onItemEditClick = {
                    onAbilityEditClick?.invoke(it)
                }
                holder.onItemViewClick = {
                    onAbilityViewClick?.invoke(it)
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

    override fun onViewRecycled(holder: BaseViewHolder<*>) {
        holder.unbind()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item) {
            is TextHeaderIcon -> CharacterViewType.HeaderAdd
            is TextHeader -> CharacterViewType.Header
            is Stats -> CharacterViewType.Stats
            is Item -> CharacterViewType.Item
            is AbilityItem -> CharacterViewType.Ability
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
    Ability,
    Note,
    Perk,
    PerkNote
}