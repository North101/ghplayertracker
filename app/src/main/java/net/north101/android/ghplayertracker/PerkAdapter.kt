package net.north101.android.ghplayertracker

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

class PerkAdapter : RecyclerView.Adapter<CharacterPerkViewHolder>() {
    val items = ArrayList<PerkData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterPerkViewHolder {
        return CharacterPerkViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: CharacterPerkViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: List<PerkData>) {
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }
}
