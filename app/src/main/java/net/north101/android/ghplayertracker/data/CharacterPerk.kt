package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.RecyclerItemCompare

@Parcelize
data class CharacterPerk(
    val perk: Perk,
    var ticks: Int
) : Parcelable, RecyclerItemCompare {
    override val compareItemId: String
        get() = perk.text
}
