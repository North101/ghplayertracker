package net.north101.android.ghplayertracker

import android.app.SearchManager
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import net.north101.android.ghplayertracker.data.Item
import net.north101.android.ghplayertracker.data.ItemType
import org.androidannotations.annotations.*


@OptionsMenu(R.menu.item)
@EFragment(R.layout.item_list_layout)
open class ItemListFragment : Fragment(), OnBackPressedListener, SearchView.OnQueryTextListener {
    @ViewById(R.id.toolbar)
    protected lateinit var toolbar: Toolbar
    @ViewById(R.id.fab)
    protected lateinit var fab: FloatingActionButton
    @ViewById(R.id.character_list)
    protected lateinit var listView: RecyclerView
    @ViewById(R.id.loading)
    protected lateinit var loadingView: View

    @OptionsMenuItem(R.id.search)
    protected lateinit var menuSearch: MenuItem

    protected lateinit var searchView: SearchView
    protected lateinit var listAdapter: ItemListAdapter

    @FragmentArg(ARG_DISABLED_ITEM_LIST)
    protected lateinit var disabledItemList: ArrayList<Item>

    lateinit var classModel: ClassModel
    var selectedItemModel: SelectedItemModel? = null

    @AfterViews
    fun afterViews() {
        classModel = ViewModelProviders.of(this.activity!!).get(ClassModel::class.java)
        if (targetFragment != null) {
            selectedItemModel = ViewModelProviders.of(targetFragment!!).get(SelectedItemModel::class.java)
        }

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        val listViewLayoutManager = LinearLayoutManager(context)
        listView.layoutManager = listViewLayoutManager

        val animator = SlideDownAlphaAnimator()
        listView.itemAnimator = animator

        if (!::listAdapter.isInitialized) {
            listAdapter = ItemListAdapter(
                ItemView.Detail,
                ItemOptions("", HashSet(), HashSet(), true, ItemSort.Id)
            )
        }
        listAdapter.onItemImageClick = {
            val items = listAdapter.items.filter {
                it is Item
            } as ArrayList<ImageUrl>
            val fragment = ImagePagerFragment.newInstance(items, items.indexOf(it))

            activity!!.supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit()
        }
        listAdapter.onItemViewClick = { item ->
            selectedItemModel?.let {
                it.selectedItem.value = item

                activity!!.supportFragmentManager
                    .popBackStack()
            }
        }
        //listAdapter.setOnClickListener(onClickListener)
        listView.adapter = listAdapter

        view!!.post {
            listAdapter.updateItems(classModel.itemList.value, classModel.itemCategoryList.value, disabledItemList)
        }
    }

    override fun onResume() {
        super.onResume()

        view!!.post {
            classModel.itemList.observe(this, Observer {
                if (it != null) {
                    setItemList(it)
                }
            })
            if (classModel.dataLoader.state.value != LiveDataState.FINISHED) {
                classModel.dataLoader.load()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        classModel.itemList.removeObservers(this)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)

        if (menu == null) return

        // setOnQueryTextListener will clear this, so make a copy
        val query = listAdapter.itemOptions.text

        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        menuSearch = menu.findItem(R.id.search)
        searchView = menuSearch.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)

        //focus the SearchView
        if (!query.isEmpty()) {
            menuSearch.expandActionView()
            searchView.setQuery(query, true)
            searchView.clearFocus()
        }

        val sortMenu = menu.findItem(R.id.sort).subMenu
        sortMenu.setGroupCheckable(0, true, true)
        if (sortMenu.size() == 0) {
            for ((index, itemSort) in ItemSort.values().withIndex()) {
                sortMenu
                    .add(0, View.generateViewId(), index, itemSort.name)
                    .setCheckable(true)
                    .setChecked(listAdapter.itemOptions.sort == itemSort)
                    .setOnMenuItemClickListener {
                        listAdapter.itemOptions.sort = itemSort
                        listAdapter.refresh()
                        it.isChecked = true
                        false
                    }
            }
        }
        val typeMenu = menu.findItem(R.id.filter_types).subMenu
        if (typeMenu.size() == 0) {
            for ((index, itemType) in ItemType.values().withIndex()) {
                typeMenu
                    .add(0, View.generateViewId(), index, itemType.name)
                    .setCheckable(true)
                    .setChecked(listAdapter.itemOptions.itemTypes.contains(itemType))
                    .setOnMenuItemClickListener {
                        if (it.isChecked) {
                            listAdapter.itemOptions.itemTypes.remove(itemType)
                        } else {
                            listAdapter.itemOptions.itemTypes.add(itemType)
                        }
                        listAdapter.refresh()
                        it.isChecked = !it.isChecked
                        false
                    }
            }
        }
        classModel.itemCategoryList.observe(this, Observer {
            if (it != null) {
                val categoryMenu = menu.findItem(R.id.filter_categories).subMenu
                if (categoryMenu.size() == 0) {
                    for ((index, itemCategory) in it.withIndex()) {
                        categoryMenu
                            .add(0, View.generateViewId(), index, itemCategory.name)
                            .setCheckable(true)
                            .setChecked(listAdapter.itemOptions.itemCategories.contains(itemCategory))
                            .setOnMenuItemClickListener {
                                if (it.isChecked) {
                                    listAdapter.itemOptions.itemCategories.remove(itemCategory)
                                } else {
                                    listAdapter.itemOptions.itemCategories.add(itemCategory)
                                }
                                listAdapter.refresh()
                                it.isChecked = !it.isChecked
                                true
                            }
                    }
                }
            }
        })
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    open fun setItemList(itemMap: Map<String, Item>) {
        if (this.isRemoving)
            return

        loadingView.visibility = View.GONE
        listView.visibility = View.VISIBLE

        listAdapter.updateItems(itemMap, classModel.itemCategoryList.value, disabledItemList)
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onQueryTextChange(text: String?): Boolean {
        listAdapter.itemOptions.text = text.orEmpty()
        listAdapter.refresh()
        return false
    }

    override fun onQueryTextSubmit(text: String?): Boolean {
        listAdapter.itemOptions.text = text.orEmpty()
        listAdapter.refresh()
        return true
    }

    companion object {
        const val ARG_DISABLED_ITEM_LIST = "disabled_item_list"

        fun newInstance(disabledItemList: ArrayList<Item> = ArrayList()): ItemListFragment_ {
            val args = Bundle()
            args.putParcelableArrayList(ARG_DISABLED_ITEM_LIST, disabledItemList)

            val fragment = ItemListFragment_()
            fragment.arguments = args

            return fragment
        }
    }
}

class SelectedItemModel : ViewModel() {
    val selectedItem = MutableLiveData<Item>()
}