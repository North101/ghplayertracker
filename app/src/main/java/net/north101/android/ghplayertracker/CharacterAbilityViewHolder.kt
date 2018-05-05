package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class CharacterAbilityViewHolder(itemView: View) : BaseViewHolder<CharacterAdapter.Ability>(itemView) {
    var indexView: TextView = itemView.findViewById(R.id.index)
    var textView: TextView = itemView.findViewById(R.id.text)
    val deleteView: ImageView = itemView.findViewById(R.id.delete)

    var onItemEditClick: ((CharacterAdapter.Ability) -> Unit)? = null
    var onItemDeleteClick: ((CharacterAdapter.Ability) -> Unit)? = null

    init {
        itemView.setOnClickListener {
            onItemEditClick?.invoke(item!!)
        }
        deleteView.setOnClickListener {
            onItemDeleteClick?.invoke(item!!)
        }
    }

    val noteObserver: ((String) -> Unit) = {
        textView.text = it
    }

    override fun bind(item: CharacterAdapter.Ability) {
        super.bind(item)

        indexView.text = item.level.toString() + "."
        item.ability.observeForever(noteObserver)
    }

    override fun unbind() {
        item?.ability?.removeObserver(noteObserver)

        super.unbind()
    }

    companion object {
        var layout = R.layout.character_note_item

        fun inflate(parent: ViewGroup): CharacterAbilityViewHolder {
            return CharacterAbilityViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
