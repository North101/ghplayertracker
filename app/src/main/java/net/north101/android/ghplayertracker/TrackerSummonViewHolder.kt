package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import net.north101.android.ghplayertracker.data.Status
import net.north101.android.ghplayertracker.livedata.SummonLiveData

class TrackerSummonViewHolder(itemView: View) : BaseViewHolder<SummonLiveData>(itemView) {
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

    var onSummonDeleteClick: ((SummonLiveData) -> Unit)? = null

    init {
        deleteView.setOnClickListener {
            onSummonDeleteClick?.invoke(item!!)
        }

        healthPlusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.health.value += 1
        })))
        healthMinusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.health.value -= 1
        })))

        for (status in Status.values()) {
            statusToView(status).setOnClickListener {
                item!!.status[status]!!.value = !(item!!.status[status]!!.value)
            }
        }
    }

    val nameObserver: (String) -> Unit = {
        nameView.text = it
    }
    val healthObserver: (Int) -> Unit = {
        healthTextView.text = it.toString()
        healthMinusView.isEnabled = (it > item!!.health.minValue!!)
    }
    val moveObserver: (Int) -> Unit = {
        moveTextView.text = it.toString()
    }
    val attackObserver: (Int) -> Unit = {
        attackTextView.text = it.toString()
    }
    val rangeObserver: (Int) -> Unit = {
        rangeTextView.text = it.toString()
    }

    val statusObservers: Map<Status, (Boolean) -> Unit> = Status.values().map { status ->
        val observer: (Boolean) -> Unit = {
            setImageViewGreyscale(statusToView(status), !it)
        }
        status to observer
    }.toMap()

    override fun bind(item: SummonLiveData) {
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
