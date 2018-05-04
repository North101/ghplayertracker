package net.north101.android.ghplayertracker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Bundle
import net.north101.android.ghplayertracker.data.Character
import net.north101.android.ghplayertracker.livedata.CharacterLiveData

class CharacterModel(application: Application) : AndroidViewModel(application) {
    lateinit var character: CharacterLiveData

    fun init(character: Character) {
        this.character = CharacterLiveData(character)
    }

    fun fromBundle(bundle: Bundle) {
        this.character = CharacterLiveData(bundle.getParcelable("character"))
    }

    fun toBundle(): Bundle {
        val bundle = Bundle()

        bundle.putParcelable("character", character.toParcel())

        return bundle
    }
}

