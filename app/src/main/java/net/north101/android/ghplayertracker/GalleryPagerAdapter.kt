package net.north101.android.ghplayertracker

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

import java.util.ArrayList

class GalleryPagerAdapter(fm: FragmentManager, private val images: ArrayList<ImageModel>) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val image = images[position]
        return ImageDetailFragment.newInstance(image, image.name)
    }

    override fun getCount(): Int {
        return images.size
    }
}