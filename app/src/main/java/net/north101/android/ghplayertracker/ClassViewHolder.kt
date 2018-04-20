package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import net.north101.android.ghplayertracker.data.CharacterClass

class ClassViewHolder(view: View) : BaseViewHolder<CharacterClass>(view) {
    private val imageView: ImageView = view.findViewById(R.id.class_icon)

    override fun bind(item: CharacterClass) {
        super.bind(item)

        this.imageView.setImageResource(Util.getImageResource(imageView.context, item.id))
    }

    companion object {
        var layout = R.layout.class_item_view

        fun inflate(parent: ViewGroup): ClassViewHolder {
            return ClassViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
