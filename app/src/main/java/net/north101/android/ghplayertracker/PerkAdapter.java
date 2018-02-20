package net.north101.android.ghplayertracker;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import net.north101.android.ghplayertracker.data.CharacterPerk;

public class PerkAdapter extends RecyclerView.Adapter<PerkViewHolder> {
    public final List<CharacterPerk> items;

    public PerkAdapter(List<CharacterPerk> items) {
        this.items = items;
    }

    @Override
    public PerkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return PerkViewHolder.inflate(parent);
    }

    @Override
    public void onBindViewHolder(PerkViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
