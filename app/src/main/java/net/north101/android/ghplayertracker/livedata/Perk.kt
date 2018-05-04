package net.north101.android.ghplayertracker.livedata

import net.north101.android.ghplayertracker.data.Perk

class PerkLiveData(
    val perk: Perk,
    value: Int = 0
) : InitLiveData<Int>(value)