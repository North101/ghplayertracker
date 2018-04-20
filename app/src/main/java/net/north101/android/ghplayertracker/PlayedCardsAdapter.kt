package net.north101.android.ghplayertracker

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.north101.android.ghplayertracker.data.Card
import net.north101.android.ghplayertracker.data.PlayedCards
import java.util.*

class PlayedCardsAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {
    private val items = ArrayList<RecyclerItemCompare>()

    class CardDivider(override val compareItemId: String) : RecyclerItemCompare

    inner class CardInfo(val card: Card?, val split: Boolean, var shuffled: Boolean) : RecyclerItemCompare {
        override val compareItemId: String
            get() = card?.id ?: ""
    }

    fun updateItems(items: ArrayList<PlayedCards>) {
        var wasShuffled = false;
        val newItems = ArrayList<RecyclerItemCompare>()
        for ((index, item) in items.withIndex()) {
            if (!wasShuffled && item.shuffled) {
                newItems.add(shuffledHeader)
                wasShuffled = true
            }
            if (newItems.size > 0) {
                newItems.add(CardDivider((items.size - index).toString()))
            }
            if (item.pile2 == null) {
                for (card in item.pile1) {
                    newItems.add(CardInfo(card, false, item.shuffled))
                }
            } else {
                val count = Math.max(item.pile1.size, item.pile2.size)
                for (i in 0 until count) {
                    if (i < item.pile1.size) {
                        newItems.add(CardInfo(item.pile1[i], true, item.shuffled))
                    } else {
                        newItems.add(CardInfo(null, true, item.shuffled))
                    }
                    if (i < item.pile2.size) {
                        newItems.add(CardInfo(item.pile2[i], true, item.shuffled))
                    } else {
                        newItems.add(CardInfo(null, true, item.shuffled))
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
        return when (viewType) {
            1 -> CardViewHolder.inflate(parent)
            2 -> CardDividerViewHolder.inflate(parent)
            3 -> HeaderViewHolder.inflate(parent)
            else -> throw RuntimeException()
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = getItem(position)
        when (item) {
            is CardInfo -> (holder as CardViewHolder).bind(item)
            is CardDivider -> (holder as CardDividerViewHolder).bind(item)
            is TextHeader -> (holder as HeaderViewHolder).bind(item)
            else -> throw RuntimeException(holder.toString())
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is CardInfo -> 1
            is CardDivider -> 2
            is TextHeader -> 3
            else -> throw RuntimeException()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int): Any {
        return items[position]
    }

    companion object {
        var shuffledHeader = TextHeader("Shuffled")
    }
}
