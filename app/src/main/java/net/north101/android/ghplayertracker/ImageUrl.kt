package net.north101.android.ghplayertracker

import android.os.Parcelable

interface ImageUrl: Parcelable, RecyclerItemCompare {
    val imageUrl: String
    val imagePlaceholder: String
    val name: String
}