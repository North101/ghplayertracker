package net.north101.android.ghplayertracker.data

import org.json.JSONException

enum class CardSpecial {
    None,
    Shuffle,
    Rolling,
    Remove;

    companion object {
        @Throws(JSONException::class)
        fun parse(data: String?): CardSpecial {
            if (data == null) return None

            return when (data) {
                "shuffle" -> CardSpecial.Shuffle
                "rolling" -> CardSpecial.Rolling
                "remove" -> CardSpecial.Remove
                else -> throw JSONException(data)
            }
        }
    }
}
