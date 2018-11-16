package net.north101.android.ghplayertracker.data

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.Util
import net.north101.android.ghplayertracker.map
import net.north101.android.ghplayertracker.mapNotNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Character(
    val id: UUID,
    val characterClass: CharacterClass,
    var name: String,
    private var _level: Int,
    private var _xp: Int,
    private var _gold: Int,
    var perks: ArrayList<Int>,
    var perkNotes: ArrayList<Int>,
    val created: Date,
    var modified: Date,
    var retired: Boolean,
    var items: ArrayList<Item>,
    var abilities: ArrayList<Ability?>,
    var notes: ArrayList<String>
) : Parcelable {
    constructor(characterClass: CharacterClass) : this(
        UUID.randomUUID(),
        characterClass,
        characterClass.name,
        1,
        0,
        0,
        ArrayList(characterClass.perks.indices.map { 0 }),
        ArrayList(0.until(PERK_NOTES_COUNT).map { 0 }),
        Date(),
        Date(),
        false,
        ArrayList(),
        ArrayList(0.until(8).map { null }),
        ArrayList()
    )

    val currentLevel: Level
        get() = characterClass.levels[level - 1]

    val maxHealth: Int
        get() = currentLevel.health

    @IgnoredOnParcel
    var xp: Int
        get() = _xp
        set(value) {
            _xp = Math.max(value, 0)
        }

    @IgnoredOnParcel
    var level: Int
        get() = _level
        set(value) {
            _level = Math.min(Math.max(value, CharacterClass.LEVEL_MIN), CharacterClass.LEVEL_MAX)
        }

    @IgnoredOnParcel
    var gold: Int
        get() = _gold
        set(value) {
            _gold = Math.max(value, 0)
        }

    fun copy(): Character {
        return Character(
            id,
            characterClass,
            name,
            level,
            xp,
            gold,
            ArrayList(perks),
            ArrayList(perkNotes.map { it }),
            created,
            modified,
            retired,
            ArrayList(items.map { it.copy() }),
            ArrayList(abilities.map { it }),
            ArrayList(notes.map { it })
        )
    }

    @Throws(JSONException::class)
    fun toJSON(): JSONObject {
        val data = JSONObject()
        data.put("id", id.toString())
        data.put("class", characterClass.id)
        data.put("name", name)
        data.put("xp", xp)
        data.put("level", level)
        data.put("gold", gold)
        data.put("perks", JSONArray(perks))
        data.put("perk_notes", JSONArray(perkNotes))
        data.put("created", DATE_FORMATTER.format(created))
        data.put("modified", DATE_FORMATTER.format(modified))
        data.put("retired", retired)
        data.put("items", JSONArray(items.map {
            it.id
        }))
        data.put("abilities", JSONArray(abilities.map { it?.id }))
        data.put("notes", JSONArray(notes))

        return data
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Character)
            return false

        if (this.id != other.id)
            return false

        if (this.characterClass.id != other.characterClass.id)
            return false

        if (this.name != other.name)
            return false

        if (this.xp != other.xp)
            return false

        if (this.level != other.level)
            return false

        if (this.gold != other.gold)
            return false

        if (this.retired != other.retired)
            return false

        if (this.created != other.created)
            return false

        if (this.modified != other.modified)
            return false

        if (!this.perks.equalContentWith(other.perks))
            return false

        if (!this.perkNotes.equalContentWith(other.perkNotes))
            return false

        if (!this.items.sameContentWith(other.items))
            return false

        if (!this.abilities.equalContentWith(other.abilities))
            return false

        if (!this.notes.equalContentWith(other.notes))
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + characterClass.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + _level
        result = 31 * result + _xp
        result = 31 * result + _gold
        result = 31 * result + perks.hashCode()
        result = 31 * result + perkNotes.hashCode()
        result = 31 * result + created.hashCode()
        result = 31 * result + modified.hashCode()
        result = 31 * result + retired.hashCode()
        result = 31 * result + items.hashCode()
        result = 31 * result + abilities.hashCode()
        result = 31 * result + notes.hashCode()
        return result
    }

    companion object {
        var TIMEZONE = TimeZone.getTimeZone("UTC")!!
        @SuppressLint("SimpleDateFormat")
        var DATE_FORMATTER: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        var PERK_NOTES_COUNT = 6

        init {
            DATE_FORMATTER.timeZone = TIMEZONE
        }

        @Throws(JSONException::class, IOException::class, ParseException::class, kotlin.KotlinNullPointerException::class)
        fun parse(data: JSONObject, classList: Map<String, CharacterClass>, itemMap: Map<String, Item>): Character {
            val id = UUID.fromString(data.getString("id"))

            val className = data.getString("class")
            val characterClass = classList[className]!!

            val name = data.getString("name")
            val xp = data.optInt("xp", 0)
            val level = data.optInt("level", 0)
            val gold = data.optInt("gold", 0)

            val perksData = data.optJSONArray("perks")
            val perks = ArrayList(perksData?.map {
                it.optInt(0)
            }.orEmpty())

            val perkNotesData = data.optJSONArray("perk_notes")
            val perkNotes = ArrayList(0.until(PERK_NOTES_COUNT).map {
                perkNotesData.optInt(it, 0)
            })

            val created = DATE_FORMATTER.parse(data.optString("created"))
            val modified = DATE_FORMATTER.parse(data.optString("modified"))
            val retired = data.optBoolean("retired", false)

            val itemsData = data.optJSONArray("items")
            val items = ArrayList(itemsData?.mapNotNull {
                try {
                    val itemData = it.get()
                    when (itemData) {
                        is String -> itemMap[itemData]
                        is JSONObject -> {
                            val itemName = itemData.getString("name")
                            itemMap.values.find {
                                it.name == itemName
                            }
                        }
                        else -> null
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    null
                }
            }.orEmpty())

            val abilitiesData = data.optJSONArray("abilities")
            val abilities = ArrayList(0.until(8).map {
                val abilityId = abilitiesData?.optString(it, null)
                    ?.replace("""ability_class_(\d+)_(\d+)""".toRegex(), """class_$1_ability_$2""")
                characterClass.abilities.find {
                    it.id == abilityId
                }?.takeIf { it.level > 1 }
            })

            val notesData = data.optJSONArray("notes")
            val notes = ArrayList(notesData?.mapNotNull {
                it.optString(null)
            }.orEmpty())

            return Character(
                id,
                characterClass,
                name,
                level,
                xp,
                gold,
                perks,
                perkNotes,
                created,
                modified,
                retired,
                items,
                abilities,
                notes
            )
        }
    }
}

