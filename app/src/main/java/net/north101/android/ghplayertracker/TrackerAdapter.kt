package net.north101.android.ghplayertracker

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.north101.android.ghplayertracker.livedata.SummonLiveData
import net.north101.android.ghplayertracker.livedata.TrackerLiveData

class TrackerAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {
    private val items = ArrayList<RecyclerItemCompare>()
    lateinit var display: DisplayItems

    var onNumberClick: ((String) -> Unit)? = null
    var onSummonAddClick: (() -> Unit)? = null
    var onSummonDeleteClick: ((SummonLiveData) -> Unit)? = null

    enum class DisplayItems(val id: Int) {
        None(1),
        Left(2),
        Right(4),
        Both(Left.id or Right.id)
    }

    data class Stats(val tracker: TrackerLiveData) : RecyclerItemCompare {
        override val compareItemId: String
            get() = tracker.character.id.toString()
    }

    data class Status(val tracker: TrackerLiveData) : RecyclerItemCompare {
        override val compareItemId: String
            get() = tracker.character.id.toString()
    }

    data class Deck(val tracker: TrackerLiveData) : RecyclerItemCompare {
        override val compareItemId: String
            get() = tracker.character.id.toString()
    }

    fun updateItems(tracker: TrackerLiveData) {
        val newItems = ArrayList<RecyclerItemCompare>()

        if ((display.id and DisplayItems.Left.id) == DisplayItems.Left.id) {
            newItems.add(TextHeader("Stats"))
            newItems.add(Stats(tracker))
            newItems.add(Status(tracker))

            newItems.add(TextHeaderIcon("Summons", {
                onSummonAddClick?.invoke()
            }))
            newItems.addAll(tracker.summons.value)
        }

        if ((display.id and DisplayItems.Right.id) == DisplayItems.Right.id) {
            newItems.add(TextHeader("Attack Deck"))
            newItems.add(Deck(tracker))

            var wasShuffled: Int? = null
            for ((index, item) in tracker.playedCards.value.withIndex().reversed()) {
                if (wasShuffled != item.shuffledIndex) {
                    newItems.add(TextHeader("Shuffle #" + item.shuffledIndex.toString()))
                    wasShuffled = item.shuffledIndex
                }
                if (newItems.size > 0) {
                    newItems.add(CardDivider(index.toString()))
                }
                if (item.pile2 == null) {
                    for (card in item.pile1) {
                        newItems.add(CardInfo(item, card, false, item.shuffledIndex))
                    }
                } else {
                    val count = Math.max(item.pile1.size, item.pile2.size)
                    for (i in 0 until count) {
                        if (i < item.pile1.size) {
                            newItems.add(CardInfo(item, item.pile1[i], true, item.shuffledIndex))
                        } else {
                            newItems.add(CardInfo(item, null, true, item.shuffledIndex))
                        }
                        if (i < item.pile2.size) {
                            newItems.add(CardInfo(item, item.pile2[i], true, item.shuffledIndex))
                        } else {
                            newItems.add(CardInfo(item, null, true, item.shuffledIndex))
                        }
                    }
                }
            }
        }

        val diffCallback = RecyclerListItemsCallback(this.items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.items.clear()
        this.items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (TrackerViewType.values()[viewType]) {
            TrackerViewType.Header -> TextHeaderViewHolder.inflate(parent)
            TrackerViewType.HeaderAdd -> TextHeaderIconViewHolder.inflate(parent)
            TrackerViewType.Stats -> TrackerStatsViewHolder.inflate(parent)
            TrackerViewType.Status -> TrackerStatusViewHolder.inflate(parent)
            TrackerViewType.Summon -> TrackerSummonViewHolder.inflate(parent)
            TrackerViewType.Deck -> TrackerDeckViewHolder.inflate(parent)
            TrackerViewType.CardInfo -> TrackerCardViewHolder.inflate(parent)
            TrackerViewType.CardDivider -> TrackerCardDividerViewHolder.inflate(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = items[position]
        when (holder) {
            is TextHeaderViewHolder -> holder.bind(item as TextHeader)
            is TextHeaderIconViewHolder -> holder.bind(item as TextHeaderIcon)
            is TrackerStatsViewHolder -> {
                holder.bind((item as Stats).tracker)
                holder.onNumberClickListener = {
                    onNumberClick?.invoke(it)
                }
            }
            is TrackerStatusViewHolder -> holder.bind((item as Status).tracker)
            is TrackerSummonViewHolder -> {
                holder.bind(item as SummonLiveData)
                holder.onSummonDeleteClick = {
                    onSummonDeleteClick?.invoke(it)
                }
            }
            is TrackerDeckViewHolder -> {
                holder.bind((item as Deck).tracker)
                holder.onNumberClickListener = {
                    onNumberClick?.invoke(it)
                }
            }
            is TrackerCardViewHolder -> holder.bind(item as CardInfo)
            is TrackerCardDividerViewHolder -> holder.bind(item as CardDivider)
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
            is TextHeaderIcon -> TrackerViewType.HeaderAdd
            is TextHeader -> TrackerViewType.Header
            is Stats -> TrackerViewType.Stats
            is Status -> TrackerViewType.Status
            is SummonLiveData -> TrackerViewType.Summon
            is Deck -> TrackerViewType.Deck
            is CardInfo -> TrackerViewType.CardInfo
            is CardDivider -> TrackerViewType.CardDivider
            else -> throw RuntimeException(position.toString())
        }.ordinal
    }

    fun getItem(position: Int): Any {
        return items[position]
    }
}

enum class TrackerViewType {
    Header,
    HeaderAdd,
    Stats,
    Status,
    Summon,
    Deck,
    CardInfo,
    CardDivider
}