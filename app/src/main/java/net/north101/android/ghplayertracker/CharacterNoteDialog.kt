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
import android.widget.TextView
import net.north101.android.ghplayertracker.livedata.InitLiveData
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentArg
import org.androidannotations.annotations.ViewById

@EFragment
open class CharacterNoteDialog : DialogFragment() {
    private lateinit var view2: View

    @JvmField
    @FragmentArg
    protected var index: Int? = null

    @ViewById(R.id.text)
    protected lateinit var textView: TextView

    lateinit var characterModel: CharacterModel

    var notes: ArrayList<InitLiveData<String>>
        get() = characterModel.character.notes.value
        set(value) {
            characterModel.character.notes.value = value
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        val inflater = activity!!.layoutInflater
        view2 = inflater.inflate(R.layout.character_note_layout, null as ViewGroup?)

        builder.setView(view2)
            .setTitle("Edit Note")
            .setPositiveButton("OK") { dialog, id ->
                if (index == null) {
                    notes.add(InitLiveData(textView.text.toString()))
                } else {
                    notes[index!!].value = textView.text.toString()
                }
                notes = notes
            }
            .setNegativeButton("CANCEL") { dialog, id ->
                this@CharacterNoteDialog.dialog.cancel()
            }

        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return view2
    }

    @AfterViews
    fun afterViews() {
        characterModel = ViewModelProviders.of(this.targetFragment!!).get(CharacterModel::class.java)

        if (index == null) {
            textView.text = ""
        } else {
            textView.text = notes[index!!].value
        }
    }
}