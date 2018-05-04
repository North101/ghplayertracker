package net.north101.android.ghplayertracker.livedata

class PerkNoteLiveData(value: Int = 0) : BoundedIntLiveData(value, minValue = 0, maxValue = 3)