package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import net.north101.android.ghplayertracker.data.ItemType
import net.north101.android.ghplayertracker.livedata.ItemLiveData

class CharacterItemViewHolder(itemView: View) : BaseViewHolder<ItemLiveData>(itemView) {
    val textView: TextView = itemView.findViewById(R.id.text)
    val iconView: ImageView = itemView.findViewById(R.id.icon)
    val deleteView: ImageView = itemView.findViewById(R.id.delete)

    var onItemEditClick: ((ItemLiveData) -> Unit)? = null
    var onItemDeleteClick: ((ItemLiveData) -> Unit)? = null

    init {
        itemView.setOnClickListener {
            onItemEditClick?.invoke(item!!)
        }
        deleteView.setOnClickListener {
            onItemDeleteClick?.invoke(item!!)
        }
    }

    val nameObserver: ((String) -> Unit) = {
        textView.text = it
    }
    val typeObserver: ((ItemType) -> Unit) = {
        iconView.setImageResource(when (it) {
            ItemType.Head -> R.drawable.icon_item_head
            ItemType.Body -> R.drawable.icon_item_body
            ItemType.Legs -> R.drawable.icon_item_legs
            ItemType.OneHand -> R.drawable.icon_item_one_hand
            ItemType.TwoHands -> R.drawable.icon_item_two_hands
            ItemType.Small -> R.drawable.icon_items_small
        })
    }

    override fun bind(item: ItemLiveData) {
        super.bind(item)

        item.name.observeForever(nameObserver)
        item.type.observeForever(typeObserver)
    }

    override fun unbind() {
        item?.name?.removeObserver(nameObserver)
        item?.type?.removeObserver(typeObserver)

        super.unbind()
    }

    companion object {
        var layout = R.layout.character_item_item

        fun inflate(parent: ViewGroup): CharacterItemViewHolder {
            return CharacterItemViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}