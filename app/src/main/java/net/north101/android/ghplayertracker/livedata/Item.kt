package net.north101.android.ghplayertracker.livedata

import net.north101.android.ghplayertracker.data.Item
import net.north101.android.ghplayertracker.data.ItemType

class ItemLiveData {
    val name = InitLiveData("")
    val type = InitLiveData(ItemType.Head)

    constructor(name: String, type: ItemType) {
        this.name.value = name
        this.type.value = type
    }

    constructor(data: Item) : this(
        data.name,
        data.type
    )

    fun toParcel(): Item {
        return Item(
            name.value,
            type.value
        )
    }
}