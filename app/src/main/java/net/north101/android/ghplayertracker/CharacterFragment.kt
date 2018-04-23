package net.north101.android.ghplayertracker

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import net.north101.android.ghplayertracker.data.Character
import net.north101.android.ghplayertracker.data.CharacterData
import net.north101.android.ghplayertracker.data.CharacterPerk
import net.north101.android.ghplayertracker.data.Level
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import org.androidannotations.annotations.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.util.*

@OptionsMenu(R.menu.character)
@EFragment(R.layout.character_layout)
open class CharacterFragment : Fragment(), OnBackPressedListener {
    protected lateinit var actionBar: ActionBar
    @ViewById(R.id.toolbar)
    protected lateinit var toolbar: Toolbar
    @ViewById(R.id.max_health)
    protected lateinit var maxHealthView: TextView
    @ViewById(R.id.xp_text)
    protected lateinit var xpTextView: EditText
    @ViewById(R.id.levels_select)
    protected lateinit var levelsView: Spinner
    @ViewById(R.id.gold_text)
    protected lateinit var goldTextView: EditText
    @ViewById(R.id.perk_list)
    protected lateinit var perkListView: RecyclerView
    @ViewById(R.id.perk_note_grid)
    protected lateinit var perkNoteGridView: RecyclerView
    @ViewById(R.id.name)
    protected lateinit var nameView: TextView
    @ViewById(R.id.minus_1_text)
    protected lateinit var minus1TextView: TextView
    @ViewById(R.id.retired)
    protected lateinit var retiredView: CheckBox

    @FragmentArg("character")
    @InstanceState
    protected lateinit var character: Character
    @JvmField
    @InstanceState
    protected var savedCharacter: Character? = null

    protected lateinit var levelAdapter: LevelAdapter
    protected lateinit var perkAdapter: PerkAdapter
    protected lateinit var perkNoteAdapter: PerkNoteAdapter

    var keyboardEventListener: Unregistrar? = null

    lateinit var classViewModel: ClassViewModel

    @AfterViews
    fun afterViews() {
        classViewModel = ViewModelProviders.of(this.activity!!).get(ClassViewModel::class.java)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        actionBar = (activity as AppCompatActivity).supportActionBar!!

        if (savedCharacter == null) {
            try {
                savedCharacter = character.copy()
            } catch (e: CloneNotSupportedException) {
                e.printStackTrace()
            }

        }

        val characterClass = character.characterClass

        val iconId = Util.getImageResource(context!!, "icon_" + characterClass.id)
        actionBar.setLogo(iconId)
        actionBar.title = characterClass.name
        var c = characterClass.color
        val hsv = FloatArray(3)
        Color.colorToHSV(c, hsv)
        hsv[2] = hsv[2] * 2 / 3
        c = Color.HSVToColor(hsv)
        actionBar.setBackgroundDrawable(ColorDrawable(c))

        val characterPerkList = characterClass.perks.mapIndexed { index, i ->
            CharacterPerk(i, character.perks.getOrElse(index, { 0 }))
        }

        val perkListLayoutManager = LinearLayoutManager(context)
        perkListView.layoutManager = perkListLayoutManager
        perkListView.itemAnimator = DefaultItemAnimator()
        perkAdapter = PerkAdapter(characterPerkList)
        perkListView.adapter = perkAdapter
        ViewCompat.setNestedScrollingEnabled(perkListView, false)

        val perkNoteListLayoutManager = GridLayoutManager(context, 3)
        perkNoteGridView.layoutManager = perkNoteListLayoutManager
        perkNoteGridView.itemAnimator = DefaultItemAnimator()
        perkNoteAdapter = PerkNoteAdapter(character.perkNotes)
        perkNoteGridView.adapter = perkNoteAdapter
        ViewCompat.setNestedScrollingEnabled(perkListView, false)

        levelAdapter = LevelAdapter(characterClass.levels)
        levelsView.adapter = levelAdapter
    }

    override fun onBackPressed(): Boolean {
        updateCharacter()
        if (character == savedCharacter)
            return false

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Would you like to save your changes?")
                .setPositiveButton("Yes") { dialog, which -> saveCharacter(Runnable { fragmentManager!!.popBackStack() }) }
                .setNegativeButton("No") { dialog, which -> fragmentManager!!.popBackStack() }
                .setNeutralButton("Cancel") { dialog, which -> dialog.dismiss() }
                .show()

        return true
    }

