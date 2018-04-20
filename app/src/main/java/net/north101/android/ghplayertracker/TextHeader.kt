package net.north101.android.ghplayertracker

data class TextHeader(var text: String) : RecyclerItemCompare {
    override val compareItemId: String
        get() = text
}