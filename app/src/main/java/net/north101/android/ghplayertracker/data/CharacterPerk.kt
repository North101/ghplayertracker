package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CharacterPerk(
        val perk: Perk,
        var ticks: Int
) : Parcelable
