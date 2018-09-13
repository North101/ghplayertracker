package net.north101.android.ghplayertracker.data

import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.Util
import net.north101.android.ghplayertracker.forEach
import net.north101.android.ghplayertracker.map
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
            cardsData.forEach {
                Card.parse(it.key, it.getJSONObject())
            }

            val basicData = data.getJSONArray("basic")
            val basicDeck = HashMap<String, Int>(basicData.map {
                val basicCardData = it.getJSONObject()

                val cardId = basicCardData.getString("card_id")
                val count = basicCardData.optInt("count", 1)
                cardId to count
            }.toMap())

            return BasicCards(basicDeck)
        }
    }
}
