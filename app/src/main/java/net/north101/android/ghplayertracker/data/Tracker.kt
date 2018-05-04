package net.north101.android.ghplayertracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import net.north101.android.ghplayertracker.AttackStatus
import java.util.ArrayList
import java.util.HashMap

@Parcelize
data class Tracker(
    var character: Character,
    var health: Int,
    var xp: Int,
    var loot: Int,
    var status: HashMap<Status, Boolean>,
    var drawDeck: ArrayList<Card>,
    var discardDeck: ArrayList<Card>,
    var playedCards: ArrayList<PlayedCards>,
    var summons: ArrayList<Summon>,
    var shuffle: Boolean,
    var shuffleCount: Int,
    var attackStatus: AttackStatus,
    var houseRule: Boolean
) : Parcelable