    override fun onResume() {
        super.onResume()

        nameView.text = character.name
        updateXPText()
        updateLevelText()
        updateMinus1Text()
        updateGoldText()
        updateHealthText()
        retiredView.isChecked = character.retired

        //HACK!
        keyboardEventListener = KeyboardVisibilityEvent.registerEventListener(activity, KeyboardVisibilityEventListener { isOpen ->
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

        view!!.post {
            if (classViewModel.characterList.value == null) {
                classViewModel.characterList.load()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (keyboardEventListener != null) {
            keyboardEventListener!!.unregister()
            keyboardEventListener = null
        }

        updateCharacter()

        classViewModel.characterList.removeObservers(this)
        classViewModel.classList.removeObservers(this)
    }

    @OptionsItem(R.id.start)
    fun onMenuStartClick() {
        updateCharacter()

        val args = Bundle()
        args.putParcelable("character", character)

        val fragment = CharacterTrackerFragment_()
        fragment.arguments = args
        fragment.setTargetFragment(this, 1)

        activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit()
    }

    @ItemSelect(R.id.levels_select)
    fun onLevelSelect(selected: Boolean, selectedItem: Level) {
        character.level = selectedItem.level
        maxHealthView.text = selectedItem.health.toString()
    }

    fun updateHealthText() {
        maxHealthView.text = character.maxHealth.toString()
    }

    fun updateCharacter() {
        character.name = nameView.text.toString()
        parseXPText()
        parseGoldText()
        parseMinus1Text()

        for (i in perkAdapter.items.indices) {
            val characterPerk = perkAdapter.items[i]
            character.perks[i] = characterPerk.ticks
        }
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
            character.xp = Integer.parseInt(xpTextView.text.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Invalid XP value", Toast.LENGTH_SHORT).show()
        }

        updateXPText()
    }

    fun updateXPText() {
        val text = character.xp.toString()
        if (text != xpTextView.text.toString()) {
            xpTextView.setText(text)
        }
    }

    fun updateLevelText() {
        levelsView.setSelection(character.level - 1)
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
            character.gold = Integer.parseInt(goldTextView.text.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Invalid Gold value", Toast.LENGTH_SHORT).show()
        }

        updateGoldText()
    }

    fun updateGoldText() {
        val text = character.gold.toString()
        if (text != goldTextView.text.toString()) {
            goldTextView.setText(text)
        }
    }

    @EditorAction(R.id.minus_1_text)
    fun onMinus1TextChange(tv: TextView, actionId: Int): Boolean {
        if (actionId != EditorInfo.IME_ACTION_DONE) return false

        parseMinus1Text()
        return false
    }

    @FocusChange(R.id.minus_1_text)
    fun onMinus1TextFocus() {
        if (!this::minus1TextView.isInitialized || minus1TextView.hasFocus()) return

        parseMinus1Text()
    }

    fun parseMinus1Text() {
        try {
            character.minus1 = Integer.parseInt(minus1TextView.text.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Invalid -1 Attack modifier value", Toast.LENGTH_SHORT).show()
        }

        updateMinus1Text()
    }

    fun updateMinus1Text() {
        val text = character.minus1.toString()
        if (text != minus1TextView.text.toString()) {
            minus1TextView.text = text
        }
    }

    @Click(R.id.retired)
    fun onRetiredClicked() {
        character.retired = retiredView.isChecked
    }

    @OptionsItem(R.id.save)
    fun onMenuSaveClick() {
        updateCharacter()
        saveCharacter(null)
    }

    fun saveCharacter(callback: Runnable?) {
        saveCharacterTask(callback)
    }

    open fun saveCharacterTask(callback: Runnable?) {
        val data = try {
            CharacterData.load(context!!)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        } ?: CharacterData(context!!, JSONObject())

        data.update(character)

        try {
            data.save()

            classViewModel.characterList.load()
        } catch (e: IOException) {
            e.printStackTrace()

            Snackbar.make(activity!!.findViewById(R.id.content), "Failed to save", Snackbar.LENGTH_SHORT).show()
            return
        } catch (e: JSONException) {
            e.printStackTrace()
            Snackbar.make(activity!!.findViewById(R.id.content), "Failed to save", Snackbar.LENGTH_SHORT).show()
            return
        }

        Snackbar.make(activity!!.findViewById(R.id.content), "Saved", Snackbar.LENGTH_SHORT).show()

        savedCharacter = character

        if (callback != null) {
            activity!!.runOnUiThread(callback)
        }
    }

    inner class LevelAdapter(val items: ArrayList<Level>) : BaseAdapter() {
        override fun getCount(): Int {
            return this.items.size
        }

        override fun getItem(i: Int): Level {
            return items[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getDropDownView(position: Int, view: View?, parent: ViewGroup): View {
            var convertedView = view
            if (convertedView == null) {
                convertedView = LayoutInflater
                        .from(parent.context)
                        .inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
            }
            (convertedView!!.findViewById<View>(android.R.id.text1) as TextView).text = getItem(position).level.toString()
            return convertedView
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup): View {
            var convertedView = view
            if (convertedView == null) {
                convertedView = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.spinner_text_view, parent, false)
            }
            (convertedView as TextView).text = getItem(position).level.toString()
            return convertedView
        }
    }
}
