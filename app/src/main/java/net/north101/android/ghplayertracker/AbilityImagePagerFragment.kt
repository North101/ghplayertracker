package net.north101.android.ghplayertracker

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.TransitionInflater
import net.north101.android.ghplayertracker.data.Ability
import net.north101.android.ghplayertracker.data.CharacterClass
import net.north101.android.ghplayertracker.livedata.InitLiveData
import org.androidannotations.annotations.*


@EFragment(R.layout.ability_image_viewpager)
@OptionsMenu(R.menu.ability_gallery)
open class AbilityImagePagerFragment : Fragment() {
    protected lateinit var actionBar: ActionBar
    @ViewById(R.id.toolbar)
    protected lateinit var toolbar: Toolbar
    @ViewById(R.id.view_pager)
    protected lateinit var viewPager: ViewPager
    protected lateinit var abilityImagePagerAdapter: AbilityImagePagerAdapter

    @FragmentArg(ARG_CHARACTER_CLASS)
    protected lateinit var characterClass: CharacterClass
    @JvmField
    @FragmentArg(ARG_ABILITY)
    protected var ability: Ability? = null

    protected lateinit var abilityGalleryModel: AbilityGalleryModel

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        abilityGalleryModel = ViewModelProviders.of(this.activity!!).get(AbilityGalleryModel::class.java)
        if (state == null) {
            abilityGalleryModel.currentIndex.value = ability?.let { characterClass.abilities.indexOf(it) + 1 } ?: 0
        } else {
            abilityGalleryModel.currentIndex.value = state.getInt("index")
        }

        postponeEnterTransition()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        state.putInt("index", abilityGalleryModel.currentIndex.value)

        super.onSaveInstanceState(state)
    }

    @AfterViews
    fun afterViews() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        actionBar = (activity as AppCompatActivity).supportActionBar!!

        val iconId = Util.getImageResource(context!!, "icon_" + characterClass.id)
        actionBar.setLogo(iconId)
        actionBar.title = characterClass.name
        var c = characterClass.color
        val hsv = FloatArray(3)
        Color.colorToHSV(c, hsv)
        hsv[2] = hsv[2] * 2 / 3
        c = Color.HSVToColor(hsv)
        actionBar.setBackgroundDrawable(ColorDrawable(c))

        abilityImagePagerAdapter = AbilityImagePagerAdapter(this, characterClass)

        viewPager.adapter = abilityImagePagerAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                abilityGalleryModel.currentIndex.value = position
            }
        })

        abilityGalleryModel.currentIndex.observe(this, {
            if (viewPager.currentItem != it) {
                viewPager.currentItem = it
            }
        })
    }

    companion object {
        protected const val ARG_CHARACTER_CLASS = "character_class"
        protected const val ARG_ABILITY = "ability"

        fun newInstance(characterClass: CharacterClass, ability: Ability?): AbilityImagePagerFragment_ {
            val args = Bundle()
            args.putParcelable(ARG_CHARACTER_CLASS, characterClass)
            ability?.let { args.putParcelable(ARG_ABILITY, ability) }

            val fragment = AbilityImagePagerFragment_()
            fragment.arguments = args

            return fragment
        }
    }

    @OptionsItem(R.id.gallery)
    fun onGalleryMenuClick() {
        abilityGalleryModel.currentIndex.value = 0
    }
}

class AbilityGalleryModel : ViewModel() {
    val currentIndex = InitLiveData(0)
}