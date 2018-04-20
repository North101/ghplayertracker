package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class StatusSet(
        var disarm: Boolean = false,
        var stun: Boolean = false,
        var immobilize: Boolean = false,
        var poison: Boolean = false,
        var wound: Boolean = false,
        var muddle: Boolean = false,
        var invisible: Boolean = false,
        var strengthen: Boolean = false
) : Parcelable
