package net.north101.android.ghplayertracker

import android.widget.ImageView

interface GalleryItemClickListener {
    fun onGalleryItemClickListener(position: Int, imageModel: ImageModel, imageView: ImageView)
}