package net.north101.android.ghplayertracker

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import net.north101.android.ghplayertracker.data.Card
import net.north101.android.ghplayertracker.data.Character
import net.north101.android.ghplayertracker.livedata.SummonLiveData
import org.androidannotations.annotations.*
import org.androidannotations.annotations.sharedpreferences.Pref

@OptionsMenu(R.menu.character_tracker)
@EFragment(R.layout.character_tracker_layout)
open class TrackerFragment : Fragment() {
    protected lateinit var actionBar: ActionBar
    @ViewById(R.id.toolbar)
    protected lateinit var toolbar: Toolbar
    @ViewById(R.id.list1)
    protected lateinit var listView1: RecyclerView
    @ViewById(R.id.list2)
    protected lateinit var listView2: RecyclerView

    @OptionsMenuItem(R.id.houseRuleVantage)
    protected lateinit var houseRuleVantageMenu: MenuItem

    protected lateinit var listAdapter1: TrackerAdapter
    protected lateinit var listAdapter2: TrackerAdapter

    @FragmentArg("character")
    protected lateinit var character: Character

    lateinit var trackerResultModel: TrackerResultModel
    lateinit var trackerModel: TrackerModel

    @Pref
    protected lateinit var sharedPrefs: SharedPrefs_

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        trackerResultModel = ViewModelProviders.of(this.targetFragment!!).get(TrackerResultModel::class.java)
        trackerModel = ViewModelProviders.of(this).get(TrackerModel::class.java)
        if (state == null) {
            trackerModel.init(character)
        } else {
            trackerModel.fromBundle(state.getBundle("tracker_model"))
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        state.putBundle("tracker_model", trackerModel.toBundle())

        super.onSaveInstanceState(state)
    }

    override fun onResume() {
        super.onResume()

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        super.onPause()
    }

    @AfterViews
    internal fun afterViews() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        actionBar = (activity as AppCompatActivity).supportActionBar!!

        val iconId = Util.getImageResource(context!!, "icon_" + character.characterClass.id)
        actionBar.setLogo(iconId)
        actionBar.title = character.name
        var c = character.characterClass.color
        val hsv = FloatArray(3)
        Color.colorToHSV(c, hsv)
        hsv[2] = hsv[2] * 2 / 3
        c = Color.HSVToColor(hsv)
        actionBar.setBackgroundDrawable(ColorDrawable(c))

        if (!this::listAdapter1.isInitialized) {
            listAdapter1 = TrackerAdapter()
        }
        listAdapter1.onSummonAddClick = {
            val fragment = TrackerSummonDialog_.builder().build()
            fragment.setTargetFragment(this, 0)
            fragment.show(fragmentManager, "TrackerSummonDialog_")
        }
        listAdapter1.onSummonDeleteClick = {
            trackerModel.tracker.summons.value.remove(it)
            trackerModel.tracker.summons.value = trackerModel.tracker.summons.value
        }
        listAdapter1.onNumberClick = callback@{
            val fragment = when (it) {
                "health" -> TrackerEditHealthDialog_.builder().build()
                "xp" -> TrackerEditXPDialog_.builder().build()
                "loot" -> TrackerEditLootDialog_.builder().build()
                "bless" -> TrackerEditBlessDialog_.builder().build()
                "curse" -> TrackerEditCurseDialog_.builder().build()
                "minus_1" -> TrackerEditMinus1Dialog_.builder().build()
                else -> null
            } ?: return@callback
            fragment.setTargetFragment(this@TrackerFragment, 0)
            fragment.show(fragmentManager, "TrackerNumberEditDialog")
        }

