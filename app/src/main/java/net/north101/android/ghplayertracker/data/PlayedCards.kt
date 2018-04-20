package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.RecyclerItemCompare
import java.util.*

@Parcelize
data class PlayedCards(
        val pile1: ArrayList<Card>,
        val pile2: ArrayList<Card>?,
        var shuffledIndex: Int?
) : Parcelable, RecyclerItemCompare {
    override val compareItemId: String
        get() {
            val id1 = pile1.joinToString(",", "[", "]") { it.id }
            val id2 = pile2?.joinToString(",", "[", "]") { it.id } ?: ""
            return id1 + id2
        }
}
