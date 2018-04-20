package net.north101.android.ghplayertracker.data

import org.json.JSONException

enum class PerkAction {
    add,
    remove;

    companion object {
        @Throws(JSONException::class)
        fun parse(perkActionData: String): PerkAction {
            return when (perkActionData) {
                "add" -> PerkAction.add
                "remove" -> PerkAction.remove
                else -> throw JSONException(perkActionData)
            }
        }
    }
}
