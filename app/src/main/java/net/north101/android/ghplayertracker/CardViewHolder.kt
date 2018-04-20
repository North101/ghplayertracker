package net.north101.android.ghplayertracker

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class CardViewHolder(itemView: View) : BaseViewHolder<PlayedCardsAdapter.CardInfo>(itemView) {
    var cardView: ImageView = itemView.findViewById(R.id.card)

    override fun bind(item: PlayedCardsAdapter.CardInfo) {
        super.bind(item)

        if (item.card == null) {
            cardView.setImageResource(0)
        } else {
            cardView.setImageResource(Util.getImageResource(itemView.context, item.card.id))
        }
        if (item.shuffled != null) {
            val matrix = ColorMatrix()
            matrix.setSaturation(0.25f)  //0 means grayscale
            val cf = ColorMatrixColorFilter(matrix)
            cardView.colorFilter = cf
        } else {
            cardView.colorFilter = null
        }
    }

    companion object {
        var layout = R.layout.card_item_view

        fun inflate(parent: ViewGroup): CardViewHolder {
            return CardViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
