package net.north101.android.ghplayertracker

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import net.north101.android.ghplayertracker.data.*
import java.util.*
import kotlin.collections.ArrayList

class CharacterModel(application: Application) : AndroidViewModel(application) {
    var id = InitLiveData<UUID>()
    var characterClass = InitLiveData<CharacterClass>()
    var created = InitLiveData<Date>()
    var modified = InitLiveData<Date>()

    val name = InitLiveData<String>()
    val level = BoundedIntLiveData(minValue = 1, maxValue = 9)
    val xp = BoundedIntLiveData(minValue = 0)
    val gold = BoundedIntLiveData(minValue = 0)
    val perks = InitLiveData<List<PerkData>>()
    val perkNotes = InitLiveData<List<PerkNoteData>>()
    val retired = InitLiveData<Boolean>()
    val items = InitLiveData<ArrayList<ItemData>>()
    val notes = InitLiveData<ArrayList<InitLiveData<String>>>()

    fun fromData(character: Character) {
        id.value = character.id
        characterClass.value = character.characterClass

        name.value = character.name
        level.value = character.level
        xp.value = character.xp
        gold.value = character.gold
        perks.value = character.characterClass.perks.withIndex().map {
            PerkData(it.value, character.perks.getOrElse(it.index, { 0 }))
        }
        perkNotes.value = character.perkNotes.map {
            PerkNoteData(it)
        }
        created.value = character.created
        modified.value = character.modified
        retired.value = character.retired
        items.value = ArrayList(character.items.map {
            ItemData(it.name, it.type)
        })
        notes.value = ArrayList(character.notes.map {
            InitLiveData(it)
        })
    }

    fun toData(): Character {
        return Character(
                id.value!!,
                characterClass.value!!,
                name.value!!,
                level.value!!,
                xp.value!!,
                gold.value!!,
                ArrayList(perks.value!!.map {
                    it.value!!
                }),
                ArrayList(perkNotes.value!!.map {
                    it.value!!
                }),
                created.value!!,
                modified.value!!,
                retired.value!!,
                ArrayList(items.value!!.map {
                    Item(it.name.value!!, it.type.value!!)
                }),
                ArrayList(notes.value!!.map {
                    it.value!!
                })
        )
    }
}

open class InitLiveData<T>(value: T? = null) : MutableLiveData<T>() {
    init {
        this.value = value
    }
}

class PerkData(
        val perk: Perk,
        value: Int?
) : InitLiveData<Int>(value)

class PerkNoteData(value: Int? = null) : BoundedIntLiveData(value, minValue = 0, maxValue = 3)

class ItemData(name: String? = null, type: ItemType? = null) {
    val name = InitLiveData<String>()
    val type = InitLiveData<ItemType>()

    init {
        this.name.value = name
        this.type.value = type
    }
}