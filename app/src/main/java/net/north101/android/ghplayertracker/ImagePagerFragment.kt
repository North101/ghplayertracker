package net.north101.android.ghplayertracker

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.TransitionInflater
import android.view.Menu
import android.view.MenuItem
import net.north101.android.ghplayertracker.livedata.InitLiveData
import org.androidannotations.annotations.*

@EFragment(R.layout.image_viewpager)
@OptionsMenu(R.menu.ability_gallery)
open class ImagePagerFragment : Fragment() {
    protected lateinit var actionBar: ActionBar
    @ViewById(R.id.toolbar)
    protected lateinit var toolbar: Toolbar
    @ViewById(R.id.view_pager)
    protected lateinit var viewPager: ViewPager
    protected lateinit var imagePagerAdapter: ImagePagerAdapter

    @OptionsMenuItem(R.id.gallery)
    protected lateinit var galleryMenu: MenuItem

    @FragmentArg(ARG_IMAGE_LIST)
    protected lateinit var imageList: ArrayList<ImageUrl>
    @FragmentArg(ARG_IMAGE_INDEX)
    @JvmField
    protected var imageIndex: Int? = null

    protected lateinit var galleryFragment: ImageGalleryFragment

    protected lateinit var imageGalleryModel: ImageGalleryModel

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setHasOptionsMenu(true)

        val supportFragmentManager = activity!!.supportFragmentManager
        galleryFragment = supportFragmentManager
            .findFragmentByTag(ImageGalleryFragment.TAG) as ImageGalleryFragment?
            ?: ImageGalleryFragment.newInstance(imageList)

        postponeEnterTransition()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }

        imageGalleryModel = ViewModelProviders.of(this.activity!!).get(ImageGalleryModel::class.java)
        if (state == null) {
            imageGalleryModel.currentIndex.value = imageIndex ?: 0
            imageGalleryModel.showGallery.postValue(imageIndex == null)
        } else {
            imageGalleryModel.currentIndex.value = state.getInt("index")
            imageGalleryModel.showGallery.value = state.getBoolean("showGallery")
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        state.putInt("index", imageGalleryModel.currentIndex.value)
        state.putBoolean("showGallery", imageGalleryModel.showGallery.value)

        super.onSaveInstanceState(state)
    }

    val showGallery: Boolean
        get() = imageList.size > 1

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)

        galleryMenu.isVisible = !imageGalleryModel.showGallery.value
    }

    @OptionsItem(R.id.gallery)
    fun onGalleryMenuClick() {
        imageGalleryModel.showGallery.value = true
    }

    @AfterViews
    fun afterViews() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        actionBar = (activity as AppCompatActivity).supportActionBar!!

        imagePagerAdapter = ImagePagerAdapter(this, imageList)

        viewPager.adapter = imagePagerAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                imageGalleryModel.currentIndex.value = position
            }
        })

        imageGalleryModel.currentIndex.observe(this) {
            if (viewPager.currentItem != it) {
                viewPager.currentItem = it
            }
            actionBar.title = imageList[it].name
        }
        imageGalleryModel.showGallery.observe(this) {
            if (it) {
                if (!galleryFragment.isAdded) {
                    activity!!.supportFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .hide(this)
                        .add(R.id.content, galleryFragment, ImageGalleryFragment.TAG)
                        .show(galleryFragment)
                        .commit()
                }
            } else {
                if (galleryFragment.isAdded) {
                    activity!!.supportFragmentManager
                        .beginTransaction()
                        .hide(galleryFragment)
                        .remove(galleryFragment)
                        .show(this)
                        .commit()
                }
            }
            if (::galleryMenu.isInitialized) {
                galleryMenu.isVisible = !it
            }
        }
    }

    companion object {
        protected const val ARG_IMAGE_LIST = "image_list"
        protected const val ARG_IMAGE_INDEX = "image_index"

        fun newInstance(imageList: ArrayList<ImageUrl>, imageIndex: Int? = null): ImagePagerFragment_ {
            val args = Bundle()
            args.putParcelableArrayList(ARG_IMAGE_LIST, imageList)
            if (imageIndex != null) {
                args.putInt(ARG_IMAGE_INDEX, imageIndex)
            }

            val fragment = ImagePagerFragment_()
            fragment.arguments = args

            return fragment
        }
    }
}

class ImageGalleryModel : ViewModel() {
    val currentIndex = InitLiveData(0)
    val showGallery = InitLiveData(false)
}