package net.north101.android.ghplayertracker

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import net.north101.android.ghplayertracker.data.PerkNote

class PerkNoteAdapter(
        val items: ArrayList<PerkNote>
) : RecyclerView.Adapter<PerkNoteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerkNoteViewHolder {
        return PerkNoteViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: PerkNoteViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
