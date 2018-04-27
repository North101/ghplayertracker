package net.north101.android.ghplayertracker

open class TextHeader(var text: String) : RecyclerItemCompare {
    override val compareItemId: String
        get() = text
}