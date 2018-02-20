package net.north101.android.ghplayertracker;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import net.north101.android.ghplayertracker.data.PerkNote;

public class PerkNoteAdapter extends RecyclerView.Adapter<PerkNoteViewHolder> {
    public final PerkNote[] items;

    public PerkNoteAdapter(PerkNote[] items) {
        this.items = items;
    }

    @Override
    public PerkNoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return PerkNoteViewHolder.inflate(parent);
    }

    @Override
    public void onBindViewHolder(PerkNoteViewHolder holder, int position) {
        holder.bind(items[position]);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }
}
