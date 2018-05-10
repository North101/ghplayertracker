package net.north101.android.ghplayertracker

import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import net.north101.android.ghplayertracker.data.CharacterClass
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentArg
import org.androidannotations.annotations.ViewById

@EFragment(R.layout.ability_gallery_layout)
open class AbilityGalleryFragment : Fragment() {
    @ViewById(R.id.recycler_view)
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var adapter: AbilityGalleryAdapter

    @FragmentArg(ARG_CHARACTER_CLASS)
    protected lateinit var characterClass: CharacterClass

    protected lateinit var abilityGalleryModel: AbilityGalleryModel

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        abilityGalleryModel = ViewModelProviders.of(this.activity!!).get(AbilityGalleryModel::class.java)
    }

    @AfterViews
    fun afterViews() {
        if (!::adapter.isInitialized) {
            adapter = AbilityGalleryAdapter()
        }
        adapter.onImageItemClick = {
            abilityGalleryModel.currentIndex.value = characterClass.abilities.indexOf(it) + 1
        }

        val landscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val galleryLayoutManager = GridLayoutManager(context, if (landscape) 4 else 2)
        galleryLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = adapter.items[position]
                return if (item is AbilityGalleryAdapter.ClassAbility) {
                    1
                } else if (!landscape) {
                    2
                } else {
                    val nextItem = adapter.items[position + 1]
                    if (nextItem is AbilityGalleryAdapter.ClassAbility && nextItem.ability?.level == 1)
                        4
                    else
                        2
                }
            }
        }

        recyclerView.layoutManager = galleryLayoutManager
        recyclerView.adapter = adapter

        adapter.updateItems(characterClass, if (landscape) 4 else 2)
    }

    companion object {
        val TAG = AbilityGalleryFragment::class.java.simpleName!!

        protected const val ARG_CHARACTER_CLASS = "character_class"

        fun newInstance(characterClass: CharacterClass): AbilityGalleryFragment_ {
            val args = Bundle()
            args.putParcelable(ARG_CHARACTER_CLASS, characterClass)

            val fragment = AbilityGalleryFragment_()
            fragment.arguments = args

            return fragment
        }
    }
}