        val listLayoutManager1 = GridLayoutManager(context, 2)
        listLayoutManager1.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = listAdapter1.getItem(position)
                return if (item is CardInfo) {
                    if (item.split) 1 else 2
                } else if (item is SummonLiveData) {
                    return 2
                } else {
                    2
                }
            }
        }
        listView1.layoutManager = listLayoutManager1
        listView1.adapter = listAdapter1

        if (!this::listAdapter2.isInitialized) {
            listAdapter2 = TrackerAdapter()
        }

        val listLayoutManager2 = GridLayoutManager(context, 2)
        listLayoutManager2.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = listAdapter2.getItem(position)
                return if (item is CardInfo) {
                    if (item.split) 1 else 2
                } else if (item is SummonLiveData) {
                    return 2
                } else {
                    2
                }
            }
        }
        listView2.layoutManager = listLayoutManager2
        listView2.adapter = listAdapter2

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            listAdapter1.display = TrackerAdapter.DisplayItems.Both
            listAdapter2.display = TrackerAdapter.DisplayItems.None
        } else {
            listAdapter1.display = TrackerAdapter.DisplayItems.Left
            listAdapter2.display = TrackerAdapter.DisplayItems.Right
        }
        listAdapter1.updateItems(trackerModel.tracker)
        listAdapter2.updateItems(trackerModel.tracker)

        trackerModel.tracker.playedCards.observe(this, Observer {
            if (it == null) return@Observer

            listAdapter1.updateItems(trackerModel.tracker)
            listAdapter2.updateItems(trackerModel.tracker)
        })

        trackerModel.tracker.summons.observe(this, Observer {
            if (it == null) return@Observer

            listAdapter1.updateItems(trackerModel.tracker)
            listAdapter2.updateItems(trackerModel.tracker)
        })

        trackerModel.tracker.houseRule.observe(this, Observer {
            if (it == null) return@Observer

            sharedPrefs.houseRuleVantage().put(it)
            if (this@TrackerFragment::houseRuleVantageMenu.isInitialized) {
                houseRuleVantageMenu.isChecked = it
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        trackerModel.tracker.houseRule.value = trackerModel.tracker.houseRule.value
    }

    @OptionsItem(R.id.complete)
    fun onCompleteClick() {
        val fragment = TrackerCompleteDialog_.builder().build()
        fragment.arguments = Bundle()
        fragment.arguments!!.putInt("gold", trackerModel.tracker.loot.value)
        fragment.arguments!!.putInt("xp", trackerModel.tracker.xp.value)
        fragment.setTargetFragment(this, 0)
        fragment.show(fragmentManager, "TrackerCompleteDialog_")
    }

    @OptionsItem(R.id.houseRuleVantage)
    fun onHomebrewVantageClick() {
        trackerModel.tracker.houseRule.value = !(trackerModel.tracker.houseRule.value)
    }
}


@EBean
abstract class TrackerNumberDialog : EditNumberDialog() {
    lateinit var trackerModel: TrackerModel

    @AfterViews
    override fun afterViews() {
        trackerModel = ViewModelProviders.of(this.targetFragment!!).get(TrackerModel::class.java)

        super.afterViews()
    }
}


@EFragment
open class TrackerEditHealthDialog : TrackerNumberDialog() {
    override val title = "Edit Health"

    override var value: Int
        get() = trackerModel.tracker.health.value
        set(value) {
            trackerModel.tracker.health.value = value
        }
}


@EFragment
open class TrackerEditXPDialog : TrackerNumberDialog() {
    override val title = "Edit XP"

    override var value: Int
        get() = trackerModel.tracker.xp.value
        set(value) {
            trackerModel.tracker.xp.value = value
        }
}


@EFragment
open class TrackerEditLootDialog : TrackerNumberDialog() {
    override val title = "Edit Coins"

    override var value: Int
        get() = trackerModel.tracker.loot.value
        set(value) {
            trackerModel.tracker.loot.value = value
        }
}


@EBean
abstract class TrackerEditCardDialog : TrackerNumberDialog() {
    abstract val card: Card

    override var value: Int
        get() {
            val drawCount = trackerModel.tracker.drawDeck.value.count { it == card }
            val discardCount = trackerModel.tracker.discardDeck.value.count { it == card }
            return (drawCount + discardCount)
        }
        set(value) {
            val current = this.value
            if (current < value) {
                for (index in current until value) {
                    trackerModel.tracker.drawDeck.value.add(card)
                }
            } else if (current > value) {
                for (index in value until current) {
                    if (trackerModel.tracker.drawDeck.value.contains(card)) {
                        trackerModel.tracker.drawDeck.value.remove(card)
                    } else {
                        trackerModel.tracker.discardDeck.value.remove(card)
                    }
                }
            }
            trackerModel.tracker.drawDeck.value = trackerModel.tracker.drawDeck.value
        }
}


@EFragment
open class TrackerEditBlessDialog : TrackerEditCardDialog() {
    override val title = "Edit Bless"

    override val card: Card
        get() = Card["mod_extra_double_bless_remove"]
}


@EFragment
open class TrackerEditCurseDialog : TrackerEditCardDialog() {
    override val title = "Edit Curse"

    override val card: Card
        get() = Card["mod_extra_null_curse_remove"]
}


@EFragment
open class TrackerEditMinus1Dialog : TrackerEditCardDialog() {
    override val title = "Edit -1"

    override val card: Card
        get() = Card["mod_extra_minus_1"]
}