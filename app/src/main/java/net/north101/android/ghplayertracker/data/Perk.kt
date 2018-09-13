package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.map
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
            val perksData = data.getJSONArray("cards")
            val perkItems = ArrayList<PerkItem>(perksData?.map {
                PerkItem.parse(it.getJSONObject())
            })

            val ticks = data.optInt("ticks", 1)
            val text = data.getString("text")

            return Perk(perkItems, ticks, text)
        }
    }
}
