package net.north101.android.ghplayertracker.data

import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.ImageUrl
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
import java.util.*

@Parcelize
data class Item(
    val id: String,
    val itemId: String,
    override val name: String,
    val type: ItemType,
    val category: ItemCategory,
    val price: Int,
    val unlocked: Boolean
) : Parcelable, RecyclerItemCompare, ImageUrl {
    override val compareItemId: String
        get() = id

    override val imageUrl: String
        get() = "https://github.com/North101/ghplayertracker/raw/master/images/$id.jpg"

    override val imagePlaceholder: String
        get() = "item_card_back"

    override fun equals(other: Any?): Boolean {
        if (other !is Item) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        fun parse(data: JSONObject, itemCategoryList: ArrayList<ItemCategory>): Item {
            val id = data.getString("id")
            val itemId = id.replace("item_", "#")
            val name = data.getString("name")
            val type = when(data.getString("type")) {
                "head" -> ItemType.Head
                "body" -> ItemType.Body
                "legs" -> ItemType.Legs
                "onehand" -> ItemType.OneHand
                "twohands" -> ItemType.TwoHands
                "small" -> ItemType.Small
                else -> throw RuntimeException(data.getString("type"))
            }
            val category = itemCategoryList.find {
                it.id == data.getString("category")
            }!!
            val price = data.optInt("price", (Random().nextInt(9) + 1) * 10)

            return Item(id, itemId, name, type, category, price, true)
        }
    }
}

class ItemData(val context: Context, val data: JSONArray) {
    companion object {
        fun load(context: Context): ItemData {
            val data = try {
                loadJSON(context)
            } catch (e: Exception) {
                JSONArray()
            }

            return ItemData(context, data)
        }

        private fun getFileInput(context: Context): InputStream {
            val file = File(context.getExternalFilesDir(null), "items.json")
            return if (file.exists()) {
                FileInputStream(file)
            } else {
                context.assets.open("items.json", Context.MODE_PRIVATE)
            }
        }

        @Throws(IOException::class, JSONException::class, ParseException::class)
        private fun loadJSON(context: Context): JSONArray {
            val inputStream = getFileInput(context)
            return JSONArray(Util.readInputString(inputStream))
        }
    }

    fun toList(itemCategoryList: ArrayList<ItemCategory>): HashMap<String, Item> {
        return HashMap(data.mapNotNull {
            try {
                val value = Item.parse(it.getJSONObject(), itemCategoryList)
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