package net.north101.android.ghplayertracker

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import net.north101.android.ghplayertracker.data.Status
import net.north101.android.ghplayertracker.livedata.TrackerLiveData

class TrackerStatusViewHolder(itemView: View) : BaseViewHolder<TrackerLiveData>(itemView) {
    val statusDisarmView: ImageView = itemView.findViewById(R.id.status_disarm)
    val statusStunView: ImageView = itemView.findViewById(R.id.status_stun)
    val statusImmobilizeView: ImageView = itemView.findViewById(R.id.status_immobilize)
    val statusStrengthenView: ImageView = itemView.findViewById(R.id.status_strengthen)
    val statusPoisonView: ImageView = itemView.findViewById(R.id.status_poison)
    val statusWoundView: ImageView = itemView.findViewById(R.id.status_wound)
    val statusMuddleView: ImageView = itemView.findViewById(R.id.status_muddle)
    val statusInvisibleView: ImageView = itemView.findViewById(R.id.status_invisible)

    init {
        for (status in Status.values()) {
            statusToView(status).setOnClickListener {
                item!!.status[status]!!.value = !(item!!.status[status]!!.value)
            }
        }
    }

    val statusObservers: Map<Status, (Boolean) -> Unit> = Status.values().map { status ->
        val observer: ((Boolean) -> Unit) = {
            setImageViewGreyscale(statusToView(status), !it)
        }
        status to observer
    }.toMap()

    override fun bind(item: TrackerLiveData) {
        super.bind(item)

        for (o in statusObservers.entries) {
            item.status[o.key]!!.observeForever(o.value)
        }
    }

    override fun unbind() {
        item?.let {
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

    companion object {
        const val layout = R.layout.character_tracker_status_item

        fun inflate(parent: ViewGroup): TrackerStatusViewHolder {
            return TrackerStatusViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}
