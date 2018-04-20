package net.north101.android.ghplayertracker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.os.AsyncTask
import net.north101.android.ghplayertracker.data.Character
import net.north101.android.ghplayertracker.data.CharacterClass

class ClassViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = ClassViewModel::class.java.simpleName

    val classList = ClassLiveData(application)
    val characterList = CharacterLiveData(application)

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

class ClassLiveData(protected val context: Context) : LiveData<List<CharacterClass>>() {
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
            return CharacterClass.CharacterClassData.load(owner.context).toList()
        }

        override fun onPostExecute(data: List<CharacterClass>) {
            owner.value = data
            owner.state.value = LiveDataState.FINISHED
        }
    }
}

class CharacterLiveData(protected val context: Context) : LiveData<List<Character>>() {
    private val TAG = CharacterLiveData::class.java.simpleName

    val state = MutableLiveData<LiveDataState>()

    init {
        state.value = LiveDataState.INIT
    }

    fun load(classList: List<CharacterClass>) {
        CharacterListTask(this, LiveDataState.LOADING, classList).execute()
    }

    fun refresh(classList: List<CharacterClass>) {
        CharacterListTask(this, LiveDataState.REFRESHING, classList).execute()
    }

    internal class CharacterListTask(
            private val owner: CharacterLiveData,
            private val state: LiveDataState,
            private val classList: List<CharacterClass>
    ) : AsyncTask<Void, Void, List<Character>>() {
        private val TAG = CharacterListTask::class.java.simpleName

        override fun onPreExecute() {
            owner.state.value = state
        }

        override fun doInBackground(vararg voids: Void): List<Character> {
            return Character.CharacterData.load(owner.context).toList(classList)
        }

        override fun onPostExecute(data: List<Character>) {
            owner.value = data
            owner.state.value = LiveDataState.FINISHED
        }
    }
}