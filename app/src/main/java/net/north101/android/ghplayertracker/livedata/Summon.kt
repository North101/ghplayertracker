package net.north101.android.ghplayertracker.livedata

import net.north101.android.ghplayertracker.RecyclerItemCompare
import net.north101.android.ghplayertracker.data.Status
import net.north101.android.ghplayertracker.data.Summon
import java.util.*

class SummonLiveData : RecyclerItemCompare {
    val id: UUID
    val name = InitLiveData("")
    val health = BoundedIntLiveData(0, minValue = 0, maxValue = 1)
    val move = InitLiveData(0)
    val attack = InitLiveData(0)
    val range = InitLiveData(0)
    val status = HashMap(Status.values().map {
        it to InitLiveData(false)
    }.toMap())

    constructor(
        id: UUID,
        name: String,
        health: Int,
        maxHealth: Int,
        move: Int,
        attack: Int,
        range: Int,
        status: HashMap<Status, Boolean>
    ) {
        this.id = id
        this.name.value = name
        this.health.value = health
        this.health.maxValue = maxHealth
        this.move.value = move
        this.attack.value = attack
        this.range.value = range
        this.status.putAll(status.map {
            it.key to InitLiveData(it.value)
        }.toMap())
    }

    constructor(data: Summon) : this(
        data.id,
        data.name,
        data.health,
        data.maxHealth,
        data.move,
        data.attack,
        data.range,
        data.status
    )

    override val compareItemId: String
        get() = id.toString()

    fun toParcel(): Summon {
        return Summon(
            id,
            name.value,
            health.value,
            health.maxValue!!,
            move.value,
            attack.value,
            range.value,
            HashMap(status.entries.map {
                it.key to it.value.value
            }.toMap())
        )
    }
}

