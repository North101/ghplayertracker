package net.north101.android.ghplayertracker

import android.os.Parcelable
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.data.Item
import net.north101.android.ghplayertracker.data.ItemCategory
import net.north101.android.ghplayertracker.data.ItemType
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ItemListAdapter(
    var itemViewType: ItemView,
    var itemOptions: ItemOptions
) : RecyclerView.Adapter<BaseViewHolder<*>>(), Filterable {
    var shopItems: Map<String, Item> = HashMap()
    var itemCategories: List<ItemCategory> = ArrayList()
    var ownedItemList: ArrayList<Item> = ArrayList()

    val items = ArrayList<RecyclerItemCompare>()
    val filter = ItemFilter(this)

    var onItemViewClick: ((Item) -> Unit)? = null
    var onItemImageClick: ((Item) -> Unit)? = null

    fun updateItems(shopItems: Map<String, Item>, itemCategories: List<ItemCategory>, ownedItemList: ArrayList<Item>) {
        this.shopItems = shopItems
        this.itemCategories = itemCategories
        this.ownedItemList = ownedItemList

        refresh()
    }

    fun refresh() {
        getFilter().filter("")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            HEADER_VIEW_TYPE -> TextHeaderViewHolder.inflate(parent)
            ITEM_DETAIL_VIEW_TYPE -> ItemDetailViewHolder.inflate(parent)
        //ITEM_IMAGE_VIEW_TYPE -> ItemImageViewHolder.inflate(parent)
            else -> throw RuntimeException(viewType.toString())
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = items[position]
        when (holder) {
            is TextHeaderViewHolder -> holder.bind(item as TextHeader)
            is ItemDetailViewHolder -> {
                holder.bind(item as Item)
                holder.setEnabled(!ownedItemList.contains(item))
                holder.onItemViewClick = {
                    onItemViewClick?.invoke(it)
                }
                holder.onItemImageClick = {
                    onItemImageClick?.invoke(it)
                }
            }
        /*is ItemImageViewHolder -> {
            holder.bind(item as Item)
            holder.setOnItemClickListener(this.onItemClickListener)
        }*/
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
            is Item -> {
                when (itemViewType) {
                    ItemView.Detail -> ITEM_DETAIL_VIEW_TYPE
                //ItemView.Image -> ITEM_IMAGE_VIEW_TYPE
                }
            }
            else -> throw RuntimeException(position.toString())
        }
    }

    override fun getFilter(): Filter {
        return filter
    }

    companion object {
        const val HEADER_VIEW_TYPE = 1
        const val ITEM_DETAIL_VIEW_TYPE = 2
        const val ITEM_IMAGE_VIEW_TYPE = 3
    }
}

enum class ItemView {
    Detail,
    //Image,
}

enum class ItemSort {
    Id,
    Name,
    ItemType,
    Price,
}

@Parcelize
class ItemOptions(
    var text: String,
    var itemCategories: HashSet<ItemCategory>,
    var itemTypes: HashSet<ItemType>,
    var onlyUnlocked: Boolean,
    var sort: ItemSort
) : Parcelable {
    fun match(item: Item): Boolean {
        if (text.isNotEmpty() && !item.name.contains(text, true) && !item.itemId.contains(text, true)) {
            return false
        } else if (!itemCategories.isEmpty() && !itemCategories.contains(item.category)) {
            return false
        } else if (!itemTypes.isEmpty() && !itemTypes.contains(item.type)) {
            return false
        } else if (onlyUnlocked && !item.unlocked) {
            return false
        }

        return true
    }
}

class ItemFilter(
    val listAdapter: ItemListAdapter
) : Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val sorter = when (listAdapter.itemOptions.sort) {
            ItemSort.Id -> compareBy<Item>({ listAdapter.itemCategories.indexOf(it.category) }, { it.id })
            ItemSort.Name -> compareBy({ it.name })
            ItemSort.ItemType -> compareBy({ it.type }, { it.name })
            ItemSort.Price -> compareBy({ it.price }, { it.type }, { it.name })
        }
        val filteredItems = listAdapter.shopItems.values.filter {
            listAdapter.itemOptions.match(it) && !listAdapter.ownedItemList.contains(it)
        }.sortedWith(sorter)

        val ownedItemList = listAdapter.ownedItemList.filter {
            listAdapter.itemOptions.match(it)
        }
        val newItems = ArrayList<RecyclerItemCompare>()
        if (ownedItemList.isNotEmpty()) {
            newItems.add(TextHeader("Owned"))
            newItems.addAll(ownedItemList.sortedWith(sorter))
        }
        when (listAdapter.itemOptions.sort) {
            ItemSort.Id -> {
                var lastCategory: ItemCategory? = null
                for (item in filteredItems) {
                    if (lastCategory == null || item.category != lastCategory) {
                        lastCategory = item.category
                        newItems.add(TextHeader(lastCategory.name))
                    }
                    newItems.add(item)
                }
            }
            ItemSort.Name -> {
                var lastLetter: Char? = null
                for (item in filteredItems) {
                    if (lastLetter == null || item.name[0] != lastLetter) {
                        lastLetter = item.name[0]
                        newItems.add(TextHeader(lastLetter.toString()))
                    }
                    newItems.add(item)
                }
            }
            ItemSort.ItemType -> {
                var lastType: ItemType? = null
                for (item in filteredItems) {
                    if (lastType == null || item.type != lastType) {
                        lastType = item.type
                        newItems.add(TextHeader(lastType.name))
                    }
                    newItems.add(item)
                }
            }
            ItemSort.Price -> {
                var lastPrice: Int? = null
                for (item in filteredItems) {
                    if (lastPrice == null || (item.price / 10) != lastPrice) {
                        lastPrice = item.price / 10
                        newItems.add(TextHeader((lastPrice * 10).toString()))
                    }
                    newItems.add(item)
                }
            }
        }

        val filterResults = Filter.FilterResults()
        filterResults.count = newItems.size
        filterResults.values = newItems

        return filterResults
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        val newItems = results?.values as List<RecyclerItemCompare>? ?: ArrayList()

        val diffCallback = RecyclerListItemsCallback(listAdapter.items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        listAdapter.items.clear()
        listAdapter.items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this.listAdapter)
    }
}