package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.RecyclerItemCompare

@Parcelize
data class SelectableCharacter(
    val character: Character,
    var selected: Boolean
) : Parcelable, RecyclerItemCompare {
    override val compareItemId: String
        get() = character.id.toString()

    fun copy(): SelectableCharacter {
        return SelectableCharacter(character.copy(), selected)
    }
}
