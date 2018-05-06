package net.north101.android.ghplayertracker.data

import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.Util
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

@Parcelize
data class BasicCards(
    val basicDeck: HashMap<String, Int>
) : Parcelable {
    init {
        BasicCards.instance = this
    }

    companion object {
        private var instance: BasicCards? = null

        @Throws(IOException::class, JSONException::class)
        fun load(context: Context): BasicCards {
            if (instance != null)
                return instance!!

            val inputStream = context.assets.open("basic_cards.json")
            return BasicCards.parse(JSONObject(Util.readInputString(inputStream)))
        }

        @Throws(JSONException::class)
        fun parse(data: JSONObject): BasicCards {
            val cardsData = data.getJSONObject("cards")
            for (cardId in cardsData.keys()) {
                Card.parse(cardId, cardsData.getJSONObject(cardId))
            }

            val basicDeck = HashMap<String, Int>()
            val basicData = data.getJSONArray("basic")
            for (i in 0 until basicData.length()) {
                val basicCardData = basicData.getJSONObject(i)

                val cardId = basicCardData.getString("card_id")
                val count = basicCardData.optInt("count", 1)
                basicDeck[cardId] = count
            }

            return BasicCards(basicDeck)
        }
    }
}
