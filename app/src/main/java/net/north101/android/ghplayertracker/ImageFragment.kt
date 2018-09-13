package net.north101.android.ghplayertracker

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.transition.TransitionInflater
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentArg
import org.androidannotations.annotations.ViewById

@EFragment(R.layout.image_item)
open class ImageFragment : Fragment() {
    @ViewById(R.id.detail_image)
    protected lateinit var imageView: PhotoView

    @FragmentArg(ARG_IMAGE)
    protected lateinit var imageUrl: ImageUrl

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }

    @AfterViews
    fun afterViews() {
        Glide.with(activity!!)
            .load(imageUrl.imageUrl)
            .apply(
                RequestOptions()
                    .placeholder(Util.getImageResource(context!!, imageUrl.imagePlaceholder))
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .into(imageView)
    }

    fun getSharedElements(): ArrayList<View>? {
        return if (isViewInBounds(activity!!.window.decorView, imageView)) {
            arrayListOf(imageView)
        } else null
    }

    fun isViewInBounds(container: View, view: View): Boolean {
        val containerBounds = Rect()
        container.getHitRect(containerBounds)
        return view.getLocalVisibleRect(containerBounds)
    }

    companion object {
        protected const val ARG_IMAGE = "image"

        fun newInstance(imageUrl: ImageUrl): ImageFragment_ {
            val args = Bundle()
            args.putParcelable(ARG_IMAGE, imageUrl)

            val fragment = ImageFragment_()
            fragment.arguments = args

            return fragment
        }
    }
}