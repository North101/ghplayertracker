package net.north101.android.ghplayertracker

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList

class GalleryViewPagerFragment : Fragment() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        postponeEnterTransition()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery_view_pager, container, false)
    }


    override fun onViewCreated(view: View, state: Bundle?) {
        super.onViewCreated(view, state)

        val currentItem = arguments!!.getInt(EXTRA_INITIAL_POS)
        val images = arguments!!.getParcelableArrayList<ImageModel>(EXTRA_IMAGES)
        val galleryPagerAdapter = GalleryPagerAdapter(childFragmentManager, images)

        val viewPager = view.findViewById(R.id.animal_view_pager) as ViewPager
        viewPager.adapter = galleryPagerAdapter
        viewPager.currentItem = currentItem
    }

    companion object {
        private const val EXTRA_INITIAL_POS = "initial_pos"
        private const val EXTRA_IMAGES = "images"

        fun newInstance(images: ArrayList<ImageModel>, position: Int): GalleryViewPagerFragment {
            val args = Bundle()
            args.putInt(EXTRA_INITIAL_POS, position)
            args.putParcelableArrayList(EXTRA_IMAGES, images)

            val fragment = GalleryViewPagerFragment()
            fragment.arguments = args

            return fragment
        }
    }
}