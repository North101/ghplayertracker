package net.north101.android.ghplayertracker

import net.north101.android.ghplayertracker.data.Card
import net.north101.android.ghplayertracker.data.PlayedCards

class CardInfo(var playedCards: PlayedCards, val card: Card?, val split: Boolean, var shuffled: Int?) : RecyclerItemCompare {
    override val compareItemId: String
        get() {
            return playedCards.compareItemId + ":" + card?.id
        }
}