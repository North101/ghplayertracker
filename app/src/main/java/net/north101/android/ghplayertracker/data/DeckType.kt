package net.north101.android.ghplayertracker.data

import org.json.JSONException

enum class DeckType {
    Basic,
    Class,
    Extra;

    companion object {
        @Throws(JSONException::class)
        fun parse(cardDeckData: String): DeckType {
            return when (cardDeckData) {
                "basic" -> DeckType.Basic
                "class" -> DeckType.Class
                "extra" -> DeckType.Extra
                else -> throw JSONException(cardDeckData)
            }
        }
    }
}
