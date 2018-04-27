package net.north101.android.ghplayertracker

import android.arch.lifecycle.Observer
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox

class CharacterPerkNoteViewHolder(itemView: View) : BaseViewHolder<PerkNoteData>(itemView) {
    var perk1View: CheckBox = itemView.findViewById(R.id.perk1)
    var perk2View: CheckBox = itemView.findViewById(R.id.perk2)
    var perk3View: CheckBox = itemView.findViewById(R.id.perk3)

    val tickObserver = Observer<Int> {
        it?.let {
            perk1View.isChecked = it >= 1
            perk2View.isChecked = it >= 2
            perk3View.isChecked = it >= 3
        }
    }

    override fun bind(item: PerkNoteData) {
        super.bind(item)

        item.observeForever(tickObserver)
    }

    override fun unbind() {
        item?.removeObserver(tickObserver)

        super.unbind()
    }

    override fun onClick(view: View) {
        val ticks = item!!.value ?: 0
        if (ticks == 3) {
            this.item!!.value = 0
        } else {
            this.item!!.value = ticks + 1
        }

        super.onClick(view)
    }

    companion object {
        var layout = R.layout.character_perk_note_item

        fun inflate(parent: ViewGroup): CharacterPerkNoteViewHolder {
            return CharacterPerkNoteViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
