package net.north101.android.ghplayertracker

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import net.north101.android.ghplayertracker.data.CharacterClass

class AbilityImagePagerAdapter(
    val fragment: Fragment,
    private val characterClass: CharacterClass
) : FragmentStatePagerAdapter(fragment.fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            AbilityGalleryFragment.newInstance(characterClass)
        } else {
            val ability = characterClass.abilities[position - 1]
            AbilityImageFragment.newInstance(characterClass.id, ability)
        }
    }

    override fun getCount(): Int {
        return characterClass.abilities.size + 1
    }
}