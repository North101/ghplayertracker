package net.north101.android.ghplayertracker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


class RecyclerViewFragment : Fragment(), GalleryItemClickListener {
    protected lateinit var images: ArrayList<ImageModel>

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        images = arguments!!.getParcelableArrayList("images")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        super.onViewCreated(view, state)

        val galleryAdapter = GalleryAdapter(
            images,
            this
        )
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val gridLayoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = galleryAdapter
    }

    override fun onGalleryItemClickListener(position: Int, imageModel: ImageModel, imageView: ImageView) {
        val galleryViewPagerFragment = GalleryViewPagerFragment.newInstance(images, position)

        fragmentManager!!
            .beginTransaction()
            .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
            .replace(R.id.content, galleryViewPagerFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        val TAG = RecyclerViewFragment::class.java.simpleName!!

        fun newInstance(images: ArrayList<ImageModel>): RecyclerViewFragment {
            val args = Bundle()
            args.putParcelableArrayList("images", images)

            val fragment = RecyclerViewFragment()
            fragment.arguments = args

            return fragment
        }
    }
}