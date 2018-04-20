package net.north101.android.ghplayertracker

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import net.north101.android.ghplayertracker.data.CharacterPerk

class PerkAdapter(val items: List<CharacterPerk>) : RecyclerView.Adapter<PerkViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerkViewHolder {
        return PerkViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: PerkViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
