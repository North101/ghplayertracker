package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONException
import org.json.JSONObject

@Parcelize
class PerkItem(
        val perkAction: PerkAction,
        val cardId: String,
        val repeat: Int
) : Parcelable {
    companion object {
        @Throws(JSONException::class)
        fun parse(jsonObject: JSONObject): PerkItem {
            val perkAction = PerkAction.parse(jsonObject.getString("action"))
            val repeat = jsonObject.optInt("repeat", 1)
            val cardId = jsonObject.getString("card_id")

            return PerkItem(perkAction, cardId, repeat)
        }
    }
}
