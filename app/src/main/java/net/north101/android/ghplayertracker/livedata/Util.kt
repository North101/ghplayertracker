package net.north101.android.ghplayertracker.livedata

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import java.util.HashMap

open class NonNullLiveData<T>(private val defaultValue: T) : MutableLiveData<T>() {
    override fun getValue(): T = super.getValue() ?: defaultValue

    private val observers = HashMap<(T) -> Unit, Observer<T>>()

    fun observe(owner: LifecycleOwner, body: (T) -> Unit) {
        if (observers.containsKey(body)) {
            removeObserver(body)
        }

        val observer = Observer<T> {
            body(it ?: defaultValue)
        }
        observers[body] = observer
        observe(owner, observer)
    }

    fun observeForever(body: (T) -> Unit) {
        val observer = Observer<T> {
            body(it ?: defaultValue)
        }
        observeForever(observer)
    }

    fun removeObserver(body: (T) -> Unit) {
        observers[body]?.let {
            removeObserver(it)
        }
    }
}

open class InitLiveData<T>(value: T) : NonNullLiveData<T>(value) {
    init {
        this.value = value
    }
}

open class BoundedIntLiveData(
    value: Int,
    var minValue: Int? = null,
    var maxValue: Int? = null
) : InitLiveData<Int>(value) {
    override fun setValue(value: Int?) {
        var newValue = value
        if (newValue != null) {
            if (minValue != null) {
                newValue = Math.max(newValue, minValue!!)
            }
            if (maxValue != null) {
                newValue = Math.min(newValue, maxValue!!)
            }
        }

        super.setValue(newValue)
    }
}