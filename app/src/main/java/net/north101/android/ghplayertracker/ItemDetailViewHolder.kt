package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import net.north101.android.ghplayertracker.data.Item
import net.north101.android.ghplayertracker.data.ItemType

class ItemDetailViewHolder(itemView: View) : BaseViewHolder<Item>(itemView) {
    val iconView: ImageView = itemView.findViewById(R.id.icon)
    val textView: TextView = itemView.findViewById(R.id.text)
    val idView: TextView = itemView.findViewById(R.id.id)
    val priceView: TextView = itemView.findViewById(R.id.price)
    val imageView: View = itemView.findViewById(R.id.image)

    var onItemViewClick: ((Item) -> Unit)? = null
    var onItemImageClick: ((Item) -> Unit)? = null

    init {
        itemView.setOnClickListener {
            onItemViewClick?.invoke(item!!)
        }
        imageView.setOnClickListener {
            onItemImageClick?.invoke(item!!)
        }
    }

    override fun bind(item: Item) {
        super.bind(item)

        iconView.setImageResource(when (item.type) {
            ItemType.Head -> R.drawable.icon_item_head
            ItemType.Body -> R.drawable.icon_item_body
            ItemType.Legs -> R.drawable.icon_item_legs
            ItemType.OneHand -> R.drawable.icon_item_one_hand
            ItemType.TwoHands -> R.drawable.icon_item_two_hands
            ItemType.Small -> R.drawable.icon_item_small
        })
        textView.text = item.name
        idView.text = item.itemId
        priceView.text = item.price.toString() + "g"
    }

    fun setEnabled(isEnabled: Boolean) {
        itemView.isEnabled = isEnabled
    }

    companion object {
        var layout = R.layout.item_detail_item

        fun inflate(parent: ViewGroup): ItemDetailViewHolder {
            return ItemDetailViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}