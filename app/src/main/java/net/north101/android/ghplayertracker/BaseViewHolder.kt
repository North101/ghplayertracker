package net.north101.android.ghplayertracker

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
    var item: T? = null
    private var listener: ClickListener<T>? = null

    init {
        this.itemView.setOnClickListener(this)
        this.itemView.setOnLongClickListener(this)
    }

    open fun bind(item: T) {
        this.item = item

    }

    fun unbind() {
        this.item = null
        this.listener = null
    }

    fun setOnItemClickListener(listener: ClickListener<T>) {
        this.listener = listener
    }

    override fun onClick(view: View) {
        if (this.listener == null) return
        this.listener!!.onItemClick(this)
    }

    override fun onLongClick(view: View): Boolean {
        if (this.listener == null) return false
        return this.listener!!.onItemLongClick(this)
    }

    open class ClickListener<T> {
        open fun onItemClick(holder: BaseViewHolder<T>) {}
        open fun onItemLongClick(holder: BaseViewHolder<T>): Boolean {
            return false
        }
    }

    companion object {

        fun inflate(parent: ViewGroup, layout: Int): View {
            return LayoutInflater.from(parent.context).inflate(layout, parent, false)
        }
    }
}
