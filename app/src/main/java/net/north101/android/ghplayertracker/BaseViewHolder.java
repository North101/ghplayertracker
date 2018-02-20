package net.north101.android.ghplayertracker;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    public T item;
    private ClickListener<T> listener;

    public static View inflate(ViewGroup parent, int layout) {
        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }

    public BaseViewHolder(View itemView) {
        super(itemView);
        this.itemView.setOnClickListener(this);
        this.itemView.setOnLongClickListener(this);
    }

    public void bind(T item) {
        this.item = item;

    }

    public void unbind() {
        this.item = null;
        this.listener = null;
    }

    public void setOnItemClickListener(ClickListener<T> listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (this.listener == null) return;
        this.listener.onItemClick(this);
    }

    @Override
    public boolean onLongClick(View view) {
        if (this.listener == null) return false;
        Log.d("test", "longclick");
        return this.listener.onItemLongClick(this);
    }

    public static class ClickListener<T> {
        public void onItemClick(BaseViewHolder<T> holder) {}
        public boolean onItemLongClick(BaseViewHolder<T> holder) {
            return false;
        }
    }
}
