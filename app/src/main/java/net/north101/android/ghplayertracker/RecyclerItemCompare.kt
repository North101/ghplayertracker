package net.north101.android.ghplayertracker

interface RecyclerItemCompare {
    val compareItemId: String

    fun contentsSame(other: RecyclerItemCompare): Boolean {
        return this === other
    }
}