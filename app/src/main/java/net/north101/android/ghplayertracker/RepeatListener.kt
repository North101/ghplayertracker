package net.north101.android.ghplayertracker

import android.graphics.Rect
import android.os.Handler
import android.view.MotionEvent
import android.view.View

class RepeatListener(
    private val holdListener: (View, Int) -> Unit,
    private val initialInterval: Long = android.view.ViewConfiguration.getLongPressTimeout().toLong(),
    private val repeatInterval: Long = 100
) : View.OnTouchListener {

    private val handler = Handler()
    private val handlerRunnable = object : Runnable {
        override fun run() {
            if (!downView!!.isEnabled) {
                handler.removeCallbacks(this)
                downView = null
                return
            }

            count += 1
            handler.postDelayed(this, repeatInterval)
            holdListener.invoke(downView!!, count)
        }
    }

    private var count = 0
    private var rect: Rect? = null // Variable rect to hold the bounds of the view
    private var downView: View? = null

    init {
        if (initialInterval < 0 || repeatInterval < 0)
            throw IllegalArgumentException("negative interval")
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                reset()

                handler.postDelayed(handlerRunnable, initialInterval)
                downView = view
                rect = Rect(view.left, view.top, view.right, view.bottom)
                holdListener.invoke(view, count)
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
        count = 0
        downView = null
    }
}