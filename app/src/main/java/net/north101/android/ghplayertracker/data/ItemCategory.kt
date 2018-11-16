package net.north101.android.ghplayertracker.data

import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.RecyclerItemCompare
import net.north101.android.ghplayertracker.Util
import net.north101.android.ghplayertracker.mapNotNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.text.ParseException

@Parcelize
class ItemCategory(
    val id: String,
    val name: String
) : Parcelable, RecyclerItemCompare {
    override val compareItemId: String
        get() = id

    override fun equals(other: Any?): Boolean {
        if (other !is ItemCategory) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        fun parse(data: JSONObject): ItemCategory {
            val id = data.getString("id")
            val name = data.getString("name")

            return ItemCategory(id, name)
        }
    }
}

class ItemCategoryData(val context: Context, val data: JSONArray) {
    companion object {
        fun load(context: Context): ItemCategoryData {
            val data = try {
                loadJSON(context)
            } catch (e: Exception) {
                JSONArray()
            }

            return ItemCategoryData(context, data)
        }

        private fun getFileInput(context: Context): InputStream {
            val file = File(context.getExternalFilesDir(null), "item_categories.json")
            return if (file.exists()) {
                FileInputStream(file)
            } else {
                context.assets.open("item_categories.json", Context.MODE_PRIVATE)
            }
        }

        @Throws(IOException::class, JSONException::class, ParseException::class)
        private fun loadJSON(context: Context): JSONArray {
            val inputStream = getFileInput(context)
            return JSONArray(Util.readInputString(inputStream))
        }
    }

    fun toList(): ArrayList<ItemCategory> {
        return ArrayList(data.mapNotNull {
            try {
                ItemCategory.parse(it.getJSONObject())
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
        })
    }
}