package net.north101.android.ghplayertracker

import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import org.androidannotations.annotations.*

@EFragment(R.layout.image_gallery_layout)
open class ImageGalleryFragment : Fragment(), OnBackPressedListener {
    protected lateinit var actionBar: ActionBar
    @ViewById(R.id.toolbar)
    protected lateinit var toolbar: Toolbar
    @ViewById(R.id.recycler_view)
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var adapter: AbilityGalleryAdapter

    @FragmentArg(ARG_IMAGE_LIST)
    protected lateinit var imageList: ArrayList<ImageUrl>

    protected lateinit var imageGalleryModel: ImageGalleryModel

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setHasOptionsMenu(true)

        imageGalleryModel = ViewModelProviders.of(this.activity!!).get(ImageGalleryModel::class.java)
    }

    @AfterViews
    fun afterViews() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        actionBar = (activity as AppCompatActivity).supportActionBar!!

        if (!::adapter.isInitialized) {
            adapter = AbilityGalleryAdapter()
        }
        adapter.onImageItemClick = {
            imageGalleryModel.currentIndex.value = imageList.indexOf(it)
            imageGalleryModel.showGallery.value = false
        }

        val landscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val galleryLayoutManager = GridLayoutManager(context, if (landscape) 4 else 2)

        recyclerView.layoutManager = galleryLayoutManager
        recyclerView.adapter = adapter

        adapter.updateItems(imageList)
    }
    
    override fun onBackPressed(): Boolean {
        imageGalleryModel.showGallery.value = false
        return false
    }


    companion object {
        val TAG = ImageGalleryFragment::class.java.simpleName

        protected const val ARG_IMAGE_LIST = "image_list"

        fun newInstance(imageList: ArrayList<ImageUrl>): ImageGalleryFragment_ {
            val args = Bundle()
            args.putParcelableArrayList(ARG_IMAGE_LIST, imageList)

            val fragment = ImageGalleryFragment_()
            fragment.arguments = args

            return fragment
        }
    }
}