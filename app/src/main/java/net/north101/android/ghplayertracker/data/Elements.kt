package net.north101.android.ghplayertracker.data

import org.json.JSONException

enum class Elements {
    fire,
    ice,
    air,
    earth,
    light,
    dark;

    companion object {
        @Throws(JSONException::class)
        fun parse(elementData: String): Elements {
            return when (elementData) {
                "fire" -> Elements.fire
                "ice" -> Elements.ice
                "earth" -> Elements.earth
                "air" -> Elements.air
                "light" -> Elements.light
                "dark" -> Elements.dark
                else -> throw JSONException(elementData)
            }
        }
    }
}
