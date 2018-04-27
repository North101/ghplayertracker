package net.north101.android.ghplayertracker

import android.arch.lifecycle.Observer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView


class CharacterStatsViewHolder(itemView: View) : BaseViewHolder<CharacterModel>(itemView) {
    var nameView: TextView = itemView.findViewById(R.id.name)
    var levelsView: TextView = itemView.findViewById(R.id.level_text)

    var levelContainerView: View = itemView.findViewById(R.id.level_container)
    var levelTextView: TextView = itemView.findViewById(R.id.level_text)
    var levelPlusView: View = itemView.findViewById(R.id.level_plus)
    var levelMinusView: View = itemView.findViewById(R.id.level_minus)

    var maxHealthView: TextView = itemView.findViewById(R.id.health_text)
    var minXPView: TextView = itemView.findViewById(R.id.min_xp_text)
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
        nameView.addTextChangedListener(object: TextWatcher {
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
        levelPlusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.level.value = item!!.level.value!! + 1
        })))
        levelMinusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.level.value = item!!.level.value!! - 1
        })))

        xpContainerView.setOnClickListener {
            onNumberClick?.invoke("xp")
        }
        xpPlusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.xp.value = item!!.xp.value!! + 1
        })))
        xpMinusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.xp.value = item!!.xp.value!! - 1
        })))

        goldContainerView.setOnClickListener {
            onNumberClick?.invoke("gold")
        }
        goldPlusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.gold.value = item!!.gold.value!! + 1
        })))
        goldMinusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.gold.value = (item!!.gold.value!! - 1)
        })))
        retiredView.setOnCheckedChangeListener { compoundButton, b ->
            if (item!!.retired.value != b) {
                item!!.retired.value = b
            }
        }
    }

    val nameObserver = Observer<String> {
        it?.let {
            if (nameView.text.toString() != it) {
                nameView.text = it
            }
        }
    }
    val levelObserver = Observer<Int> {
        it?.let {
            val levelInfo = item!!.characterClass.value!!.levels.find { levelInfo -> levelInfo.level == it }!!
            levelsView.text = levelInfo.level.toString()
            maxHealthView.text = levelInfo.health.toString()
            minXPView.text = levelInfo.minXP.toString()
            maxXPView.text = levelInfo.maxXP?.toString() ?: "∞"
        }
    }
    val xpObserver = Observer<Int> {
        it?.let {
            xpTextView.text = it.toString()
        }
    }
    val goldObserver = Observer<Int> {
        it?.let {
            goldTextView.text = it.toString()
        }
    }
    val retiredObserver = Observer<Boolean> {
        it?.let {
            retiredView.isChecked = it
        }
    }

    override fun bind(item: CharacterModel) {
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