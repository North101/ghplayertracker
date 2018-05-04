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
import android.widget.EditText
import net.north101.android.ghplayertracker.livedata.SummonLiveData
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.ViewById
import java.util.*
import kotlin.collections.HashMap

@EFragment
open class TrackerSummonDialog : DialogFragment() {
    private lateinit var view2: View

    @ViewById(R.id.name)
    protected lateinit var nameView: EditText
    @ViewById(R.id.max_health)
    protected lateinit var maxHealthView: EditText
    @ViewById(R.id.move)
    protected lateinit var moveView: EditText
    @ViewById(R.id.attack)
    protected lateinit var attackView: EditText
    @ViewById(R.id.range)
    protected lateinit var rangeView: EditText

    lateinit var trackerModel: TrackerModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        val inflater = activity!!.layoutInflater
        view2 = inflater.inflate(R.layout.character_tracker_summon_layout, null as ViewGroup?)

        builder.setView(view2)
            .setTitle("Add SummonLiveData")
            .setPositiveButton("OK", null)
            .setNegativeButton("CANCEL") { dialog, id ->
                this@TrackerSummonDialog.dialog.cancel()
            }

        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setOnShowListener { dialog ->
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                maxHealthView.error = null
                moveView.error = null
                attackView.error = null
                rangeView.error = null

                val health = try {
                    Integer.parseInt(maxHealthView.text.toString())
                } catch (e: Exception) {
                    maxHealthView.error = "Invalid Number"
                    return@setOnClickListener
                }
                val move = try {
                    Integer.parseInt(moveView.text.toString())
                } catch (e: Exception) {
                    moveView.error = "Invalid Number"
                    return@setOnClickListener
                }
                val attack = try {
                    Integer.parseInt(attackView.text.toString())
                } catch (e: Exception) {
                    attackView.error = "Invalid Number"
                    return@setOnClickListener
                }
                val range = try {
                    Integer.parseInt(rangeView.text.toString())
                } catch (e: Exception) {
                    rangeView.error = "Invalid Number"
                    return@setOnClickListener
                }

                trackerModel.tracker.summons.value.add(SummonLiveData(
                    UUID.randomUUID(),
                    nameView.text.toString(),
                    health,
                    health,
                    move,
                    attack,
                    range,
                    HashMap()
                ))
                trackerModel.tracker.summons.value = trackerModel.tracker.summons.value

                dialog.dismiss()
            }
        }
        return view2
    }

    @AfterViews
    fun afterViews() {
        trackerModel = ViewModelProviders.of(this.targetFragment!!).get(TrackerModel::class.java)
    }
}