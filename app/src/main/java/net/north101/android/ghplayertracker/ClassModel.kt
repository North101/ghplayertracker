package net.north101.android.ghplayertracker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import net.north101.android.ghplayertracker.data.*
import net.north101.android.ghplayertracker.livedata.NonNullLiveData

class ClassModel(
    application: Application
) : AndroidViewModel(application) {
    private val TAG = ClassModel::class.java.simpleName

    val dataLoader = DataLoader(this)

    val itemCategoryList = NonNullLiveData<ArrayList<ItemCategory>>(ArrayList())
    val itemList = NonNullLiveData<HashMap<String, Item>>(HashMap())
    val classList = NonNullLiveData<HashMap<String, CharacterClass>>(HashMap())
    val characterList = NonNullLiveData<ArrayList<Character>>(ArrayList())

    val context
        get() = this.getApplication<Application>()
}

enum class LiveDataState {
    INIT,
    LOADING,
    FINISHED
}

class DataLoader(
    private val model: ClassModel
) {
    private val TAG = DataLoader::class.java.simpleName

    val state = MutableLiveData<LiveDataState>()
    var task: DataLoaderTask? = null

    init {
        state.value = LiveDataState.INIT
    }

    fun load(): DataLoaderTask {
        var task = this.task
        if (task != null && !task.isCancelled && task.status != AsyncTask.Status.FINISHED)
            return task

        task = DataLoaderTask(this.model)
        task.execute()
        this.task = task

        return task
    }
}

data class DataResult(
    val itemCategoryList: ArrayList<ItemCategory>,
    val itemList: HashMap<String, Item>,
    val classList: HashMap<String, CharacterClass>,
    val characterList: ArrayList<Character>
)

class DataLoaderTask(
    private val model: ClassModel
) : AsyncTask<Void, Void, DataResult>() {
    private val TAG = DataLoaderTask::class.java.simpleName

    override fun onPreExecute() {
        this.model.dataLoader.state.value = LiveDataState.LOADING
    }

    override fun doInBackground(vararg voids: Void): DataResult {
        val itemCategoryList = ItemCategoryData.load(model.context).toList()
        val itemMap = ItemData.load(model.context).toList(itemCategoryList)
        val classMap = CharacterClassData.load(model.context).toList()
        val characterList = CharacterData.load(model.context).toList(classMap, itemMap)

        return DataResult(itemCategoryList, itemMap, classMap, characterList)
    }

    override fun onPostExecute(data: DataResult) {
        model.itemCategoryList.value = data.itemCategoryList
        model.itemList.value = data.itemList
        model.classList.value = data.classList
        model.characterList.value = data.characterList

        this.model.dataLoader.state.value = LiveDataState.FINISHED
    }
}