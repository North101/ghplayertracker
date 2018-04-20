package net.north101.android.ghplayertracker

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import net.north101.android.ghplayertracker.data.Character
import net.north101.android.ghplayertracker.data.SelectableCharacter
import org.androidannotations.annotations.*
import org.json.JSONException
import java.io.IOException
import java.text.ParseException

@EFragment(R.layout.character_list_layout)
open class CharacterListFragment : Fragment(), ActionMode.Callback {
    @ViewById(R.id.toolbar)
    protected lateinit var toolbar: Toolbar
    @ViewById(R.id.fab)
    protected lateinit var fab: FloatingActionButton
    @ViewById(R.id.character_list)
    protected lateinit var listView: RecyclerView
    @ViewById(R.id.loading)
    protected lateinit var loadingView: View

    protected lateinit var listAdapter: CharacterListAdapter

    lateinit var classViewModel: ClassViewModel

    @JvmField
    @InstanceState
    protected var selectedCharacters = ArrayList<SelectableCharacter>()

    var actionMode: ActionMode? = null

    var onClickListener: BaseViewHolder.ClickListener<SelectableCharacter> = object : BaseViewHolder.ClickListener<SelectableCharacter>() {
        override fun onItemClick(holder: BaseViewHolder<SelectableCharacter>) {
            if (actionMode != null) {
                holder.item!!.selected = !holder.item!!.selected
                listAdapter.notifyItemChanged(holder.adapterPosition)
                return
            }

            val fragment = CharacterFragment_()
            val args = Bundle()
            args.putParcelable("character", holder.item!!.character)
            fragment.arguments = args

            activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.content, fragment)
                    .addToBackStack(null)
                    .commit()
        }

        override fun onItemLongClick(holder: BaseViewHolder<SelectableCharacter>): Boolean {
            if (actionMode != null) {
                actionMode!!.finish()
                return true
            }

            actionMode = (activity as AppCompatActivity).startSupportActionMode(this@CharacterListFragment)
            holder.item!!.selected = true
            listAdapter.notifyItemChanged(holder.adapterPosition)

            return true
        }
    }

    @AfterViews
    fun afterViews() {
        classViewModel = ViewModelProviders.of(this.activity!!).get(ClassViewModel::class.java)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        fab.setOnClickListener {
            val fragment = ClassListFragment_()
            activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.content, fragment)
                    .addToBackStack(null)
                    .commit()
        }

        val listViewLayoutManager = LinearLayoutManager(context)
        listView.layoutManager = listViewLayoutManager

        val animator = SlideDownAlphaAnimator()
        listView.itemAnimator = animator

        listAdapter = CharacterListAdapter()
        listAdapter.updateItems(selectedCharacters)
        listAdapter.setOnClickListener(onClickListener)
        listView.adapter = listAdapter

        listAdapter.updateItems(selectedCharacters)
        view!!.post {
            if (classViewModel.classList.value == null) {
                classViewModel.classList.load()
            }
            classViewModel.classList.observe(this, Observer {
                if (it != null && classViewModel.characterList.value == null) {
                    classViewModel.characterList.load(it)
                }
            })
            classViewModel.characterList.observe(this, Observer {
                if (it != null) {
                    setCharacterList(it)
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()
        if (actionMode != null) {
            actionMode!!.finish()
        }

        selectedCharacters = ArrayList(selectedCharacters.map { it.copy() })
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    open fun setCharacterList(characterList: List<Character>) {
        if (this.isRemoving) {
            return
        }

        loadingView.visibility = View.GONE
        listView.visibility = View.VISIBLE

        selectedCharacters = ArrayList(characterList.map {
            SelectableCharacter(it, selectedCharacters.find { selected ->
                selected.character == it
            }?.selected ?: false)
        })
        listAdapter.updateItems(selectedCharacters)
    }

    @Background
    open fun deleteSelectedCharacters() {
        val data = try {
            Character.CharacterData.load(context!!)
        } catch (e: IOException) {
            e.printStackTrace()
            return
        } catch (e: JSONException) {
            e.printStackTrace()
            return
        } catch (e: ParseException) {
            e.printStackTrace()
            return
        }

        val charactersDeleted = selectedCharacters
                .filter { it.selected }
                .count {
                    data.delete(it.character)
                    true
                }
        if (charactersDeleted > 0) {
            try {
                data.save()
            } catch (e: IOException) {
                e.printStackTrace()
                Snackbar.make(activity!!.findViewById(R.id.content), "Failed to delete character(s)", Snackbar.LENGTH_SHORT).show()
                return
            } catch (e: JSONException) {
                e.printStackTrace()
                Snackbar.make(activity!!.findViewById(R.id.content), "Failed to delete character(s)", Snackbar.LENGTH_SHORT).show()
                return
            }

            setCharacterList(data.toList(classViewModel.classList.value!!))
        }
        Snackbar.make(activity!!.findViewById(R.id.content), "Deleted " + charactersDeleted.toString() + " character(s)", Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
        val inflater = actionMode.menuInflater
        inflater.inflate(R.menu.character_list_action_mode, menu)
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(actionMode: ActionMode, menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.delete) {
            deleteSelectedCharacters()
            actionMode.finish()
            return true
        }
        return false
    }

    override fun onDestroyActionMode(actionMode: ActionMode) {
        selectedCharacters.forEach { it.selected = false }
        listAdapter.updateItems(selectedCharacters)
        this.actionMode = null
    }
}
