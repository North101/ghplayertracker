package net.north101.android.ghplayertracker

import android.content.Context
import android.util.Log
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
