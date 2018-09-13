package net.north101.android.ghplayertracker

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter

class ImagePagerAdapter(
    val fragment: Fragment,
    val imageList: ArrayList<ImageUrl>
) : FragmentStatePagerAdapter(fragment.fragmentManager) {
    override fun getItem(position: Int): Fragment {
        val imageUrl = imageList[position]
        return ImageFragment.newInstance(imageUrl)
    }

    override fun getCount(): Int {
        return imageList.size
    }
}