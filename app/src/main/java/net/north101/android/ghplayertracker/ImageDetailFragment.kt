package net.north101.android.ghplayertracker

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.chrisbanes.photoview.PhotoView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget


class ImageDetailFragment : Fragment() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        postponeEnterTransition()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_detail, container, false)
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        super.onViewCreated(view, state)

        val image = arguments!!.getParcelable<ImageModel>(EXTRA_IMAGE)
        val transitionName = arguments!!.getString(EXTRA_TRANSITION_NAME)

        val imageView = view.findViewById(R.id.detail_image) as PhotoView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.transitionName = transitionName
        }

        Glide.with(activity!!)
            .load(image!!.url)
            .asBitmap()
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                    startPostponedEnterTransition()
                    imageView.setImageBitmap(resource)
                }
            })

    }

    companion object {
        private const val EXTRA_IMAGE = "image_item"
        private const val EXTRA_TRANSITION_NAME = "transition_name"

        fun newInstance(image: ImageModel, transitionName: String): ImageDetailFragment {
            val fragment = ImageDetailFragment()
            val args = Bundle()
            args.putParcelable(EXTRA_IMAGE, image)
            args.putString(EXTRA_TRANSITION_NAME, transitionName)
            fragment.arguments = args
            return fragment
        }
    }
}