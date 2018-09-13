package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import android.support.constraint.Placeholder
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.ImageUrl
import net.north101.android.ghplayertracker.RecyclerItemCompare
import org.json.JSONException
import org.json.JSONObject

@Parcelize
data class Ability(
    val id: String,
    override val name: String,
    val level: Int,
    val special: String?,
    override val imagePlaceholder: String
) : Parcelable, RecyclerItemCompare, ImageUrl {
    override val compareItemId: String
        get() = id

    override val imageUrl: String
        get() = "https://github.com/North101/ghplayertracker/raw/master/images/$id.jpg"

    companion object {
        @Throws(JSONException::class)
        fun parse(id: String, imagePlaceholder: String, data: JSONObject): Ability {
            val name = data.getString("name")
            val level = data.getInt("level")
            val special = data.optString("special")
            return Ability(id, name, level, special, imagePlaceholder)
        }
    }
}