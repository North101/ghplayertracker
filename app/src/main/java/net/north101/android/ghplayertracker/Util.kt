package net.north101.android.ghplayertracker

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.widget.ImageView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object Util {
    @Throws(IOException::class)
    fun readInputString(inputStream: InputStream): String {
        val streamReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        val builder = StringBuilder(1024)

        for (line in streamReader.readLines()) {
            builder.append(line)
        }

        return builder.toString()
    }

    fun getImageResource(context: Context, name: String): Int {
        val res = context.resources
        val i = res.getIdentifier(name, "drawable", context.packageName)
        if (i == 0)
            Log.d("getImageResource", name)
        return i
    }
}

fun setImageViewGreyscale(imageView: ImageView, set: Boolean) {
    if (set) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)  //0 means greyscale
        val cf = ColorMatrixColorFilter(matrix)
        imageView.colorFilter = cf
        imageView.alpha = 0.5f
    } else {
        imageView.colorFilter = null
        imageView.alpha = 1.0f
    }
}

class JSONArrayNode(private val items: JSONArray, val index: Int) {
    fun isNull(): Boolean {
        return items.isNull(index)
    }

    @Throws(JSONException::class)
    fun get(): Any {
        return items.get(index)
    }

    fun opt(): Any {
        return items.opt(index)
    }

    @Throws(JSONException::class)
    fun getBoolean(): Boolean {
        return items.getBoolean(index)
    }

    fun optBoolean(): Boolean {
        return items.optBoolean(index)
    }

    fun optBoolean(fallback: Boolean): Boolean {
        return items.optBoolean(index, fallback)
    }

    @Throws(JSONException::class)
    fun getDouble(): Double {
        return items.getDouble(index)
    }

    fun optDouble(): Double {
        return items.optDouble(index)
    }

    fun optDouble(fallback: Double): Double {
        return items.optDouble(index, fallback)
    }

    @Throws(JSONException::class)
    fun getInt(): Int {
        return items.getInt(index)
    }

    fun optInt(): Int {
        return items.optInt(index)
    }

    fun optInt(fallback: Int): Int {
        return items.optInt(index, fallback)
    }

    @Throws(JSONException::class)
    fun getLong(): Long {
        return items.getLong(index)
    }

    fun optLong(): Long {
        return items.optLong(index)
    }

    fun optLong(fallback: Long): Long {
        return items.optLong(index, fallback)
    }

    @Throws(JSONException::class)
    fun getString(): String {
        return items.getString(index)
    }

    fun optString(): String {
        return items.optString(index)
    }

    fun optString(fallback: String?): String {
        return items.optString(index, fallback)
    }

    @Throws(JSONException::class)
    fun getJSONArray(): JSONArray {
        return items.getJSONArray(index)
    }

    fun optJSONArray(): JSONArray {
        return items.optJSONArray(index)
    }

    @Throws(JSONException::class)
    fun getJSONObject(): JSONObject {
        return items.getJSONObject(index)
    }

    fun optJSONObject(): JSONObject {
        return items.optJSONObject(index)
    }
}

class JSONObjectNode(private val items: JSONObject, val key: String) {
    fun isNull(): Boolean {
        return items.isNull(key)
    }

    @Throws(JSONException::class)
    fun get(): Any {
        return items.get(key)
    }

    fun opt(): Any {
        return items.opt(key)
    }

    @Throws(JSONException::class)
    fun getBoolean(): Boolean {
        return items.getBoolean(key)
    }

    fun optBoolean(): Boolean {
        return items.optBoolean(key)
    }

    fun optBoolean(fallback: Boolean): Boolean {
        return items.optBoolean(key, fallback)
    }

    @Throws(JSONException::class)
    fun getDouble(): Double {
        return items.getDouble(key)
    }

    fun optDouble(): Double {
        return items.optDouble(key)
    }

    fun optDouble(fallback: Double): Double {
        return items.optDouble(key, fallback)
    }

    @Throws(JSONException::class)
    fun getInt(): Int {
        return items.getInt(key)
    }

    fun optInt(): Int {
        return items.optInt(key)
    }

    fun optInt(fallback: Int): Int {
        return items.optInt(key, fallback)
    }

    @Throws(JSONException::class)
    fun getLong(): Long {
        return items.getLong(key)
    }

    fun optLong(): Long {
        return items.optLong(key)
    }

    fun optLong(fallback: Long): Long {
        return items.optLong(key, fallback)
    }

    @Throws(JSONException::class)
    fun getString(): String {
        return items.getString(key)
    }

    fun optString(): String {
        return items.optString(key)
    }

    fun optString(fallback: String?): String {
        return items.optString(key, fallback)
    }

    @Throws(JSONException::class)
    fun getJSONArray(): JSONArray {
        return items.getJSONArray(key)
    }

    fun optJSONArray(): JSONArray {
        return items.optJSONArray(key)
    }

    @Throws(JSONException::class)
    fun getJSONObject(): JSONObject {
        return items.getJSONObject(key)
    }

    fun optJSONObject(): JSONObject {
        return items.optJSONObject(key)
    }
}

fun <T> JSONArray.map(transform: (JSONArrayNode) -> T): List<T> {
    return 0.until(this.length()).map {
        transform(JSONArrayNode(this, it))
    }
}

fun <T : Any> JSONArray.mapNotNull(transform: (JSONArrayNode) -> T?): List<T> {
    return 0.until(this.length()).mapNotNull {
        transform(JSONArrayNode(this, it))
    }
}

fun JSONArray.forEach(action: (JSONArrayNode) -> Unit) {
    0.until(this.length()).forEach {
        action(JSONArrayNode(this, it))
    }
}

fun <T> JSONObject.map(transform: (JSONObjectNode) -> T): List<T> {
    return this.keys().asSequence().map {
        transform(JSONObjectNode(this, it))
    }.toList()
}

fun <T : Any> JSONObject.mapNotNull(transform: (JSONObjectNode) -> T?): List<T> {
    return this.keys().asSequence().mapNotNull {
        transform(JSONObjectNode(this, it))
    }.toList()
}

fun JSONObject.forEach(action: (JSONObjectNode) -> Unit) {
    this.keys().asSequence().forEach {
        action(JSONObjectNode(this, it))
    }
}