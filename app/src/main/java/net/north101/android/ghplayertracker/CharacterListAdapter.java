package net.north101.android.ghplayertracker;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import net.north101.android.ghplayertracker.data.SelectableCharacter;

import java.util.ArrayList;
import java.util.List;

import static net.north101.android.ghplayertracker.BaseViewHolder.ClickListener;

public class CharacterListAdapter extends RecyclerView.Adapter<CharacterViewHolder> {
    public final List<SelectableCharacter> items;
    private ClickListener<SelectableCharacter> listener;

    public CharacterListAdapter(ArrayList<SelectableCharacter> items) {
        this.items = items;
        setHasStableIds(true);
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CharacterViewHolder.inflate(parent);
    }

    @Override
    public void onBindViewHolder(CharacterViewHolder holder, int position) {
        holder.bind(items.get(position));
        holder.setOnItemClickListener(this.onClickListener);
    }

    @Override
    public void onViewRecycled(CharacterViewHolder holder) {
        super.onViewRecycled(holder);

        holder.unbind();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).character.getId().hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    ClickListener<SelectableCharacter> onClickListener = new ClickListener<SelectableCharacter>() {
        @Override
        public void onItemClick(BaseViewHolder<SelectableCharacter> item) {
            listener.onItemClick(item);
        }

        @Override
        public boolean onItemLongClick(BaseViewHolder<SelectableCharacter> item) {
            return listener.onItemLongClick(item);
        }
    };

    public void setOnClickListener(ClickListener<SelectableCharacter> listener) {
        this.listener = listener;
    }
}
