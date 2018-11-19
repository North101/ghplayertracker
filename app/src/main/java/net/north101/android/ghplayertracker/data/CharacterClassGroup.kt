package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.RecyclerItemCompare
import net.north101.android.ghplayertracker.map
import org.json.JSONException
import org.json.JSONObject

@Parcelize
data class CharacterClassGroup(
    val name: String,
    val classes: ArrayList<CharacterClass>
) : Parcelable, RecyclerItemCompare {
    override val compareItemId: String
        get() = name

    companion object {
        @Throws(JSONException::class)
        fun parse(data: JSONObject): CharacterClassGroup {
            val name = data.getString("name")

            val classGroupsData = data.getJSONArray("classes")
            val classes = ArrayList(classGroupsData.map {
                CharacterClass.parse(it.getJSONObject())
            })

            return CharacterClassGroup(name, classes)
        }
    }
}