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
        listView.layoutManager = gridLayoutManager

        val animator = SlideDownAlphaAnimator()
        listView.itemAnimator = animator

        listView.adapter = listAdapter

        if (classModel.classList.value == null) {
            classModel.classList.load()
        }
        view!!.post {
            classModel.classList.observe(this, Observer {
                setClassList(it)
            })
        }
    }

    open fun setClassList(classList: List<CharacterClass>?) {
        if (this.isRemoving) {
            return
        }

        loadingView.visibility = View.GONE
        listView.visibility = View.VISIBLE

        if (classList != null) {
            listAdapter.updateItems(classList)
        }
    }

    companion object {
        fun newInstance() : ClassListFragment_ {
            return ClassListFragment_()
        }
    }
}
