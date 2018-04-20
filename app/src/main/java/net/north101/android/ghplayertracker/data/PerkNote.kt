package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PerkNote(
        protected var _ticks: Int
) : Parcelable {
    @IgnoredOnParcel
    var ticks: Int
        get() = _ticks
        set(value) {
            _ticks = Math.min(Math.max(value, 0), MAX_TICKS)
        }

    companion object {
        const val MAX_TICKS = 3
    }
}
