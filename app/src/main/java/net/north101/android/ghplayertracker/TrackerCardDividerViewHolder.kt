package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup

class TrackerCardDividerViewHolder(itemView: View) : BaseViewHolder<Any>(itemView) {
    companion object {
        var layout = R.layout.card_divider_view

        fun inflate(parent: ViewGroup): TrackerCardDividerViewHolder {
            return TrackerCardDividerViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
