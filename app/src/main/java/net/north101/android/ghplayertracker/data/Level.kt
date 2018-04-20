package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Level(
        val level: Int,
        val health: Int
) : Parcelable {
    val xp: Int
        get() = LEVEL_XP_LIST[level - 1]

    companion object {
        var LEVEL_XP_LIST = intArrayOf(0, 45, 95, 150, 210, 275, 345, 420, 500)
    }
}