class CharacterData(val context: Context, val data: JSONObject) {
    companion object {
        fun load(context: Context): CharacterData {
            val data = try {
                loadJSON(context)
            } catch (e: Exception) {
                JSONObject()
            }

            return CharacterData(context, data)
        }

        private fun getFile(context: Context): File {
            return File(context.getExternalFilesDir(null), "characters.json")
        }

        @Throws(IOException::class, JSONException::class, ParseException::class)
        private fun loadJSON(context: Context): JSONObject {
            val inputStream = FileInputStream(getFile(context))
            return JSONObject(Util.readInputString(inputStream))
        }
    }

    fun update(character: Character): CharacterData {
        data.put(character.id.toString(), character.toJSON())

        return this
    }

    fun delete(character: Character): CharacterData {
        data.remove(character.id.toString())

        return this
    }

    @Throws(IOException::class, JSONException::class)
    fun save() {
        FileOutputStream(getFile(context)).use { outputStream ->
            outputStream.write(data.toString().toByteArray())
        }
    }

    fun toList(classMap: Map<String, CharacterClass>, itemMap: Map<String, Item>): ArrayList<Character> {
        return ArrayList(data.mapNotNull {
            try {
                Character.parse(it.getJSONObject(), classMap, itemMap)
            } catch (e: JSONException) {
                e.printStackTrace()
                null
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            } catch (e: kotlin.KotlinNullPointerException) {
                e.printStackTrace()
                null
            }
        })
    }
}

infix fun <T> Collection<T>?.sameContentWith(collection: Collection<T>?): Boolean {
    return if (this == collection) {
        true
    } else if (this != null && collection != null) {
        this.size == collection.size && this.containsAll(collection)
    } else {
        false
    }
}

infix fun <T> List<T>?.equalContentWith(list: List<T>?): Boolean {
    return if (this == list) {
        true
    } else if (this != null && list != null) {
        this.size == list.size && this.withIndex().all {
            list[it.index] == it.value
        }
    } else {
        false
    }
}