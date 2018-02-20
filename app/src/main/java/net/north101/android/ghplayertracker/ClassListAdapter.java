package net.north101.android.ghplayertracker;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import net.north101.android.ghplayertracker.data.ClassList;

import static net.north101.android.ghplayertracker.ClassViewHolder.*;

public class ClassListAdapter extends RecyclerView.Adapter<ClassViewHolder> {
    public final List<String> items;
    private ClickListener<String> listener;

    public ClassListAdapter(ClassList items) {
        this.items = items.classList;
    }

    @Override
    public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ClassViewHolder.inflate(parent);
    }

    @Override
    public void onBindViewHolder(ClassViewHolder holder, int position) {
        holder.bind(items.get(position));
        holder.setOnItemClickListener(this.onClickListener);
    }

    @Override
    public void onViewRecycled(ClassViewHolder holder) {
        super.onViewRecycled(holder);

        holder.unbind();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    ClickListener<String> onClickListener = new ClickListener<String>() {
        @Override
        public void onItemClick(BaseViewHolder<String> item) {
            listener.onItemClick(item);
        }

        @Override
        public boolean onItemLongClick(BaseViewHolder<String> item) {
            return listener.onItemLongClick(item);
        }
    };

    public void setOnClickListener(ClickListener<String> listener) {
        this.listener = listener;
    }
}
