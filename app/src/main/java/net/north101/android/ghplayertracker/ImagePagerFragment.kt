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
    protected var imageIndex = 0

    protected lateinit var galleryFragment: ImageGalleryFragment

    protected lateinit var imageGalleryModel: ImageGalleryModel

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val supportFragmentManager = activity!!.supportFragmentManager
        galleryFragment = supportFragmentManager
            .findFragmentByTag(ImageGalleryFragment.TAG) as ImageGalleryFragment?
            ?: ImageGalleryFragment.newInstance(imageList)

        imageGalleryModel = ViewModelProviders.of(this.activity!!).get(ImageGalleryModel::class.java)
        if (state == null) {
            imageGalleryModel.currentIndex.value = imageIndex
        } else {
            imageGalleryModel.currentIndex.value = state.getInt("index")
        }

        postponeEnterTransition()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        state.putInt("index", imageGalleryModel.currentIndex.value)

        super.onSaveInstanceState(state)
    }

    val showGallery: Boolean
        get() = imageList.size > 1

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
                        .commit()
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)

        galleryMenu.isVisible = showGallery
    }

    companion object {
        protected const val ARG_IMAGE_LIST = "image_list"
        protected const val ARG_IMAGE_INDEX = "image_index"

        fun newInstance(imageList: ArrayList<ImageUrl>, imageIndex: Int = 0): ImagePagerFragment_ {
            val args = Bundle()
            args.putParcelableArrayList(ARG_IMAGE_LIST, imageList)
            args.putInt(ARG_IMAGE_INDEX, imageIndex)

            val fragment = ImagePagerFragment_()
            fragment.arguments = args

            return fragment
        }
    }

    @OptionsItem(R.id.gallery)
    fun onGalleryMenuClick() {
        imageGalleryModel.showGallery.value = true
    }
}

class ImageGalleryModel : ViewModel() {
    val currentIndex = InitLiveData(0)
    val showGallery = InitLiveData(false)
}