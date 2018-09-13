package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

open class TextHeaderIconViewHolder(itemView: View) : BaseViewHolder<TextHeaderIcon>(itemView) {
    var textView: TextView = itemView.findViewById(R.id.text)
    var iconView: View = itemView.findViewById(R.id.icon)
    var iconImageView: ImageView = itemView.findViewById(R.id.icon_image)

    init {
        this.iconView.setOnClickListener {
            item?.onItemAddClick?.invoke()
        }
    }

    override fun bind(item: TextHeaderIcon) {
        super.bind(item)

        this.textView.text = item.text
        this.iconImageView.setImageResource(item.icon)
    }

    companion object {
        var layout = R.layout.header_icon_view

        fun inflate(parent: ViewGroup): TextHeaderIconViewHolder {
            return TextHeaderIconViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}


interface OnItemAddClick {
    fun onItemAddClick()
}