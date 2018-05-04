package net.north101.android.ghplayertracker

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import net.north101.android.ghplayertracker.data.Character
import net.north101.android.ghplayertracker.data.CharacterData
import org.androidannotations.annotations.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException

@OptionsMenu(R.menu.character)
@EFragment(R.layout.character_tracker_layout)
open class CharacterFragment : Fragment(), OnBackPressedListener {
    protected lateinit var actionBar: ActionBar
    @ViewById(R.id.toolbar)
    protected lateinit var toolbar: Toolbar
    @ViewById(R.id.list1)
    protected lateinit var listView1: RecyclerView
    @ViewById(R.id.list2)
    protected lateinit var listView2: RecyclerView

    protected lateinit var listAdapter1: CharacterAdapter
    protected lateinit var listAdapter2: CharacterAdapter

    @FragmentArg("character")
    @InstanceState
    protected lateinit var character: Character

    lateinit var classModel: ClassModel
    lateinit var characterModel: CharacterModel
    lateinit var trackerResultModel: TrackerResultModel

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        classModel = ViewModelProviders.of(this.activity!!).get(ClassModel::class.java)
        characterModel = ViewModelProviders.of(this).get(CharacterModel::class.java)
        trackerResultModel = ViewModelProviders.of(this).get(TrackerResultModel::class.java)

        if (state == null) {
            characterModel.init(character)
        } else {
            characterModel.fromBundle(state.getBundle("character_model"))
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        state.putBundle("character_model", characterModel.toBundle())

        super.onSaveInstanceState(state)
    }

