package net.north101.android.ghplayertracker

import android.content.Intent
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.ColorDrawable
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import net.north101.android.ghplayertracker.data.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import org.androidannotations.annotations.*
import org.androidannotations.annotations.sharedpreferences.Pref
import org.json.JSONException
import java.io.IOException
import java.util.*

@OptionsMenu(R.menu.character_tracker)
@EFragment(R.layout.character_tracker_layout)
open class CharacterTrackerFragment : Fragment() {
    var randomGenerator = Random()

    protected lateinit var actionBar: ActionBar
    @ViewById(R.id.toolbar)
    protected lateinit var toolbar: Toolbar
    @ViewById(R.id.health_text)
    protected lateinit var healthTextView: EditText
    @ViewById(R.id.health_plus)
    protected lateinit var healthPlusView: ImageView
    @ViewById(R.id.health_minus)
    protected lateinit var healthMinusView: ImageView
    @ViewById(R.id.xp_text)
    protected lateinit var xpTextView: EditText
    @ViewById(R.id.xp_plus)
    protected lateinit var xpPlusView: ImageView
    @ViewById(R.id.xp_minus)
    protected lateinit var xpMinusView: ImageView
    @ViewById(R.id.gold_text)
    protected lateinit var goldTextView: EditText
    @ViewById(R.id.gold_plus)
    protected lateinit var goldPlusView: ImageView
    @ViewById(R.id.gold_minus)
    protected lateinit var goldMinusView: ImageView
    @ViewById(R.id.draw_deck)
    protected lateinit var drawDeckView: ImageView
    @ViewById(R.id.status_disarm)
    protected lateinit var statusDisarmView: ImageView
    @ViewById(R.id.status_stun)
    protected lateinit var statusStunView: ImageView
    @ViewById(R.id.status_immobilize)
    protected lateinit var statusImmobilizeView: ImageView
    @ViewById(R.id.status_strengthen)
    protected lateinit var statusStrengthenView: ImageView
    @ViewById(R.id.status_poison)
    protected lateinit var statusPoisonView: ImageView
    @ViewById(R.id.status_wound)
    protected lateinit var statusWoundView: ImageView
    @ViewById(R.id.status_muddle)
    protected lateinit var statusMuddleView: ImageView
    @ViewById(R.id.status_invisible)
    protected lateinit var statusInvisibleView: ImageView
    @ViewById(R.id.bless_text)
    protected lateinit var blessTextView: EditText
    @ViewById(R.id.bless_plus)
    protected lateinit var blessPlusView: ImageView
    @ViewById(R.id.bless_minus)
    protected lateinit var blessMinusView: ImageView
    @ViewById(R.id.curse_text)
    protected lateinit var curseTextView: EditText
    @ViewById(R.id.curse_plus)
    protected lateinit var cursePlusView: ImageView
    @ViewById(R.id.curse_minus)
    protected lateinit var curseMinusView: ImageView
    @ViewById(R.id.split)
    protected lateinit var splitIconView: ImageView
    @ViewById(R.id.shuffle)
    protected lateinit var shuffleIconView: ImageView
    @ViewById(R.id.active_deck_list)
    protected lateinit var playedCardsListView: RecyclerView

    @OptionsMenuItem(R.id.houseRuleVantage)
    protected lateinit var houseRuleVantageMenu: MenuItem

    protected lateinit var playedCardsAdapter: PlayedCardsAdapter

    @FragmentArg("character")
    protected lateinit var character: Character

    @InstanceState
    protected lateinit var characterTracker: CharacterTracker
    @JvmField
    @InstanceState
    protected var basicCards: BasicCards? = null
    @JvmField
    @InstanceState
    protected var split = false
    @JvmField
    @InstanceState
    protected var shuffle = false
    @InstanceState
    protected lateinit var blessCard: Card
    @InstanceState
    protected lateinit var curseCard: Card
    @JvmField
    @InstanceState
    protected var shuffleCount = 0

    @Pref
    protected lateinit var sharedPrefs: SharedPrefs_

    protected var keyboardEventListener: Unregistrar? = null

    @AfterViews
    fun afterViews() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        actionBar = (activity as AppCompatActivity).supportActionBar!!

