package net.north101.android.ghplayertracker

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.widget.ImageView
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