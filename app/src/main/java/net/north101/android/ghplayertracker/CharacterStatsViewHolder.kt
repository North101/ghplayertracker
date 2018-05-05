package net.north101.android.ghplayertracker

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import net.north101.android.ghplayertracker.livedata.CharacterLiveData

class CharacterStatsViewHolder(itemView: View) : BaseViewHolder<CharacterLiveData>(itemView) {
    var nameView: TextView = itemView.findViewById(R.id.name)
    var levelsView: TextView = itemView.findViewById(R.id.level_text)

    var levelContainerView: View = itemView.findViewById(R.id.level_container)
    var levelTextView: TextView = itemView.findViewById(R.id.level_text)
    var levelPlusView: View = itemView.findViewById(R.id.level_plus)
    var levelMinusView: View = itemView.findViewById(R.id.level_minus)

    var maxHealthView: TextView = itemView.findViewById(R.id.health_text)
    var maxXPView: TextView = itemView.findViewById(R.id.max_xp_text)

    var xpContainerView: View = itemView.findViewById(R.id.xp_container)
    var xpTextView: TextView = itemView.findViewById(R.id.xp_text)
    var xpPlusView: View = itemView.findViewById(R.id.xp_plus)
    var xpMinusView: View = itemView.findViewById(R.id.xp_minus)

    var goldContainerView: View = itemView.findViewById(R.id.gold_container)
    var goldTextView: TextView = itemView.findViewById(R.id.gold_text)
    var goldPlusView: View = itemView.findViewById(R.id.gold_plus)
    var goldMinusView: View = itemView.findViewById(R.id.gold_minus)

    var retiredView: CheckBox = itemView.findViewById(R.id.retired)

    var onNumberClick: ((String) -> Unit)? = null

    init {
        nameView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val text = p0.toString()
                if (item!!.name.value != text) {
                    item!!.name.value = text
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        levelContainerView.setOnClickListener {
            onNumberClick?.invoke("level")
        }
        levelPlusView.setOnTouchListener(RepeatListener({ _, _ ->
            item!!.level.value += 1
        }))
        levelMinusView.setOnTouchListener(RepeatListener({ _, _ ->
            item!!.level.value -= 1
        }))

        xpContainerView.setOnClickListener {
            onNumberClick?.invoke("xp")
        }
        xpPlusView.setOnTouchListener(RepeatListener({ _, count ->
            if (count >= 10) {
                item!!.xp.value = ((5 * Math.floor(item!!.xp.value / 5.0)) + 5).toInt()
            } else {
                item!!.xp.value += 1
            }
        }))
        xpMinusView.setOnTouchListener(RepeatListener({ _, count ->
            if (count >= 10) {
                item!!.xp.value = ((5 * Math.ceil(item!!.xp.value / 5.0)) - 5).toInt()
            } else {
                item!!.xp.value -= 1
            }
        }))

        goldContainerView.setOnClickListener {
            onNumberClick?.invoke("gold")
        }
        goldPlusView.setOnTouchListener(RepeatListener({ _, count ->
            if (count >= 10) {
                item!!.gold.value = ((5 * Math.floor(item!!.gold.value / 5.0)) + 5).toInt()
            } else {
                item!!.gold.value += 1
            }
        }))
        goldMinusView.setOnTouchListener(RepeatListener({ _, count ->
            if (count >= 10) {
                item!!.gold.value = ((5 * Math.ceil(item!!.gold.value / 5.0)) - 5).toInt()
            } else {
                item!!.gold.value -= 1
            }
        }))
        retiredView.setOnCheckedChangeListener { _, b ->
            if (item!!.retired.value != b) {
                item!!.retired.value = b
            }
        }
    }

    val nameObserver: (String) -> Unit = {
        if (nameView.text.toString() != it) {
            nameView.text = it
        }
    }
    val levelObserver: (Int) -> Unit = {
        val levelInfo = item!!.characterClass.levels.find { levelInfo -> levelInfo.level == it }!!
        levelsView.text = levelInfo.level.toString()
        maxHealthView.text = levelInfo.health.toString()
        maxXPView.text = levelInfo.maxXP?.toString() ?: "∞"
    }
    val xpObserver: (Int) -> Unit = {
        xpTextView.text = it.toString()
    }
    val goldObserver: (Int) -> Unit = {
        goldTextView.text = it.toString()
    }
    val retiredObserver: (Boolean) -> Unit = {
        retiredView.isChecked = it
    }

    override fun bind(item: CharacterLiveData) {
        super.bind(item)

        item.name.observeForever(nameObserver)
        item.level.observeForever(levelObserver)
        item.xp.observeForever(xpObserver)
        item.gold.observeForever(goldObserver)
        item.retired.observeForever(retiredObserver)
    }

    override fun unbind() {
        item?.name?.removeObserver(nameObserver)
        item?.level?.removeObserver(levelObserver)
        item?.xp?.removeObserver(xpObserver)
        item?.gold?.removeObserver(goldObserver)
        item?.retired?.removeObserver(retiredObserver)

        super.unbind()
    }

    companion object {
        var layout = R.layout.character_stats_item

        fun inflate(parent: ViewGroup): CharacterStatsViewHolder {
            return CharacterStatsViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}