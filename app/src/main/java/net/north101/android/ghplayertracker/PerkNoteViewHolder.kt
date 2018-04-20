package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox

import net.north101.android.ghplayertracker.data.PerkNote

class PerkNoteViewHolder(itemView: View) : BaseViewHolder<PerkNote>(itemView) {
    var perk1View: CheckBox = itemView.findViewById(R.id.perk1)
    var perk2View: CheckBox = itemView.findViewById(R.id.perk2)
    var perk3View: CheckBox = itemView.findViewById(R.id.perk3)

    override fun bind(item: PerkNote) {
        super.bind(item)

        updatePerks()
    }

    fun updatePerks() {
        val ticks = item!!.ticks
        perk1View.isChecked = ticks >= 1
        perk2View.isChecked = ticks >= 2
        perk3View.isChecked = ticks >= 3
    }

    override fun onClick(view: View) {
        val ticks = item!!.ticks
        if (ticks == PerkNote.MAX_TICKS) {
            this.item!!.ticks = 0
        } else {
            this.item!!.ticks = ticks + 1
        }
        updatePerks()

        super.onClick(view)
    }

    companion object {
        var layout = R.layout.perk_note_item_view

        fun inflate(parent: ViewGroup): PerkNoteViewHolder {
            return PerkNoteViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
