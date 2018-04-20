package net.north101.android.ghplayertracker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import net.north101.android.ghplayertracker.data.CharacterPerk

class PerkViewHolder(itemView: View) : BaseViewHolder<CharacterPerk>(itemView) {
    var perk1: CheckBox = itemView.findViewById(R.id.perk1)
    var perk2: CheckBox = itemView.findViewById(R.id.perk2)
    var perk3: CheckBox = itemView.findViewById(R.id.perk3)
    var textView: TextView = itemView.findViewById(R.id.textView)

    fun setText(text: String) {
        val ssb = SpannableStringBuilder()

        val context = itemView.context
        val res = context.resources
        var index = 0
        var offset = 0
        while (index + offset < text.length) {
            val start = text.indexOf("[[", index + offset)
            if (start == -1) break

            val end = text.indexOf("]]", start) + 2
            if (end == -1) break

            val icon = text.substring(start + 2, end - 2)

            val iconId = res.getIdentifier(icon, "drawable", context.packageName)
            if (iconId == 0) {
                offset++
            } else {
                ssb.append(SpannableString(text.substring(index, start)))
                val image = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, iconId), 50, 50, false)
                ssb.append(text.substring(start, end), ImageSpan(context, image, ImageSpan.ALIGN_BOTTOM), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                index = end
                offset = 0
            }
        }
        if (index < text.length) {
            ssb.append(SpannableString(text.substring(index)))
        }

        textView.setText(ssb, TextView.BufferType.SPANNABLE)
    }

    override fun bind(item: CharacterPerk) {
        super.bind(item)

        perk2.visibility = if (item.perk.ticks >= 2) View.VISIBLE else View.GONE
        perk3.visibility = if (item.perk.ticks >= 3) View.VISIBLE else View.GONE

        setText(item.perk.text)
        updatePerks()
    }

    fun updatePerks() {
        perk1.isChecked = item!!.ticks >= 1
        perk2.isChecked = item!!.ticks >= 2
        perk3.isChecked = item!!.ticks >= 3
    }

    override fun onClick(view: View) {
        if (this.item!!.ticks == this.item!!.perk.ticks) {
            this.item!!.ticks = 0
        } else {
            this.item!!.ticks += 1
        }
        updatePerks()

        super.onClick(view)
    }

    companion object {
        var layout = R.layout.perk_item_view

        fun inflate(parent: ViewGroup): PerkViewHolder {
            return PerkViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
