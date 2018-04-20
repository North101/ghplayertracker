package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class CharacterTracker(
        val character: Character,
        var _health: Int,
        var _xp: Int,
        var _gold: Int,
        val statusSet: StatusSet,
        val deck: ArrayList<Card>,
        val playedCardsHistory: ArrayList<PlayedCards>
) : Parcelable {
    constructor(character: Character, basicCards: BasicCards) : this(character, character.currentLevel.health, 0, 0, StatusSet(), ArrayList<Card>(), ArrayList<PlayedCards>()) {
        for ((key, value) in basicCards.basicDeck) {
            val card = Card.get(key)
            for (i in 0 until value) {
                deck.add(card)
            }
        }

        val minus1Card = Card.get("mod_extra_minus_1")
        for (i in 0 until character.minus1) {
            deck.add(minus1Card)
        }

        for (perkIndex in 0 until character.perks.size) {
            val perk = character.characterClass.perks[perkIndex]
            val perkTicks = character.perks[perkIndex]
            for (perkTick in 0 until perkTicks) {
                for (perkItem in perk.perkItems) {
                    val card = Card[perkItem.cardId]
                    if (perkItem.perkAction == PerkAction.add) {
                        for (perkItemCount in 0 until perkItem.repeat) {
                            deck.add(card)
                        }
                    } else if (perkItem.perkAction == PerkAction.remove) {
                        for (perkItemCount in 0 until perkItem.repeat) {
                            deck.remove(card)
                        }
                    }
                }
            }
        }
    }

    @IgnoredOnParcel
    var health: Int
        get() = _health
        set(value) {
            _health = Math.min(Math.max(value, 0), character.currentLevel.health)
        }

    @IgnoredOnParcel
    var xp: Int
        get() = _xp
        set(value) {
            _xp = Math.max(value, 0)
        }

    @IgnoredOnParcel
    var gold: Int
        get() = _gold
        set(value) {
            _gold = Math.max(value, 0)
        }
}
