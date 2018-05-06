package net.north101.android.ghplayertracker

import android.app.Dialog
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.TextView
import net.north101.android.ghplayertracker.data.Ability
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentArg
import org.androidannotations.annotations.ViewById

@EFragment
open class CharacterAbilityDialog : DialogFragment() {
    private lateinit var view2: View

    @JvmField
    @FragmentArg
    protected var index: Int = 0

    @ViewById(R.id.spinner)
    protected lateinit var spinnerView: Spinner

    protected lateinit var abilityAdapter: AbilityAdapter

    lateinit var characterModel: CharacterModel

    var abilities: ArrayList<MutableLiveData<Ability>>
        get() = characterModel.character.abilities.value
        set(value) {
            characterModel.character.abilities.value = value
        }

    override fun onCreateDialog(state: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        val inflater = activity!!.layoutInflater
        view2 = inflater.inflate(R.layout.character_ability_layout, null as ViewGroup?)

        builder.setView(view2)
            .setTitle("Choose Ability")
            .setPositiveButton("OK") { _, _ ->
                abilities[index].value = spinnerView.selectedItem as Ability?
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.cancel()
            }

        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        return view2
    }

    @AfterViews
    fun afterViews() {
        characterModel = ViewModelProviders.of(this.targetFragment!!).get(CharacterModel::class.java)

        abilityAdapter = AbilityAdapter(characterModel, index)
        spinnerView.adapter = abilityAdapter
        spinnerView.setSelection(abilityAdapter.classAbilities.indexOf(abilityAdapter.currentAbility) + 1)
    }
}


class AbilityAdapter(val characterModel: CharacterModel, val index: Int) : BaseAdapter() {
    val classAbilities = characterModel.character.characterClass.abilities.values
        .filter { it.level > 1 && it.level <= index + 2 }
        .sortedWith(compareBy({ -it.level }, { it.id }))
    val characterAbilities = characterModel.character.abilities.value
        .map { it.value }
    val currentAbility = characterAbilities[index]

    override fun getItemId(p0: Int): Long {
        return getItem(p0)?.id?.hashCode()?.toLong() ?: 0
    }

    override fun getCount(): Int {
        return classAbilities.size + 1
    }

    override fun getItem(i: Int): Ability? {
        return if (i == 0) {
            null
        } else {
            classAbilities[i - 1]
        }
    }

    override fun isEnabled(position: Int): Boolean {
        val item = getItem(position)
        return item == null || item == currentAbility || !characterAbilities.contains(item)
    }

    override fun getDropDownView(position: Int, view: View?, parent: ViewGroup): View {
        var convertedView = view
        if (convertedView == null) {
            convertedView = LayoutInflater
                .from(parent.context)
                .inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
        }
        convertedView!!.findViewById<TextView>(android.R.id.text1).text = getItem(position)?.let {
            it.level.toString() + ": " + it.name
        } ?: "None"
        convertedView.isEnabled = isEnabled(position)
        return convertedView
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertedView = view as TextView?
        if (convertedView == null) {
            convertedView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.spinner_text_view, parent, false) as TextView
        }
        convertedView.text = getItem(position)?.let {
            it.level.toString() + ": " + it.name
        } ?: "None"
        return convertedView
    }
}