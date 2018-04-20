package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Parcelize
class Perk(
        val perkItems: List<PerkItem>,
        val ticks: Int,
        val text: String
) : Parcelable {
    companion object {
        @Throws(JSONException::class)
        fun parse(data: JSONObject): Perk {
            val perkItems = ArrayList<PerkItem>()
            val perksData = data.getJSONArray("cards")
            for (i in 0 until perksData.length()) {
                perkItems.add(PerkItem.parse(perksData.getJSONObject(i)))
            }

            val ticks = data.optInt("ticks", 1)
            val text = data.getString("text")

            return Perk(perkItems, ticks, text)
        }
    }
}
