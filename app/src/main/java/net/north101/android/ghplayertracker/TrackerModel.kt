package net.north101.android.ghplayertracker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.os.Bundle
import net.north101.android.ghplayertracker.data.*
import net.north101.android.ghplayertracker.livedata.TrackerLiveData

class TrackerModel(
    application: Application
) : AndroidViewModel(application) {
    lateinit var tracker: TrackerLiveData

    val cards = CardsDataLoader(application)

    fun init(character: Character) {
        this.tracker = TrackerLiveData(character)
        this.cards.loadData()

        for ((key, value) in this.cards.value!!.basicDeck) {
            val card = Card[key]
            for (i in 0 until value) {
                tracker.drawDeck.value.add(card)
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
                            tracker.drawDeck.value.add(card)
                        }
                    } else if (perkItem.perkAction == PerkAction.remove) {
                        for (perkItemCount in 0 until perkItem.repeat) {
                            tracker.drawDeck.value.remove(card)
                        }
                    }
                }
            }
        }
    }

    fun fromBundle(bundle: Bundle) {
        this.tracker = TrackerLiveData(bundle.getParcelable<Tracker>("tracker"))
    }

    fun toBundle(): Bundle {
        val bundle = Bundle()

        bundle.putParcelable("tracker", tracker.toParcel())

        return bundle
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