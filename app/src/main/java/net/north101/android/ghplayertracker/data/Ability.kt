package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.RecyclerItemCompare
import org.json.JSONException
import org.json.JSONObject

@Parcelize
data class Ability(
    val id: String,
    val name: String,
    val level: Int,
    val special: String?
) : Parcelable, RecyclerItemCompare {
    override val compareItemId: String
        get() = id

    val url: String
        get() = "https://raw.githubusercontent.com/North101/ghplayertracker/master/abilities/$id.jpg"

    companion object {
        @Throws(JSONException::class)
        fun parse(id: String, data: JSONObject): Ability {
            val name = data.getString("name")
            val level = data.getInt("level")
            val special = data.optString("special")
            return Ability(id, name, level, special)
        }
    }
}