        if (basicCards == null) {
            try {
                basicCards = BasicCards.load(context!!)
            } catch (e: IOException) {
                e.printStackTrace()
                return
            } catch (e: JSONException) {
                e.printStackTrace()
                return
            }

            blessCard = Card["mod_extra_double_bless_remove"]
            curseCard = Card["mod_extra_null_curse_remove"]
        }

        if (!this::characterTracker.isInitialized) {
            characterTracker = CharacterTracker(character, basicCards!!)
        }

        val iconId = Util.getImageResource(context!!, "icon_" + character.characterClass.id)
        actionBar.setLogo(iconId)
        actionBar.title = character.name
        var c = character.characterClass.color
        val hsv = FloatArray(3)
        Color.colorToHSV(c, hsv)
        hsv[2] = hsv[2] * 2 / 3
        c = Color.HSVToColor(hsv)
        actionBar.setBackgroundDrawable(ColorDrawable(c))

        updateHealthText()
        updateXPText()
        updateGoldText()
        updateStatusView()
        updateBlessText()
        updateCurseText()

        playedCardsAdapter = PlayedCardsAdapter()
        val activeDeckListLayoutManager = GridLayoutManager(context, 2)
        activeDeckListLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = playedCardsAdapter.getItem(position)
                return if (item is PlayedCardsAdapter.CardInfo) {
                    if (item.split) 1 else 2
                } else if (item is PlayedCardsAdapter.CardDivider) {
                    2
                } else if (item is TextHeader) {
                    2
                } else {
                    throw RuntimeException()
                }
            }
        }

        val animator = SlideDownAlphaAnimator()
        playedCardsListView.itemAnimator = animator
        playedCardsListView.layoutManager = activeDeckListLayoutManager

        playedCardsListView.adapter = playedCardsAdapter
        playedCardsAdapter.updateItems(characterTracker.playedCardsHistory)

        setSplit(split)
        setShuffleEnabled(shuffle)
        updateActiveDecks()
        ViewCompat.setNestedScrollingEnabled(playedCardsListView, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        updateHouseRuleVantageMenu()
    }

    override fun onResume() {
        super.onResume()

        //HACK!
        keyboardEventListener = KeyboardVisibilityEvent.registerEventListener(activity!!, KeyboardVisibilityEventListener { isOpen ->
            if (!isOpen) {
                val activity = activity as AppCompatActivity?
                        ?: return@KeyboardVisibilityEventListener

                val view = activity.currentFocus
                if (view is EditText) {
                    view.clearFocus()
                    view.requestFocus()
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if (keyboardEventListener != null) {
            keyboardEventListener!!.unregister()
            keyboardEventListener = null
        }
    }

    @OptionsItem(R.id.complete)
    fun onCompleteClick() {
        parseXPText()
        parseGoldText()

        val intent = Intent()
        intent.putExtra("xp", characterTracker.xp)
        intent.putExtra("gold", characterTracker.gold)
        targetFragment!!.onActivityResult(this.targetRequestCode, AppCompatActivity.RESULT_OK, intent)
        fragmentManager!!.popBackStack()
    }

    @OptionsItem(R.id.houseRuleVantage)
    fun onHomebrewVantageClick() {
        sharedPrefs.houseRuleVantage().put(!sharedPrefs.houseRuleVantage().get())
        updateHouseRuleVantageMenu()
    }

    fun updateHouseRuleVantageMenu() {
        houseRuleVantageMenu.isChecked = sharedPrefs.houseRuleVantage().get()
    }

    @Click(R.id.status_disarm)
    fun onStatusDisarmClick() {
        characterTracker.statusSet.disarm = !characterTracker.statusSet.disarm
        updateStatusDisarmView()
    }

    fun updateStatusDisarmView() {
        setImageViewGreyscale(this.statusDisarmView, !characterTracker.statusSet.disarm)
    }

    @Click(R.id.status_stun)
    fun onStatusStunClick() {
        characterTracker.statusSet.stun = !characterTracker.statusSet.stun
        updateStatusStunView()
    }

    fun updateStatusStunView() {
        setImageViewGreyscale(this.statusStunView, !characterTracker.statusSet.stun)
    }

    @Click(R.id.status_immobilize)
    fun onStatusImmobilizeClick() {
        characterTracker.statusSet.immobilize = !characterTracker.statusSet.immobilize
        updateStatusImmobilizeView()
    }

    fun updateStatusImmobilizeView() {
        setImageViewGreyscale(this.statusImmobilizeView, !characterTracker.statusSet.immobilize)
    }

    @Click(R.id.status_poison)
    fun onStatusPoisonClick() {
        characterTracker.statusSet.poison = !characterTracker.statusSet.poison
        updateStatusPoisonView()
    }

    fun updateStatusPoisonView() {
        setImageViewGreyscale(this.statusPoisonView, !characterTracker.statusSet.poison)
    }

    @Click(R.id.status_wound)
    fun onStatusWoundClick() {
        characterTracker.statusSet.wound = !characterTracker.statusSet.wound
        updateStatusWoundView()
    }

    fun updateStatusWoundView() {
        setImageViewGreyscale(this.statusWoundView, !characterTracker.statusSet.wound)
    }

    @Click(R.id.status_muddle)
    fun onStatusMuddleClick() {
        characterTracker.statusSet.muddle = !characterTracker.statusSet.muddle
        updateStatusMuddleView()
    }

    fun updateStatusMuddleView() {
        setImageViewGreyscale(this.statusMuddleView, !characterTracker.statusSet.muddle)
    }

    @Click(R.id.status_strengthen)
    fun onStatusStrengthenClick() {
        characterTracker.statusSet.strengthen = !characterTracker.statusSet.strengthen
        updateStatusStrengthenView()
    }

    fun updateStatusStrengthenView() {
        setImageViewGreyscale(this.statusStrengthenView, !characterTracker.statusSet.strengthen)
    }

    @Click(R.id.status_invisible)
    fun onStatusInvisibleClick() {
        characterTracker.statusSet.invisible = !characterTracker.statusSet.invisible
        updateStatusInvisibleView()
    }

    fun updateStatusInvisibleView() {
        setImageViewGreyscale(this.statusInvisibleView, !characterTracker.statusSet.invisible)
    }

    fun updateStatusView() {
        updateStatusDisarmView()
        updateStatusStunView()
        updateStatusImmobilizeView()
        updateStatusPoisonView()
        updateStatusWoundView()
        updateStatusMuddleView()
        updateStatusStrengthenView()
        updateStatusInvisibleView()
    }

    fun setImageViewGreyscale(imageView: ImageView?, set: Boolean) {
        if (set) {
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)  //0 means grayscale
            val cf = ColorMatrixColorFilter(matrix)
            imageView!!.colorFilter = cf
            imageView.alpha = 0.5f
        } else {
            imageView!!.colorFilter = null
            imageView.alpha = 1.0f
        }
    }

    @Click(R.id.draw_deck)
    fun onDrawDeckClicked() {
        val item1 = drawCards()
        val item2: ArrayList<Card>?
        if (split) {
            if (sharedPrefs.houseRuleVantage().get()) {
                item2 = drawCards()
            } else {
                item2 = ArrayList()
                if (!hasSpecial(item1, CardSpecial.Rolling)) {
                    val splitCard = drawCard()
                    if (splitCard != null) {
                        if (splitCard.special == CardSpecial.Rolling) {
                            item1.add(splitCard)
                        } else {
                            item2.add(splitCard)
                        }
                    }
                }
            }
        } else {
            item2 = null
        }
        if (hasSpecial(item1, CardSpecial.Shuffle) || item2 != null && hasSpecial(item2, CardSpecial.Shuffle)) {
            setShuffleEnabled(true)
        }
        characterTracker.playedCardsHistory.add(0, PlayedCards(item1, item2, null))
        playedCardsAdapter.updateItems(characterTracker.playedCardsHistory)
        updateActiveDecks()
    }

    @Click(R.id.split)
    fun onSplitIconClick() {
        toggleSplit()
    }

    @Click(R.id.shuffle)
    fun onShuffleIconClick() {
        shuffle()
    }

    @Click(R.id.health_minus)
    fun onHealthMinusClick() {
        characterTracker.health = characterTracker.health - 1
        updateHealthText()
    }

    @Click(R.id.health_plus)
    fun onHealthPlusClick() {
        characterTracker.health = characterTracker.health + 1
        updateHealthText()
    }

    @EditorAction(R.id.health_text)
    fun onHealthTextChange(tv: TextView, actionId: Int): Boolean {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false

        parseHealthText()
        return false
    }

    @FocusChange(R.id.health_text)
    fun onHealthTextFocus() {
        if (!this::healthTextView.isInitialized || healthTextView.hasFocus()) return

        parseHealthText()
    }

    fun parseHealthText() {
        try {
            characterTracker.health = Integer.parseInt(healthTextView.text.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Invalid Health value", Toast.LENGTH_SHORT).show()
        }

        updateHealthText()
    }

    fun updateHealthText() {
        val text = characterTracker.health.toString()
        if (text != healthTextView.text.toString()) {
            healthTextView.setText(text)
        }
    }

    @Click(R.id.xp_minus)
    fun onXPMinusClick() {
        characterTracker.xp = characterTracker.xp - 1
        updateXPText()
    }

    @Click(R.id.xp_plus)
    fun onXPPlusClick() {
        characterTracker.xp = characterTracker.xp + 1
        updateXPText()
    }

    @EditorAction(R.id.xp_text)
    fun onXPTextChange(tv: TextView, actionId: Int): Boolean {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false

        parseXPText()
        return false
    }

    @FocusChange(R.id.xp_text)
    fun onXPTextFocus() {
        if (!this::xpTextView.isInitialized || xpTextView.hasFocus()) return

        parseXPText()
    }

    fun parseXPText() {
        try {
            characterTracker.xp = Integer.parseInt(xpTextView.text.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Invalid XP value", Toast.LENGTH_SHORT).show()
        }

        updateXPText()
    }

    fun updateXPText() {
        val text = characterTracker.xp.toString()
        if (text != xpTextView.text.toString()) {
            xpTextView.setText(text)
        }
    }

    @Click(R.id.gold_minus)
    fun onGoldMinusClick() {
        characterTracker.gold = characterTracker.gold - 1
        updateGoldText()
    }

    @Click(R.id.gold_plus)
    fun onGoldPlusClick() {
        characterTracker.gold = characterTracker.gold + 1
        updateGoldText()
    }

    @EditorAction(R.id.gold_text)
    fun onGoldTextChange(tv: TextView, actionId: Int): Boolean {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false

        parseGoldText()
        return false
    }

    @FocusChange(R.id.gold_text)
    fun onGoldTextFocus() {
        if (!this::goldTextView.isInitialized || goldTextView.hasFocus()) return

        parseGoldText()
    }

    fun parseGoldText() {
        try {
            characterTracker.gold = Integer.parseInt(goldTextView.text.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Invalid Gold value", Toast.LENGTH_SHORT).show()
        }

        updateGoldText()
    }

    fun updateGoldText() {
        val text = characterTracker.gold.toString()
        if (text != goldTextView.text.toString()) {
            goldTextView.setText(text)
        }
    }

    @Click(R.id.bless_minus)
    fun onBlessMinusClick() {
        characterTracker.deck.remove(blessCard)
        updateBlessText()
    }

    @Click(R.id.bless_plus)
    fun onBlessPlusClick() {
        characterTracker.deck.add(blessCard)
        updateBlessText()
    }

    @EditorAction(R.id.bless_text)
    fun onBlessTextChange(tv: TextView, actionId: Int): Boolean {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false

        parseBlessText()
        return false
    }

    @FocusChange(R.id.bless_text)
    fun onBlessTextFocus() {
        if (!this::blessTextView.isInitialized || blessTextView.hasFocus()) return

        parseBlessText()
    }

    fun parseBlessText() {
        try {
            val newCount = Math.max(Integer.parseInt(blessTextView.text.toString()), 0)

            var oldCount = 0
            for (card in characterTracker.deck) {
                if (card == blessCard) {
                    oldCount++
                }
            }

            while (oldCount > newCount) {
                characterTracker.deck.remove(blessCard)
                oldCount--
            }
            while (oldCount < newCount) {
                characterTracker.deck.add(blessCard)
                oldCount++
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Invalid Bless Card count", Toast.LENGTH_SHORT).show()
        }

        updateBlessText()
    }

    fun updateBlessText() {
        var count = 0
        for (card in characterTracker.deck) {
            if (card == blessCard) {
                count++
            }
        }
        val text = count.toString()
        if (text != blessTextView.text.toString()) {
            blessTextView.setText(count.toString())
        }
    }

    @Click(R.id.curse_minus)
    fun onCurseMinusClick() {
        characterTracker.deck.remove(curseCard)
        updateCurseText()
    }

    @Click(R.id.curse_plus)
    fun onCursePlusClick() {
        characterTracker.deck.add(curseCard)
        updateCurseText()
    }

    @EditorAction(R.id.curse_text)
    fun onCurseTextChange(tv: TextView, actionId: Int): Boolean {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false

        parseCurseText()
        return false
    }

    @FocusChange(R.id.curse_text)
    fun onCurseTextFocus() {
        if (!this::curseTextView.isInitialized || curseTextView.hasFocus()) return

        parseCurseText()
    }

    fun parseCurseText() {
        try {
            val newCount = Math.max(Integer.parseInt(curseTextView.text.toString()), 0)

            var oldCount = 0
            for (card in characterTracker.deck) {
                if (card == curseCard) {
                    oldCount++
                }
            }

            while (oldCount > newCount) {
                characterTracker.deck.remove(curseCard)
                oldCount--
            }
            while (oldCount < newCount) {
                characterTracker.deck.add(curseCard)
                oldCount++
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Invalid Curse Card count", Toast.LENGTH_SHORT).show()
        }

        updateCurseText()
    }

    fun updateCurseText() {
        var count = 0
        for (card in characterTracker.deck) {
            if (card == curseCard) {
                count++
            }
        }
        val text = count.toString()
        if (text != curseTextView.text.toString()) {
            curseTextView.setText(count.toString())
        }
    }

    fun toggleSplit() {
        setSplit(!this.split)
    }

    fun setSplit(split: Boolean) {
        this.split = split
        if (split) {
            splitIconView.setImageResource(R.drawable.ic_call_split_black_24dp)
        } else {
            splitIconView.setImageResource(R.drawable.ic_arrow_upward_black_24dp)
        }
    }

    fun setShuffleEnabled(shuffle: Boolean) {
        this.shuffle = shuffle
        shuffleIconView.isEnabled = shuffle
        shuffleIconView.alpha = if (shuffle) 1.0f else 0.5f
    }

    fun shuffle() {
        setShuffleEnabled(false)

        shuffleCount++
        for (playedCards in characterTracker.playedCardsHistory) {
            if (playedCards.shuffledIndex != null)
                continue

            for (card in playedCards.pile1) {
                if (card.special != CardSpecial.Remove) {
                    characterTracker.deck.add(card)
                }
            }
            if (playedCards.pile2 != null) {
                for (card in playedCards.pile2) {
                    if (card.special != CardSpecial.Remove) {
                        characterTracker.deck.add(card)
                    }
                }
            }
            playedCards.shuffledIndex = shuffleCount
        }
        playedCardsAdapter.updateItems(characterTracker.playedCardsHistory)
        updateActiveDecks()

        Snackbar.make(activity!!.findViewById(R.id.content), "Shuffled", Snackbar.LENGTH_SHORT).show()
    }

    fun hasSpecial(cards: List<Card>, special: CardSpecial): Boolean {
        for (card in cards) {
            if (card.special == special) {
                return true
            }
        }
        return false
    }

    fun drawCards(): ArrayList<Card> {
        val cards = ArrayList<Card>()

        while (true) {
            val card = drawCard() ?: break

            cards.add(card)
            if (card.special != CardSpecial.Rolling)
                break
        }

        return cards
    }

    fun drawCard(): Card? {
        if (characterTracker.deck.size == 0) {
            shuffle()
            if (characterTracker.deck.size == 0) {
                return null
            }
        }

        val index = randomGenerator.nextInt(characterTracker.deck.size)
        return characterTracker.deck.removeAt(index)
    }

    fun updateActiveDecks() {
        updateBlessText()
        updateCurseText()

        if (characterTracker.deck.size == 0) {
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)  //0 means grayscale
            val cf = ColorMatrixColorFilter(matrix)
            drawDeckView.colorFilter = cf
        } else {
            drawDeckView.colorFilter = null
        }
    }
}
