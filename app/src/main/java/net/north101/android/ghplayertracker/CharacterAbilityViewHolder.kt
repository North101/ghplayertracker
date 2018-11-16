package net.north101.android.ghplayertracker

import android.arch.lifecycle.Observer
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import net.north101.android.ghplayertracker.data.Ability

class CharacterAbilityViewHolder(itemView: View) : BaseViewHolder<CharacterAdapter.AbilityItem>(itemView) {
    var indexView: TextView = itemView.findViewById(R.id.index)
    var textView: TextView = itemView.findViewById(R.id.text)
    val viewView: View = itemView.findViewById(R.id.view)

    var onItemEditClick: ((CharacterAdapter.AbilityItem) -> Unit)? = null
    var onItemViewClick: ((CharacterAdapter.AbilityItem) -> Unit)? = null

    init {
        itemView.setOnClickListener {
            if (item!!.ability.value == null) {
                onItemViewClick?.invoke(item!!)
            } else {
                onItemEditClick?.invoke(item!!)
            }
        }
        viewView.setOnClickListener {
            onItemViewClick?.invoke(item!!)
        }
    }

    val abilityObserver = Observer<Ability> {
        textView.text = if (item!!.ability.value != null) {
            item!!.ability.value!!.name
        } else {
            ""
        }
        viewView.visibility = if (item!!.ability.value != null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun bind(item: CharacterAdapter.AbilityItem) {
        super.bind(item)

        indexView.text = item.level.toString() + "."
        item.ability.observeForever(abilityObserver)
        textView.text = if (item.ability.value != null) {
            item.ability.value!!.name
        } else {
            ""
        }
    }

    override fun unbind() {
        item?.ability?.removeObserver(abilityObserver)

        super.unbind()
    }

    companion object {
        var layout = R.layout.character_ability_item

        fun inflate(parent: ViewGroup): CharacterAbilityViewHolder {
            return CharacterAbilityViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
