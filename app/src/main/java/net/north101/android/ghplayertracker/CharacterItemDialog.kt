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
import net.north101.android.ghplayertracker.data.ItemType
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentArg
import org.androidannotations.annotations.ViewById

@EFragment
open class CharacterItemDialog : DialogFragment() {
    private lateinit var view2: View

    @JvmField
    @FragmentArg
    protected var index: Int? = null

    @ViewById(R.id.text)
    protected lateinit var textView: TextView
    @ViewById(R.id.type)
    protected lateinit var typeView: Spinner

    lateinit var characterModel: CharacterModel

    var items: ArrayList<ItemData>
        get() = characterModel.items.value!!
        set(value) {
            characterModel.items.value = value
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        val inflater = activity!!.layoutInflater
        view2 = inflater.inflate(R.layout.character_item_layout, null as ViewGroup?)

        builder.setView(view2)
                .setTitle("Edit Item")
                .setPositiveButton("OK") { dialog, id ->
                    val name = textView.text.toString()
                    val type = typeView.selectedItem as ItemType
                    if (index == null) {
                        items.add(ItemData(name, type))
                    } else {
                        items[index!!].name.value = name
                        items[index!!].type.value = type
                    }
                    items = items
                }
                .setNegativeButton("CANCEL") { dialog, id ->
                    this@CharacterItemDialog.dialog.cancel()
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

        val typeAdapter = ItemTypeAdapter()
        typeView.adapter = typeAdapter

        if (index == null) {
            textView.text = ""
        } else {
            textView.text = items[index!!].name.value
            typeView.setSelection(items[index!!].type.value!!.ordinal)
        }
    }
}

class ItemTypeAdapter : BaseAdapter() {
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return ItemType.values().size
    }

    override fun getItem(i: Int): ItemType {
        return ItemType.values()[i]
    }

    override fun getDropDownView(position: Int, view: View?, parent: ViewGroup): View {
        var convertedView = view
        if (convertedView == null) {
            convertedView = LayoutInflater
                    .from(parent.context)
                    .inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
        }
        (convertedView!!.findViewById<View>(android.R.id.text1) as TextView).text = getItem(position).name
        return convertedView
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertedView = view
        if (convertedView == null) {
            convertedView = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.spinner_text_view, parent, false)
        }
        (convertedView as TextView).text = getItem(position).name
        return convertedView
    }
}