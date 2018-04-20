package net.north101.android.ghplayertracker.data

import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.Util
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
        protected var _xp: Int,
        protected var _level: Int,
        protected var _gold: Int,
        protected var _minus1: Int,
        var perks: ArrayList<Int>,
        var perkNotes: ArrayList<PerkNote>,
        val created: Date,
        var modified: Date,
        var retired: Boolean
) : Parcelable {
    constructor(characterClass: CharacterClass) : this(
            UUID.randomUUID(),
            characterClass,
            characterClass.name,
            0,
            1,
            0,
            0,
            ArrayList<Int>(characterClass.perks.indices.map { 0 }),
            ArrayList<PerkNote>(0.until(PERK_NOTES_COUNT).map { PerkNote(0) }),
            Date(),
            Date(),
            false
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

    @IgnoredOnParcel
    var minus1: Int
        get() = _minus1
        set(value) {
            _minus1 = Math.max(value, 0)
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
        data.put("minus1", minus1)
        data.put("perks", JSONArray(perks))
        data.put("perk_notes", JSONArray(perkNotes.map {
            it.ticks
        }))
        data.put("created", DATE_FORMATTER.format(created))
        data.put("modified", DATE_FORMATTER.format(modified))
        data.put("retired", retired)

        return data
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

        fun toList(classList: List<CharacterClass>): ArrayList<Character> {
            val items = ArrayList<Character>()

            val keys = data.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                try {
                    val character = Character.parse(data.getJSONObject(key), classList)
                    items.add(character)
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

            }

            return items
        }
    }

    companion object {
        var TIMEZONE = TimeZone.getTimeZone("UTC")!!
        var DATE_FORMATTER: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        var PERK_NOTES_COUNT = 6

        init {
            DATE_FORMATTER.timeZone = TIMEZONE
        }

        @Throws(JSONException::class, IOException::class, ParseException::class)
        fun parse(data: JSONObject, classList: List<CharacterClass>): Character {
            val id = UUID.fromString(data.getString("id"))

            val className = data.getString("class")
            val characterClass = classList.find { it.id == className }!!

            val name = data.getString("name")
            val xp = data.getInt("xp")
            val level = data.getInt("level")
            val gold = data.getInt("gold")
            val minus1 = data.getInt("minus1")

            val perksData = data.getJSONArray("perks")
            val perks = ArrayList(0.until(perksData.length()).map {
                perksData.getInt(it)
            })

            val perkNotesData = data.getJSONArray("perk_notes")
            val perkNotes = ArrayList(0.until(PERK_NOTES_COUNT).map {
                PerkNote(perkNotesData.getInt(it))
            })

            val created = DATE_FORMATTER.parse(data.getString("created"))
            val modified = DATE_FORMATTER.parse(data.getString("modified"))
            val retired = data.optBoolean("retired", false)

            return Character(id, characterClass, name, xp, level, gold, minus1, perks, perkNotes, created, modified, retired)
        }
    }
}