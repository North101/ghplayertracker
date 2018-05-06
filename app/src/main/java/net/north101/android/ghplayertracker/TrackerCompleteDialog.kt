package net.north101.android.ghplayertracker

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.TextView
import org.androidannotations.annotations.*

@EFragment
open class TrackerCompleteDialog : DialogFragment() {
    private lateinit var view2: View

    @ViewById(R.id.scenario_level)
    protected lateinit var scenarioLevelView: Spinner
    @ViewById(R.id.scenario_multiplier)
    protected lateinit var scenarioMultiplierText: TextView
    @ViewById(R.id.gained_loot)
    protected lateinit var gainedLootText: TextView
    @ViewById(R.id.bonus_gold)
    protected lateinit var bonusGoldText: TextView
    @ViewById(R.id.total_gold)
    protected lateinit var totalGoldText: TextView
    @ViewById(R.id.scenario_xp)
    protected lateinit var scenarioXPText: TextView
    @ViewById(R.id.gained_xp)
    protected lateinit var gainedXPText: TextView
    @ViewById(R.id.bonus_xp)
    protected lateinit var bonusXPText: TextView
    @ViewById(R.id.total_xp)
    protected lateinit var totalXPText: TextView

    private lateinit var scenarioAdapter: ScenarioLevelAdapter

    @JvmField
    @InstanceState
    @FragmentArg("gold")
    protected var gold: Int = 0
    @JvmField
    @InstanceState
    @FragmentArg("xp")
    protected var xp: Int = 0
    @JvmField
    @InstanceState
    protected var scenarioLevel = 0

    lateinit var trackerResultModel: TrackerResultModel

    override fun onCreateDialog(state: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        val inflater = activity!!.layoutInflater
        view2 = inflater.inflate(R.layout.character_tracker_complete_layout, null as ViewGroup?)

        builder.setView(view2)
            .setTitle("Finish Scenario")
            .setPositiveButton("OK") { dialog, id ->
                trackerResultModel.gold = try {
                    parseGoldTotal()
                } catch (e: Exception) {
                    0
                }

                trackerResultModel.xp = try {
                    parseXPTotal()
                } catch (e: Exception) {
                    0
                }

                fragmentManager!!.popBackStack()
            }
            .setNegativeButton("CANCEL") { dialog, id ->
                this@TrackerCompleteDialog.dialog.cancel()
            }

        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return view2
    }

    @AfterViews
    fun afterViews() {
        trackerResultModel = ViewModelProviders.of(this.targetFragment!!.targetFragment!!).get(TrackerResultModel::class.java)

        gainedLootText.text = gold.toString()
        gainedXPText.text = xp.toString()

        scenarioAdapter = ScenarioLevelAdapter(scenarioLevels)
        scenarioLevelView.adapter = scenarioAdapter
        scenarioLevelView.setSelection(scenarioLevel)
    }

    @TextChange(R.id.scenario_multiplier, R.id.gained_loot, R.id.bonus_gold)
    fun onGoldChanged() {
        totalGoldText.text = try {
            parseGoldTotal().toString()
        } catch (e: Exception) {
            "Error"
        }
    }

    fun parseGoldTotal(): Int {
        val value1 = Integer.parseInt(scenarioMultiplierText.text.toString())
        val value2 = Integer.parseInt(gainedLootText.text.toString())
        val value3 = Integer.parseInt(bonusGoldText.text.toString())
        return value1 * value2 + value3
    }

    @TextChange(R.id.scenario_xp, R.id.gained_xp, R.id.bonus_xp)
    fun onXPChange() {
        totalXPText.text = try {
            parseXPTotal().toString()
        } catch (e: Exception) {
            "Error"
        }
    }

    fun parseXPTotal(): Int {
        val value1 = Integer.parseInt(scenarioXPText.text.toString())
        val value2 = Integer.parseInt(gainedXPText.text.toString())
        val value3 = Integer.parseInt(bonusXPText.text.toString())
        return value1 + value2 + value3
    }

    @ItemSelect(R.id.scenario_level)
    fun onScenarioLevelSelect(selected: Boolean, selectedItem: ScenarioLevel) {
        scenarioMultiplierText.text = selectedItem.gold.toString()
        scenarioXPText.text = selectedItem.xp.toString()
    }

    data class ScenarioLevel(val level: Int, val gold: Int, val xp: Int)

    val scenarioLevels = listOf(
        ScenarioLevel(-1, 0, 0),
        ScenarioLevel(0, 2, 4),
        ScenarioLevel(1, 2, 6),
        ScenarioLevel(2, 3, 8),
        ScenarioLevel(3, 3, 10),
        ScenarioLevel(4, 4, 12),
        ScenarioLevel(5, 4, 14),
        ScenarioLevel(6, 5, 16),
        ScenarioLevel(7, 5, 18)
    )
}

class ScenarioLevelAdapter(val items: List<TrackerCompleteDialog.ScenarioLevel>) : BaseAdapter() {
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return this.items.count()
    }

    override fun getItem(i: Int): TrackerCompleteDialog.ScenarioLevel {
        return items[i]
    }

    override fun getDropDownView(position: Int, view: View?, parent: ViewGroup): View {
        var convertedView = view
        if (convertedView == null) {
            convertedView = LayoutInflater
                .from(parent.context)
                .inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
        }
        val textView = convertedView!!.findViewById<TextView>(android.R.id.text1)
        textView.text = getItem(position).level.let {
            if (it == -1) {
                "Failed"
            } else {
                it.toString()
            }
        }
        textView.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
        return convertedView
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertedView = view as TextView?
        if (convertedView == null) {
            convertedView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.spinner_text_view, parent, false) as TextView
        }
        convertedView.text = getItem(position).level.let {
            if (it == -1) {
                "Failed"
            } else {
                it.toString()
            }
        }
        convertedView.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
        return convertedView
    }
}