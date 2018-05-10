package net.north101.android.ghplayertracker

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.transition.TransitionInflater
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.chrisbanes.photoview.PhotoView
import net.north101.android.ghplayertracker.data.Ability
import net.north101.android.ghplayertracker.data.CharacterClass
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentArg
import org.androidannotations.annotations.ViewById

@EFragment(R.layout.ability_image_item)
open class AbilityImageFragment : Fragment() {
    @ViewById(R.id.detail_image)
    protected lateinit var imageView: PhotoView

    @FragmentArg(ARG_CLASS_ID)
    protected lateinit var classId: String
    @FragmentArg(ARG_ABILITY)
    protected lateinit var ability: Ability

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }

    @AfterViews
    fun afterViews() {
        Glide.with(activity!!)
            .load(ability.url)
            .placeholder(Util.getImageResource(context!!, CharacterClass.cardBack(classId)))
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
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
        protected const val ARG_CLASS_ID = "class_id"
        protected const val ARG_ABILITY = "ability"

        fun newInstance(classId: String, ability: Ability): AbilityImageFragment_ {
            val args = Bundle()
            args.putString(ARG_CLASS_ID, classId)
            args.putParcelable(ARG_ABILITY, ability)

            val fragment = AbilityImageFragment_()
            fragment.arguments = args

            return fragment
        }
    }
}