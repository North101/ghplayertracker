package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import net.north101.android.ghplayertracker.data.Card
import net.north101.android.ghplayertracker.data.CardSpecial
import net.north101.android.ghplayertracker.data.PlayedCards
import net.north101.android.ghplayertracker.livedata.TrackerLiveData
import java.util.*
import kotlin.collections.ArrayList

class TrackerDeckViewHolder(itemView: View) : BaseViewHolder<TrackerLiveData>(itemView) {
    var blessContainerView: View = itemView.findViewById(R.id.bless_container)
    var blessTextView: TextView = itemView.findViewById(R.id.bless_text)
    var blessPlusView: View = itemView.findViewById(R.id.bless_plus)
    var blessMinusView: View = itemView.findViewById(R.id.bless_minus)

    var curseContainerView: View = itemView.findViewById(R.id.curse_container)
    var curseTextView: TextView = itemView.findViewById(R.id.curse_text)
    var cursePlusView: View = itemView.findViewById(R.id.curse_plus)
    var curseMinusView: View = itemView.findViewById(R.id.curse_minus)

    var minus1ContainerView: View = itemView.findViewById(R.id.minus_1_container)
    var minus1TextView: TextView = itemView.findViewById(R.id.minus_1_text)
    var minus1PlusView: View = itemView.findViewById(R.id.minus_1_plus)
    var minus1MinusView: View = itemView.findViewById(R.id.minus_1_minus)

    val deckView: ImageView = itemView.findViewById(R.id.draw_deck)
    val advantageView: ImageView = itemView.findViewById(R.id.advantage)
    val disadvantageView: ImageView = itemView.findViewById(R.id.disadvantage)
    val shuffleView: ImageView = itemView.findViewById(R.id.shuffle)

    var onNumberClickListener: ((String) -> Unit)? = null

    val blessCard: Card
        get() = Card["mod_extra_double_bless_remove"]
    val curseCard: Card
        get() = Card["mod_extra_null_curse_remove"]
    val minus1Card: Card
        get() = Card["mod_extra_minus_1"]

    var shuffle: Boolean
        get() = item!!.shuffle.value
        set(value) {
            item!!.shuffle.value = value
        }

    var shuffleCount: Int
        get() = item!!.shuffleCount.value
        set(value) {
            item!!.shuffleCount.value = value
        }

    var drawDeck: ArrayList<Card>
        get() = item!!.drawDeck.value
        set(value) {
            item!!.drawDeck.value = value
        }

    var discardDeck: ArrayList<Card>
        get() = item!!.discardDeck.value
        set(value) {
            item!!.discardDeck.value = value
        }

    var playedCards: ArrayList<PlayedCards>
        get() = item!!.playedCards.value
        set(value) {
            item!!.playedCards.value = value
        }

    var attackStatus: AttackStatus
        get() = item!!.attackStatus.value
        set(value) {
            item!!.attackStatus.value = value
        }

    var houseRule: Boolean
        get() = item!!.houseRule.value
        set(value) {
            item!!.houseRule.value = value
        }

