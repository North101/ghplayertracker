package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONException
import org.json.JSONObject

@Parcelize
data class Ability(
    val id: String,
    val name: String,
    val level: Int
) : Parcelable {
    companion object {
        @Throws(JSONException::class)
        fun parse(id: String, data: JSONObject): Ability {
            val name = data.getString("name")
            val level = data.getInt("level")
            return Ability(id, name, level)
        }
    }
}