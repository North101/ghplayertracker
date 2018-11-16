package net.north101.android.ghplayertracker.data

import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import android.util.Base64
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.RecyclerItemCompare
import net.north101.android.ghplayertracker.Util
import net.north101.android.ghplayertracker.map
import net.north101.android.ghplayertracker.mapNotNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class CharacterClass(
    val id: String,
    val name: String,
    val color: Int,
    val levels: ArrayList<Level>,
    val cards: HashMap<String, Card>,
    val perks: ArrayList<Perk>,
    val abilities: ArrayList<Ability>
) : Parcelable, RecyclerItemCompare {
    override val compareItemId: String
        get() = this.id

    companion object {
        val HEALTH_HIGH = ArrayList(arrayOf(10, 12, 14, 16, 18, 20, 22, 24, 26).mapIndexed { index, i -> Level(index + 1, i) })
        val HEALTH_MEDIUM = ArrayList(arrayOf(8, 9, 11, 12, 14, 15, 17, 18, 20).mapIndexed { index, i -> Level(index + 1, i) })
        val HEALTH_LOW = ArrayList(arrayOf(6, 7, 8, 9, 10, 11, 12, 13, 14).mapIndexed { index, i -> Level(index + 1, i) })

        const val LEVEL_MIN = 1
        const val LEVEL_MAX = 9

        fun cardBack(id: String): String {
            return "${id}_card_back"
        }

        @Throws(JSONException::class)
        fun parse(data: JSONObject): CharacterClass {
            val id = data.getString("id")
            val name = decrypt(data.getString("name"))
            val color = Color.parseColor(data.getString("color"))

            val health = data.getString("health")
            val levels = when (health) {
                "high" -> HEALTH_HIGH
                "medium" -> HEALTH_MEDIUM
                "low" -> HEALTH_LOW
                else -> throw RuntimeException(health)
            }

            val cardsData = data.getJSONObject("cards")
            val cards = HashMap(cardsData.map {
                it.key to Card.parse(it.key, it.getJSONObject())
            }.toMap())

            val perkGroupsData = data.getJSONArray("perks")
            val perks = ArrayList(perkGroupsData.map {
                Perk.parse(it.getJSONObject())
            })

            val abilitiesData = data.optJSONObject("abilities")
            val abilities = ArrayList(abilitiesData?.map {
                Ability.parse(it.key, cardBack(id), it.getJSONObject())
            }.orEmpty())

            return CharacterClass(id, name, color, levels, cards, perks, ArrayList(abilities.sortedBy { it.id }))
        }

        fun decrypt(value: String): String {
            return String(Base64.decode(value, Base64.DEFAULT))
        }
    }
}


class CharacterClassData(val context: Context, val data: JSONArray) {
    companion object {
        fun load(context: Context): CharacterClassData {
            val data = try {
                loadJSON(context)
            } catch (e: Exception) {
                JSONArray()
            }

            return CharacterClassData(context, data)
        }

        private fun getFileInput(context: Context): InputStream {
            val file = File(context.getExternalFilesDir(null), "classes.json")
            return if (file.exists()) {
                FileInputStream(file)
            } else {
                context.assets.open("classes.json", Context.MODE_PRIVATE)
            }
        }

        @Throws(IOException::class, JSONException::class, ParseException::class)
        private fun loadJSON(context: Context): JSONArray {
            val inputStream = getFileInput(context)
            return JSONArray(Util.readInputString(inputStream))
        }
    }

    fun toList(): HashMap<String, CharacterClass> {
        return HashMap(data.mapNotNull {
            try {
                val value = CharacterClass.parse(it.getJSONObject())
                value.id to value
            } catch (e: JSONException) {
                e.printStackTrace()
                null
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }
        }.toMap())
    }
}