    @AfterViews
    fun afterViews() {
        if (trackerResultModel.gold > 0) {
            characterModel.character.gold.value += trackerResultModel.gold
            trackerResultModel.gold = 0
        }
        if (trackerResultModel.xp > 0) {
            characterModel.character.xp.value += trackerResultModel.xp
            trackerResultModel.xp = 0
        }

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        actionBar = (activity as AppCompatActivity).supportActionBar!!

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

        if (!this::listAdapter1.isInitialized) {
            listAdapter1 = CharacterAdapter()
        }
        listAdapter1.onNumberEditClick = callback@{
            val fragment = when (it) {
                "level" -> CharacterEditLevelDialog_.builder().build()
                "xp" -> CharacterEditXPDialog_.builder().build()
                "gold" -> CharacterEditGoldDialog_.builder().build()
                else -> null
            } ?: return@callback
            fragment.setTargetFragment(this@CharacterFragment, 0)
            fragment.show(fragmentManager, "CharacterNumberEdit")
        }
        listAdapter1.onItemAddClick = {
            val fragment = CharacterItemDialog_.builder().build()
            fragment.setTargetFragment(this, 0)

            fragment.show(fragmentManager, "CharacterItemDialog_")
        }
        listAdapter1.onItemEditClick = {
            val args = Bundle()
            args.putInt("index", characterModel.character.items.value.indexOf(it))

            val fragment = CharacterItemDialog_.builder().build()
            fragment.arguments = args
            fragment.setTargetFragment(this, 0)

            fragment.show(fragmentManager, "CharacterNoteDialog")
        }
        listAdapter1.onItemDeleteClick = {
            characterModel.character.items.value.remove(it)
            characterModel.character.items.value = characterModel.character.items.value

        }
        listAdapter1.onNoteAddClick = {
            val fragment = CharacterNoteDialog_.builder().build()
            fragment.setTargetFragment(this, 0)

            fragment.show(fragmentManager, "CharacterNoteDialog")
        }
        listAdapter1.onNoteEditClick = {
            val args = Bundle()
            args.putInt("index", characterModel.character.notes.value.indexOf(it.note))

            val fragment = CharacterNoteDialog_.builder().build()
            fragment.arguments = args
            fragment.setTargetFragment(this, 0)

            fragment.show(fragmentManager, "CharacterNoteDialog")
        }
        listAdapter1.onNoteDeleteClick = {
            characterModel.character.notes.value.remove(it.note)
            characterModel.character.notes.value = characterModel.character.notes.value
        }

        val listLayoutManager1 = GridLayoutManager(context, 3)
        listLayoutManager1.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = listAdapter1.getItem(position)
                return if (item is CharacterAdapter.PerkNote) {
                    1
                } else {
                    3
                }
            }
        }
        listView1.layoutManager = listLayoutManager1
        listView1.adapter = listAdapter1

        if (!this::listAdapter2.isInitialized) {
            listAdapter2 = CharacterAdapter()
        }

        val listLayoutManager2 = GridLayoutManager(context, 3)
        listLayoutManager2.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = listAdapter2.getItem(position)
                return if (item is CharacterAdapter.PerkNote) {
                    1
                } else {
                    3
                }
            }
        }
        listView2.layoutManager = listLayoutManager2
        listView2.adapter = listAdapter2

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            listAdapter1.display = CharacterAdapter.DisplayItems.Both
            listAdapter2.display = CharacterAdapter.DisplayItems.None
        } else {
            listAdapter1.display = CharacterAdapter.DisplayItems.Left
            listAdapter2.display = CharacterAdapter.DisplayItems.Right
        }
        listAdapter1.updateItems(characterModel.character)
        listAdapter2.updateItems(characterModel.character)

        characterModel.character.items.observe(this, Observer {
            listAdapter1.updateItems(characterModel.character)
            listAdapter2.updateItems(characterModel.character)
        })
        characterModel.character.notes.observe(this, Observer {
            listAdapter1.updateItems(characterModel.character)
            listAdapter2.updateItems(characterModel.character)
        })
    }

    override fun onBackPressed(): Boolean {
        if (characterModel.character.toParcel() == character)
            return false

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Would you like to save your changes?")
            .setPositiveButton("Yes") { dialog, which ->
                saveCharacter(characterModel.character.toParcel(), Runnable {
                    fragmentManager!!.popBackStack()
                })
            }
            .setNegativeButton("No") { dialog, which ->
                fragmentManager!!.popBackStack()
            }
            .setNeutralButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()

        return true
    }

    @OptionsItem(R.id.start)
    fun onMenuStartClick() {
        val args = Bundle()
        args.putParcelable("character", characterModel.character.toParcel())

        val fragment = TrackerFragment_()
        fragment.arguments = args
        fragment.setTargetFragment(this, 1)

        fragmentManager!!.beginTransaction()
            .replace(R.id.content, fragment)
            .addToBackStack(null)
            .commit()
    }

    @OptionsItem(R.id.save)
    fun onMenuSaveClick() {
        character = characterModel.character.toParcel()
        saveCharacter(character, null)
    }

    fun saveCharacter(character: Character, callback: Runnable?) {
        saveCharacterTask(character, callback)
    }

    open fun saveCharacterTask(character: Character, callback: Runnable?) {
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

            classModel.characterList.load()
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

        if (callback != null) {
            activity!!.runOnUiThread(callback)
        }
    }
}


@EBean
abstract class CharacterNumberDialog : EditNumberDialog() {
    lateinit var characterModel: CharacterModel

    @AfterViews
    override fun afterViews() {
        characterModel = ViewModelProviders.of(this.targetFragment!!).get(CharacterModel::class.java)

        super.afterViews()
    }
}


@EFragment
open class CharacterEditLevelDialog : CharacterNumberDialog() {
    override val title = "Edit Level"

    override var value: Int
        get() = characterModel.character.level.value
        set(value) {
            characterModel.character.level.value = value
        }
}


@EFragment
open class CharacterEditXPDialog : CharacterNumberDialog() {
    override val title = "Edit XP"

    override var value: Int
        get() = characterModel.character.xp.value
        set(value) {
            characterModel.character.xp.value = value
        }
}


@EFragment
open class CharacterEditGoldDialog : CharacterNumberDialog() {
    override val title = "Edit Gold"

    override var value: Int
        get() = characterModel.character.gold.value
        set(value) {
            characterModel.character.gold.value = value
        }
}
