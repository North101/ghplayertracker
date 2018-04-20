package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Parcelize
class Card(
        val id: String,
        val deckType: DeckType,
        val special: CardSpecial
) : Parcelable {
    init {
        Card.cache[id] = this
    }

    companion object {
        private val cache = HashMap<String, Card>()

        @Throws(JSONException::class)
        fun parse(cardId: String, data: JSONObject): Card {
            val card = cache[cardId]
            if (card != null)
                return card

            val deckType = DeckType.parse(data.getString("deck"))
            val special = CardSpecial.parse(data.optString("special", null))

            return Card(cardId, deckType, special)
        }

        operator fun get(cardId: String): Card {
            return cache[cardId] ?: throw RuntimeException(cardId)
        }
    }
}
