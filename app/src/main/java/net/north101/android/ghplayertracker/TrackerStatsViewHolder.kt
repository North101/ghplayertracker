package net.north101.android.ghplayertracker

import android.graphics.Rect
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.TextView
import net.north101.android.ghplayertracker.livedata.TrackerLiveData

class TrackerStatsViewHolder(itemView: View) : BaseViewHolder<TrackerLiveData>(itemView) {
    var healthContainerView: View = itemView.findViewById(R.id.health_container)
    var healthTextView: TextView = itemView.findViewById(R.id.health_text)
    var healthPlusView: View = itemView.findViewById(R.id.health_plus)
    var healthMinusView: View = itemView.findViewById(R.id.health_minus)

    var xpContainerView: View = itemView.findViewById(R.id.xp_container)
    var xpTextView: TextView = itemView.findViewById(R.id.xp_text)
    var xpPlusView: View = itemView.findViewById(R.id.xp_plus)
    var xpMinusView: View = itemView.findViewById(R.id.xp_minus)

    var lootContainerView: View = itemView.findViewById(R.id.loot_container)
    var lootTextView: TextView = itemView.findViewById(R.id.loot_text)
    var lootPlusView: View = itemView.findViewById(R.id.loot_plus)
    var lootMinusView: View = itemView.findViewById(R.id.loot_minus)

    var onNumberClickListener: ((String) -> Unit)? = null

    init {
        healthContainerView.setOnClickListener {
            onNumberClickListener?.invoke("health")
        }
        healthPlusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.health.value += 1
        })))
        healthMinusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.health.value -= 1
        })))

        xpContainerView.setOnClickListener {
            onNumberClickListener?.invoke("xp")
        }
        xpPlusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.xp.value += 1
        })))
        xpMinusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.xp.value -= 1
        })))

        lootContainerView.setOnClickListener {
            onNumberClickListener?.invoke("loot")
        }
        lootPlusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.loot.value += 1
        })))
        lootMinusView.setOnTouchListener(RepeatListener(400, 100, View.OnClickListener({
            item!!.loot.value -= 1
        })))
    }

    val healthObserver: (Int) -> Unit = {
        healthTextView.text = it.toString()
        healthPlusView.isEnabled = (it < item!!.health.maxValue!!)
        healthMinusView.isEnabled = (it > item!!.health.minValue!!)
    }
    val xpObserver: (Int) -> Unit = {
        xpTextView.text = it.toString()
        xpMinusView.isEnabled = (it > item!!.health.minValue!!)
    }
    val goldObserver: (Int) -> Unit = {
        lootTextView.text = it.toString()
        lootMinusView.isEnabled = (it > item!!.health.minValue!!)
    }

    override fun bind(item: TrackerLiveData) {
        super.bind(item)

        item.health.observeForever(healthObserver)
        item.xp.observeForever(xpObserver)
        item.loot.observeForever(goldObserver)
    }

    override fun unbind() {
        item?.let {
            item!!.health.removeObserver(healthObserver)
            item!!.xp.removeObserver(xpObserver)
            item!!.loot.removeObserver(goldObserver)
        }

        super.unbind()
    }

    companion object {
        var layout = R.layout.character_tracker_stats_item

        fun inflate(parent: ViewGroup): TrackerStatsViewHolder {
            return TrackerStatsViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}


class RepeatListener(
    private val initialInterval: Long,
    private val normalInterval: Long,
    private val clickListener: View.OnClickListener?
) : OnTouchListener {

    private val handler = Handler()
    private val handlerRunnable = object : Runnable {
        override fun run() {
            if (!downView!!.isEnabled) {
                handler.removeCallbacks(this)
                downView = null
                return
            }

            handler.postDelayed(this, normalInterval)
            clickListener?.onClick(downView)
        }
    }

    private var rect: Rect? = null // Variable rect to hold the bounds of the view
    private var downView: View? = null

    init {
        if (clickListener == null)
            throw IllegalArgumentException("null runnable")
        if (initialInterval < 0 || normalInterval < 0)
            throw IllegalArgumentException("negative interval")
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                reset()

                handler.postDelayed(handlerRunnable, initialInterval)
                downView = view
                rect = Rect(view.left, view.top, view.right, view.bottom)
                clickListener?.onClick(view)
            }
            MotionEvent.ACTION_UP -> reset()
            MotionEvent.ACTION_MOVE -> if (!rect!!.contains(view.left + motionEvent.x.toInt(), view.top + motionEvent.y.toInt())) {
                reset()
            }
            MotionEvent.ACTION_CANCEL -> reset()
        }
        return true
    }

    private fun reset() {
        handler.removeCallbacks(handlerRunnable)
        downView = null
    }
}