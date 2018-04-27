package net.north101.android.ghplayertracker

import android.arch.lifecycle.Observer
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import net.north101.android.ghplayertracker.data.Status

class TrackerSummonViewHolder(itemView: View) : BaseViewHolder<Summon>(itemView) {
    val nameView: TextView = itemView.findViewById(R.id.name)
    val deleteView: ImageView = itemView.findViewById(R.id.delete)

    var healthTextView: TextView = itemView.findViewById(R.id.health_text)
    var healthPlusView: View = itemView.findViewById(R.id.health_plus)
    var healthMinusView: View = itemView.findViewById(R.id.health_minus)
    var moveTextView: TextView = itemView.findViewById(R.id.move_text)
    var attackTextView: TextView = itemView.findViewById(R.id.attack_text)
    var rangeTextView: TextView = itemView.findViewById(R.id.range_text)

    val statusDisarmView: ImageView = itemView.findViewById(R.id.status_disarm)
    val statusStunView: ImageView = itemView.findViewById(R.id.status_stun)
    val statusImmobilizeView: ImageView = itemView.findViewById(R.id.status_immobilize)
    val statusStrengthenView: ImageView = itemView.findViewById(R.id.status_strengthen)
    val statusPoisonView: ImageView = itemView.findViewById(R.id.status_poison)
    val statusWoundView: ImageView = itemView.findViewById(R.id.status_wound)
    val statusMuddleView: ImageView = itemView.findViewById(R.id.status_muddle)
    val statusInvisibleView: ImageView = itemView.findViewById(R.id.status_invisible)

    init {
        deleteView.setOnClickListener {
            item!!.tracker.summons.value!!.remove(item!!)
            item!!.tracker.summons.value = item!!.tracker.summons.value
        }

        healthPlusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.health.value = item!!.health.value!! + 1
        })))
        healthMinusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.health.value = item!!.health.value!! - 1
        })))

        for (status in Status.values()) {
            statusToView(status).setOnClickListener {
                item!!.status[status]!!.value = !(item!!.status[status]!!.value!!)
            }
        }
    }

    val nameObserver = Observer<String> {
        if (it != null) {
            nameView.text = it
        }
    }
    val healthObserver = Observer<Int> {
        if (it != null) {
            healthTextView.text = it.toString()
            healthMinusView.isEnabled = (it > item!!.health.minValue!!)
        }
    }
    val moveObserver = Observer<Int> {
        if (it != null) {
            moveTextView.text = it.toString()
        }
    }
    val attackObserver = Observer<Int> {
        if (it != null) {
            attackTextView.text = it.toString()
        }
    }
    val rangeObserver = Observer<Int> {
        if (it != null) {
            rangeTextView.text = it.toString()
        }
    }

    val statusObservers: Map<Status, Observer<Boolean>> = Status.values().map { status ->
        status to Observer<Boolean> {
            if (it != null) {
                setImageViewGreyscale(statusToView(status), !it)
            }
        }
    }.toMap()

    override fun bind(item: Summon) {
        super.bind(item)

        item.name.observeForever(nameObserver)
        item.health.observeForever(healthObserver)
        item.move.observeForever(moveObserver)
        item.attack.observeForever(attackObserver)
        item.range.observeForever(rangeObserver)
        for (o in statusObservers.entries) {
            item.status[o.key]!!.observeForever(o.value)
        }
    }

    override fun unbind() {
        item?.let {
            item!!.name.removeObserver(nameObserver)
            item!!.health.removeObserver(healthObserver)
            item!!.move.removeObserver(moveObserver)
            item!!.attack.removeObserver(attackObserver)
            item!!.range.removeObserver(rangeObserver)
            for (o in statusObservers.entries) {
                item!!.status[o.key]!!.removeObserver(o.value)
            }
        }

        super.unbind()
    }

    fun statusToView(status: Status): ImageView {
        return when (status) {
            Status.disarm -> statusDisarmView
            Status.stun -> statusStunView
            Status.immobilize -> statusImmobilizeView
            Status.strengthen -> statusStrengthenView
            Status.poison -> statusPoisonView
            Status.wound -> statusWoundView
            Status.muddle -> statusMuddleView
            Status.invisible -> statusInvisibleView
        }
    }

    fun parseInt(TextView: TextView, fallback: Int): Int {
        return try {
            Integer.parseInt(TextView.text.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } ?: fallback
    }

    companion object {
        var layout = R.layout.character_tracker_summon_item

        fun inflate(parent: ViewGroup): TrackerSummonViewHolder {
            return TrackerSummonViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
