package net.north101.android.ghplayertracker

open class TextHeader(var text: String) : RecyclerItemCompare {
    override val compareItemId: String
        get() = text

    override fun contentsSame(other: RecyclerItemCompare): Boolean {
        return this == other
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TextHeader) return false

        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }
}