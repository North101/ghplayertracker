package net.north101.android.ghplayertracker.livedata

import net.north101.android.ghplayertracker.AttackStatus
import net.north101.android.ghplayertracker.data.*
import java.util.*

class TrackerLiveData {
    var character: Character
    val health = BoundedIntLiveData(0, minValue = 0)
    val xp = BoundedIntLiveData(0, minValue = 0)
    val loot = BoundedIntLiveData(0, minValue = 0)
    val status = HashMap(Status.values().map {
        it to InitLiveData(false)
    }.toMap())
    val drawDeck = InitLiveData<ArrayList<Card>>(ArrayList())
    val discardDeck = InitLiveData<ArrayList<Card>>(ArrayList())
    val playedCards = InitLiveData<ArrayList<PlayedCards>>(ArrayList())
    val summons = InitLiveData<ArrayList<SummonLiveData>>(ArrayList())

    val shuffle = InitLiveData(false)
    val shuffleCount = InitLiveData(0)
    val attackStatus = InitLiveData(AttackStatus.None)
    val houseRule = InitLiveData(false)

    constructor(character: Character) {
        this.character = character
        this.health.value = character.maxHealth
        this.health.maxValue = character.maxHealth
        this.xp.value = 0
        this.loot.value = 0
        this.status.forEach {
            it.value.value = false
        }
        this.drawDeck.value = ArrayList()
        this.discardDeck.value = ArrayList()
        this.playedCards.value = ArrayList()
        this.summons.value = ArrayList()

        this.shuffle.value = false
        this.shuffleCount.value = 0
        this.attackStatus.value = AttackStatus.None
        this.houseRule.value = false
    }

    constructor(data: Tracker) {
        character = data.character
        health.value = data.health
        health.maxValue = data.character.maxHealth
        xp.value = data.xp
        loot.value = data.loot
        status.putAll(data.status.map {
            it.key to InitLiveData(it.value)
        }.toMap())
        drawDeck.value = data.drawDeck
        discardDeck.value = data.discardDeck
        playedCards.value = data.playedCards
        summons.value = ArrayList(data.summons.map {
            SummonLiveData(it)
        })
        shuffle.value = data.shuffle
        shuffleCount.value = data.shuffleCount
        attackStatus.value = data.attackStatus
        houseRule.value = data.houseRule
    }

    fun toParcel(): Tracker {
        return Tracker(
            character,
            health.value,
            xp.value,
            loot.value,
            HashMap(status.entries.map {
                it.key to it.value.value
            }.toMap()),
            drawDeck.value,
            discardDeck.value,
            playedCards.value,
            ArrayList(summons.value.map {
                it.toParcel()
            }),
            shuffle.value,
            shuffleCount.value,
            attackStatus.value,
            houseRule.value
        )
    }
}