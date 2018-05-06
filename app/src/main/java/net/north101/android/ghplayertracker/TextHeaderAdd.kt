package net.north101.android.ghplayertracker

open class TextHeaderAdd(
    text: String,
    val onItemAddClick: () -> Unit,
    val icon: Int = R.drawable.ic_add_circle_black_24dp
) : TextHeader(text)