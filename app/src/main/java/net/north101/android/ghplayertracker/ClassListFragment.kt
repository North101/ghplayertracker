package net.north101.android.ghplayertracker

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import net.north101.android.ghplayertracker.data.Character
import net.north101.android.ghplayertracker.data.CharacterClass
import net.north101.android.ghplayertracker.data.CharacterClassGroup
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.ViewById


@EFragment(R.layout.class_list_layout)
open class ClassListFragment : Fragment() {
    @ViewById(R.id.class_list)
    protected lateinit var listView: RecyclerView
    @ViewById(R.id.loading)
    protected lateinit var loadingView: View

    lateinit var classModel: ClassModel

    protected lateinit var listAdapter: ClassListAdapter

    var onClickListener: BaseViewHolder.ClickListener<CharacterClass> = object : BaseViewHolder.ClickListener<CharacterClass>() {
        override fun onItemClick(holder: BaseViewHolder<CharacterClass>) {
            val character = Character(holder.item!!)

            val fragment = CharacterFragment.newInstance(character)
            fragmentManager!!.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    @AfterViews
    fun afterViews() {
        classModel = ViewModelProviders.of(this.activity!!).get(ClassModel::class.java)

        if (!::listAdapter.isInitialized) {
            listAdapter = ClassListAdapter()
        }
        listAdapter.setOnClickListener(onClickListener)

        val maxSpan = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2
        val gridLayoutManager = GridLayoutManager(context, maxSpan)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = listAdapter.getItem(position)
                return if (item is CharacterClass) {
                    1
                } else {
                    maxSpan
                }
            }
        }
        listView.layoutManager = gridLayoutManager

        val animator = SlideDownAlphaAnimator()
        listView.itemAnimator = animator

        listView.adapter = listAdapter

        if (classModel.dataLoader.state.value != LiveDataState.FINISHED) {
            classModel.dataLoader.load()
        }
        view!!.post {
            classModel.classGroupList.observe(this, Observer {
                setClassList(it)
            })
        }
    }

    open fun setClassList(classGroupList: ArrayList<CharacterClassGroup>?) {
        if (this.isRemoving) {
            return
        }

        loadingView.visibility = View.GONE
        listView.visibility = View.VISIBLE

        if (classGroupList != null) {
            listAdapter.updateItems(classGroupList)
        }
    }

    companion object {
        fun newInstance(): ClassListFragment_ {
            return ClassListFragment_()
        }
    }
}
