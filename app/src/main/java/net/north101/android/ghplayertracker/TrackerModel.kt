package net.north101.android.ghplayertracker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.data.*
import java.util.*
import kotlin.collections.ArrayList

class TrackerModel(
        application: Application
) : AndroidViewModel(application) {
    var character = InitLiveData<Character>()

    val health = BoundedIntLiveData(minValue = 0)
    val xp = BoundedIntLiveData(minValue = 0)
    val loot = BoundedIntLiveData(minValue = 0)
    val status = HashMap(Status.values().map {
        it to MutableLiveData<Boolean>()
    }.toMap())
    val drawDeck = MutableLiveData<ArrayList<Card>>()
    val discardDeck = MutableLiveData<ArrayList<Card>>()
    val playedCards = MutableLiveData<ArrayList<PlayedCards>>()
    val summons = MutableLiveData<ArrayList<Summon>>()

    val shuffle = MutableLiveData<Boolean>()
    val shuffleCount = MutableLiveData<Int>()
    val attackStatus = MutableLiveData<AttackStatus>()
    val houseRule = MutableLiveData<Boolean>()

    val cards = CardsDataLoader(application)

    fun init(character: Character) {
        this.character.value = character

        this.cards.loadData()

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

        for ((key, value) in this.cards.value!!.basicDeck) {
            val card = Card[key]
            for (i in 0 until value) {
                drawDeck.value!!.add(card)
            }
        }

        for (perkIndex in 0 until character.perks.size) {
            val perk = character.characterClass.perks[perkIndex]
            val perkTicks = character.perks[perkIndex]
            for (perkTick in 0 until perkTicks) {
                for (perkItem in perk.perkItems) {
                    val card = Card[perkItem.cardId]
                    if (perkItem.perkAction == PerkAction.add) {
                        for (perkItemCount in 0 until perkItem.repeat) {
                            drawDeck.value!!.add(card)
                        }
                    } else if (perkItem.perkAction == PerkAction.remove) {
                        for (perkItemCount in 0 until perkItem.repeat) {
                            drawDeck.value!!.remove(card)
                        }
                    }
                }
            }
        }
    }

    fun toData(): Tracker {
        return Tracker(
                character.value!!,
                health.value!!,
                xp.value!!,
                loot.value!!,
                ArrayList(status.map {
                    StatusData(it.key, it.value.value!!)
                }),
                drawDeck.value!!,
                discardDeck.value!!,
                playedCards.value!!,
                ArrayList(summons.value!!.map {
                    SummonData(
                            it.compareItemId,
                            it.name.value!!,
                            it.health.value!!,
                            it.health.maxValue!!,
                            it.move.value!!,
                            it.attack.value!!,
                            it.range.value!!,
                            ArrayList(status.entries.map {
                                StatusData(it.key, it.value.value!!)
                            })
                    )
                }),
                shuffle.value!!,
                shuffleCount.value!!,
                attackStatus.value!!,
                houseRule.value!!
        )
    }

    fun fromData(tracker: Tracker) {
        character.value = tracker.character
        health.value = tracker.health
        health.maxValue = tracker.character.maxHealth
        xp.value = tracker.xp
        loot.value = tracker.loot
        tracker.status.forEach {
            this.status[it.status]!!.value = it.applied
        }
        drawDeck.value = tracker.drawDeck
        discardDeck.value = tracker.discardDeck
        playedCards.value = tracker.playedCards
        summons.value = ArrayList(tracker.summons.map {
            val summon = Summon(it.id, this)
            summon.name.value = it.name
            summon.health.value = it.health
            summon.health.maxValue = it.maxHealth
            summon.move.value = it.move
            summon.attack.value = it.attack
            summon.range.value = it.range
            it.status.forEach {
                summon.status[it.status]!!.value = it.applied
            }
            summon
        })
        shuffle.value = tracker.shuffle
        shuffleCount.value = tracker.shuffleCount
        attackStatus.value = tracker.attackStatus
        houseRule.value = tracker.houseRule
    }
}

@Parcelize
data class Tracker(
        var character: Character,
        var health: Int,
        var xp: Int,
        var loot: Int,
        var status: ArrayList<StatusData>,
        var drawDeck: ArrayList<Card>,
        var discardDeck: ArrayList<Card>,
        var playedCards: ArrayList<PlayedCards>,
        var summons: ArrayList<SummonData>,
        var shuffle: Boolean,
        var shuffleCount: Int,
        var attackStatus: AttackStatus,
        var houseRule: Boolean
) : Parcelable


@Parcelize
data class StatusData(
        val status: Status,
        val applied: Boolean
) : Parcelable


class Summon(
        override val compareItemId: String,
        val tracker: TrackerModel
) : RecyclerItemCompare {
    val name = MutableLiveData<String>()
    val health = BoundedIntLiveData(minValue = 0)
    val move = MutableLiveData<Int>()
    val attack = MutableLiveData<Int>()
    val range = MutableLiveData<Int>()
    val status: HashMap<Status, MutableLiveData<Boolean>> = HashMap(Status.values().map {
        it to MutableLiveData<Boolean>()
    }.toMap())
}


@Parcelize
data class SummonData(
        val id: String,
        val name: String,
        val health: Int,
        val maxHealth: Int,
        val move: Int,
        val attack: Int,
        val range: Int,
        val status: ArrayList<StatusData>
) : Parcelable


open class BoundedIntLiveData(
        value: Int? = null,
        var minValue: Int? = null,
        var maxValue: Int? = null
) : InitLiveData<Int>(value) {
    override fun setValue(value: Int?) {
        var newValue = value
        if (newValue != null) {
            if (minValue != null) {
                newValue = Math.max(newValue, minValue!!)
            }
            if (maxValue != null) {
                newValue = Math.min(newValue, maxValue!!)
            }
        }

        super.setValue(newValue)
    }
}


class CardsDataLoader(
        val context: Context
) : MutableLiveData<BasicCards>() {
    override fun getValue(): BasicCards? {
        var value = super.getValue()
        if (value == null) {
            value = BasicCards.load(context)
            this.value = value
        }
        return value
    }

    fun loadData() {
        this.value = BasicCards.load(context)
    }
}