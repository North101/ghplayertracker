package net.north101.android.ghplayertracker.livedata

import android.arch.lifecycle.MutableLiveData
import net.north101.android.ghplayertracker.data.Ability
import net.north101.android.ghplayertracker.data.Character
import net.north101.android.ghplayertracker.data.CharacterClass
import net.north101.android.ghplayertracker.data.Item
import java.util.*
import kotlin.collections.ArrayList

class CharacterLiveData {
    val id: UUID
    val characterClass: CharacterClass

    var created = InitLiveData(Date())
    var modified = InitLiveData(Date())

    val name = InitLiveData("")
    val level = BoundedIntLiveData(1, minValue = 1, maxValue = 9)
    val xp = BoundedIntLiveData(0, minValue = 0)
    val gold = BoundedIntLiveData(0, minValue = 0)
    val perks = InitLiveData<List<PerkLiveData>>(ArrayList())
    val perkNotes = InitLiveData<List<PerkNoteLiveData>>(ArrayList())
    val retired = InitLiveData(false)
    val items = InitLiveData<ArrayList<ItemLiveData>>(ArrayList())
    val abilities = InitLiveData<ArrayList<MutableLiveData<Ability>>>(ArrayList())
    val notes = InitLiveData<ArrayList<InitLiveData<String>>>(ArrayList())

    constructor(character: Character) {
        id = character.id
        characterClass = character.characterClass

        name.value = character.name
        level.value = character.level
        xp.value = character.xp
        gold.value = character.gold
        perks.value = character.characterClass.perks.withIndex().map {
            PerkLiveData(it.value, character.perks.getOrElse(it.index, { 0 }))
        }
        perkNotes.value = character.perkNotes.map {
            PerkNoteLiveData(it)
        }
        created.value = character.created
        modified.value = character.modified
        retired.value = character.retired
        items.value = ArrayList(character.items.map {
            ItemLiveData(it.name, it.type)
        })
        abilities.value = ArrayList(character.abilities.map {
            val item = MutableLiveData<Ability>()
            item.value = it
            item
        })
        notes.value = ArrayList(character.notes.map {
            InitLiveData(it)
        })
    }

    fun toParcel(): Character {
        return Character(
            id,
            characterClass,
            name.value,
            level.value,
            xp.value,
            gold.value,
            ArrayList(perks.value.map {
                it.value
            }),
            ArrayList(perkNotes.value.map {
                it.value
            }),
            created.value,
            modified.value,
            retired.value,
            ArrayList(items.value.map {
                Item(it.name.value, it.type.value)
            }),
            ArrayList(abilities.value.map {
                it.value
            }),
            ArrayList(notes.value.map {
                it.value
            })
        )
    }
}