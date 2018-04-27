package net.north101.android.ghplayertracker

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

class PerkNoteAdapter : RecyclerView.Adapter<CharacterPerkNoteViewHolder>() {
    val items = ArrayList<PerkNoteData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterPerkNoteViewHolder {
        return CharacterPerkNoteViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: CharacterPerkNoteViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: List<PerkNoteData>) {
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }
}
