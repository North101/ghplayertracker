package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class HeaderViewHolder(itemView: View) : BaseViewHolder<TextHeader>(itemView) {
    var textView: TextView = itemView.findViewById(R.id.text)

    override fun bind(item: TextHeader) {
        super.bind(item)

        this.textView.text = item.text
    }

    companion object {
        var layout = R.layout.card_header_view

        fun inflate(parent: ViewGroup): HeaderViewHolder {
            return HeaderViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