    init {
        blessContainerView.setOnClickListener {
            onNumberClickListener?.invoke("bless")
        }
        blessPlusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            drawDeck.add(blessCard)
            updateBlessText()
        })))
        blessMinusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            drawDeck.remove(blessCard)
            updateBlessText()
        })))

        curseContainerView.setOnClickListener {
            onNumberClickListener?.invoke("curse")
        }
        cursePlusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            drawDeck.add(curseCard)
            updateCurseText()
        })))
        curseMinusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            drawDeck.remove(curseCard)
            updateCurseText()
        })))

        minus1ContainerView.setOnClickListener {
            onNumberClickListener?.invoke("minus_1")
        }
        minus1PlusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            drawDeck.add(minus1Card)
            updateMinus1Text()
        })))
        minus1MinusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            if (drawDeck.contains(minus1Card)) {
                drawDeck.remove(minus1Card)
            } else if (discardDeck.contains(minus1Card)) {
                discardDeck.remove(minus1Card)
            }
            updateMinus1Text()
        })))

        advantageView.setOnClickListener {
            attackStatus = if (attackStatus == AttackStatus.Advantage) {
                AttackStatus.None
            } else {
                AttackStatus.Advantage
            }
        }
        disadvantageView.setOnClickListener {
            attackStatus = if (attackStatus == AttackStatus.Disadvantage) {
                AttackStatus.None
            } else {
                AttackStatus.Disadvantage
            }
        }
        deckView.setOnClickListener {
            draw()
        }
        shuffleView.setOnClickListener {
            shuffle()
        }
    }

    val shuffleObserver: (Boolean) -> Unit = {
        updateShuffle()
    }

    val attackStatusObserver: (AttackStatus) -> Unit = {
        updateAttackStatus()
    }

    val deckObserver: (ArrayList<Card>) -> Unit = {
        updateBlessText()
        updateCurseText()
        updateMinus1Text()
        setImageViewGreyscale(deckView, it.isEmpty())
    }

    override fun bind(item: TrackerLiveData) {
        super.bind(item)

        item.shuffle.observeForever(shuffleObserver)
        item.attackStatus.observeForever(attackStatusObserver)
        item.drawDeck.observeForever(deckObserver)
    }

    override fun unbind() {
        item?.let {
            item!!.shuffle.removeObserver(shuffleObserver)
            item!!.attackStatus.removeObserver(attackStatusObserver)
            item!!.drawDeck.removeObserver(deckObserver)
        }

        super.unbind()
    }

    companion object {
        var layout = R.layout.character_tracker_deck_item

        fun inflate(parent: ViewGroup): TrackerDeckViewHolder {
            return TrackerDeckViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }

    fun updateBlessText() {
        val count = (drawDeck + discardDeck).count { it == blessCard }
        blessTextView.text = count.toString()
    }

    fun updateCurseText() {
        val count = (drawDeck + discardDeck).count { it == curseCard }
        curseTextView.text = count.toString()
    }

    fun updateMinus1Text() {
        val count = (drawDeck + discardDeck).count { it == minus1Card }
        minus1TextView.text = count.toString()
    }

    fun updateAttackStatus() {
        setImageViewGreyscale(advantageView, attackStatus != AttackStatus.Advantage)
        setImageViewGreyscale(disadvantageView, attackStatus != AttackStatus.Disadvantage)
    }

    fun updateShuffle() {
        shuffleView.isEnabled = shuffle
        setImageViewGreyscale(shuffleView, !shuffle)
    }

    fun shuffle() {
        shuffle = false

        shuffleCount += 1
        for (playedCards in playedCards) {
            playedCards.shuffledIndex = shuffleCount
        }

        drawDeck.addAll(discardDeck)
        discardDeck.clear()

        drawDeck = drawDeck
        discardDeck = discardDeck
        playedCards = playedCards

        Toast.makeText(itemView.context, "Shuffled", Toast.LENGTH_SHORT).show()
    }

    fun draw() {
        val playedCards = if (attackStatus == AttackStatus.Advantage) {
            if (houseRule) {
                drawHouse()
            } else {
                drawAdvantage()
            }
        } else if (attackStatus == AttackStatus.Disadvantage) {
            if (houseRule) {
                drawHouse()
            } else {
                drawDisadvantage()
            }
        } else {
            drawNormal()
        }
        if (playedCards.hasShuffle()) {
            shuffle = true
        }

        for (card in playedCards.pile1) {
            if (card.special != CardSpecial.Remove) {
                discardDeck.add(card)
            }
        }
        if (playedCards.pile2 != null) {
            for (card in playedCards.pile2) {
                if (card.special != CardSpecial.Remove) {
                    discardDeck.add(card)
                }
            }
        }

        this.playedCards.add(playedCards)
        this.playedCards = this.playedCards
        drawDeck = drawDeck
    }

    fun drawNormal(): PlayedCards {
        return PlayedCards(drawCards(), null, null)
    }

    fun drawAdvantage(): PlayedCards {
        val item1 = drawCards()
        val item2 = ArrayList<Card>()
        if (item1.count() < 2) {
            val card = drawCard()
            if (card.special == CardSpecial.Rolling) {
                item1.add(card)
            } else {
                item2.add(card)
            }
        }
        return PlayedCards(item1, item2, null)
    }

    fun drawDisadvantage(): PlayedCards {
        val item1 = ArrayList<Card>()
        val item2 = ArrayList<Card>()
        val card1 = drawCard()
        val card2 = drawCard()

        item1.add(card1)
        if (card1.special == CardSpecial.Rolling && card2.special == CardSpecial.Rolling) {
            item1.add(card2)
            while (true) {
                val card = drawCard()
                item1.add(card)
                if (card.special != CardSpecial.Rolling)
                    break
            }
        } else {
            item2.add(card2)
        }
        return PlayedCards(item1, item2, null)
    }

    fun drawHouse(): PlayedCards {
        return PlayedCards(drawCards(), drawCards(), null)
    }

    fun drawCards(): ArrayList<Card> {
        val cards = ArrayList<Card>()

        while (true) {
            val card = drawCard()

            cards.add(card)
            if (card.special != CardSpecial.Rolling)
                break
        }

        return cards
    }

    var random = Random()
    fun drawCard(): Card {
        if (drawDeck.isEmpty()) {
            shuffle()
        }

        val index = random.nextInt(drawDeck.size)
        return drawDeck.removeAt(index)
    }
}
