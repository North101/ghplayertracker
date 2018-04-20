package net.north101.android.ghplayertracker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import net.north101.android.ghplayertracker.data.Character
import net.north101.android.ghplayertracker.data.CharacterClass

class ClassViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = ClassViewModel::class.java.simpleName

    val classList = ClassLiveData(this)
    val characterList = CharacterLiveData(this)

    override fun onCleared() {
        super.onCleared()
    }
}

enum class LiveDataState {
    INIT,
    LOADING,
    FINISHED,
    REFRESHING
}

class ClassLiveData(protected val context: ClassViewModel) : MutableLiveData<List<CharacterClass>>() {
    private val TAG = ClassLiveData::class.java.simpleName

    val state = MutableLiveData<LiveDataState>()

    init {
        state.value = LiveDataState.INIT
    }

    fun load() {
        ClassListTask(this, LiveDataState.LOADING).execute()
    }

    fun refresh() {
        ClassListTask(this, LiveDataState.REFRESHING).execute()
    }

    internal class ClassListTask(
            private val owner: ClassLiveData,
            private val state: LiveDataState
    ) : AsyncTask<Void, Void, List<CharacterClass>>() {
        private val TAG = ClassListTask::class.java.simpleName
        override fun onPreExecute() {
            owner.state.value = state
        }

        override fun doInBackground(vararg voids: Void): List<CharacterClass> {
            return CharacterClass.CharacterClassData.load(owner.context.getApplication()).toList()
        }

        override fun onPostExecute(data: List<CharacterClass>) {
            owner.value = data
            owner.state.value = LiveDataState.FINISHED
        }
    }
}

class CharacterLiveData(protected val context: ClassViewModel) : MutableLiveData<List<Character>>() {
    private val TAG = CharacterLiveData::class.java.simpleName

    val state = MutableLiveData<LiveDataState>()

    init {
        state.value = LiveDataState.INIT
    }

    fun load() {
        CharacterListLoadTask(this, LiveDataState.LOADING).execute()
    }

    fun refresh(classList: List<CharacterClass>) {
        CharacterListLoadTask(this, LiveDataState.REFRESHING).execute()
    }

    fun set(value: ArrayList<Character>) {
        this.value = value
    }

    internal class CharacterListLoadTask(
            private val owner: CharacterLiveData,
            private val state: LiveDataState
    ) : AsyncTask<Void, Void, Pair<List<CharacterClass>, ArrayList<Character>>>() {
        private val TAG = CharacterListLoadTask::class.java.simpleName

        override fun onPreExecute() {
            owner.state.value = state
        }

        override fun doInBackground(vararg voids: Void): Pair<List<CharacterClass>, ArrayList<Character>> {
            var classList = owner.context.classList.value
            if (classList == null) {
                classList = CharacterClass.CharacterClassData.load(owner.context.getApplication()).toList()
            }
            return Pair(classList, Character.CharacterData.load(owner.context.getApplication()).toList(classList))
        }

        override fun onPostExecute(data: Pair<List<CharacterClass>, ArrayList<Character>>) {
            owner.context.classList.value = data.first
            owner.value = data.second
            owner.state.value = LiveDataState.FINISHED
        }
    }
}