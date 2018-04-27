package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class TextHeaderAddViewHolder(itemView: View) : BaseViewHolder<TextHeaderAdd>(itemView) {
    var textView: TextView = itemView.findViewById(R.id.text)
    var addView: View = itemView.findViewById(R.id.add)

    override fun bind(item: TextHeaderAdd) {
        super.bind(item)

        this.textView.text = item.text
        addView.setOnClickListener {
            item.onItemAddClick.invoke()
        }
    }

    companion object {
        var layout = R.layout.header_add_view

        fun inflate(parent: ViewGroup): TextHeaderAddViewHolder {
            return TextHeaderAddViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}


interface OnItemAddClick {
    fun onItemAddClick()
}