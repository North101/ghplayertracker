package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Summon(
    val id: UUID,
    val name: String,
    val health: Int,
    val maxHealth: Int,
    val move: Int,
    val attack: Int,
    val range: Int,
    val status: HashMap<Status, Boolean>
) : Parcelable