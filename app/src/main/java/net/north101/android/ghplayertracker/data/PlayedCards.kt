package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class PlayedCards(
        val pile1: ArrayList<Card>,
        val pile2: ArrayList<Card>?,
        var shuffled: Boolean
) : Parcelable
