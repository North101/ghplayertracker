package net.north101.android.ghplayertracker

import android.arch.lifecycle.Observer
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class CharacterNoteViewHolder(itemView: View) : BaseViewHolder<CharacterAdapter.Note>(itemView) {
    var textView: TextView = itemView.findViewById(R.id.text)
    val deleteView: ImageView = itemView.findViewById(R.id.delete)

    var onItemEditClick: ((CharacterAdapter.Note) -> Unit)? = null
    var onItemDeleteClick: ((CharacterAdapter.Note) -> Unit)? = null

    init {
        itemView.setOnClickListener {
            onItemEditClick?.invoke(item!!)
        }
        deleteView.setOnClickListener {
            onItemDeleteClick?.invoke(item!!)
        }
    }

    val noteObserver = Observer<String> {
        it?.let {
            textView.text = it
        }
    }

    override fun bind(item: CharacterAdapter.Note) {
        super.bind(item)

        item.note.observeForever(noteObserver)
    }

    override fun unbind() {
        item?.note?.removeObserver(noteObserver)

        super.unbind()
    }

    companion object {
        var layout = R.layout.character_note_item

        fun inflate(parent: ViewGroup): CharacterNoteViewHolder {
            return CharacterNoteViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
