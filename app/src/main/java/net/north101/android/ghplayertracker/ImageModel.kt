package net.north101.android.ghplayertracker

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageModel(
    var name: String,
    var url: String
) : Parcelable