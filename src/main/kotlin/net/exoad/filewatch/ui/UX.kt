package net.exoad.filewatch.ui

import javax.swing.AbstractListModel
import javax.swing.Timer

class MutableState<T>(initialValue: T)
{
    private var _value = initialValue
    private val observers = mutableListOf<(T) -> Unit>()
    var value: T
        get() = _value
        set(newValue)
        {
            if(_value != newValue)
            {
                _value = newValue
                observers.forEach { it(newValue) }
            }
        }

    fun observe(observer: (T) -> Unit)
    {
        observers.add(observer)
    }

    operator fun invoke(): T
    {
        return value
    }

    operator fun invoke(t: T)
    {
        value = t
    }
}

class Notifier
{
    private val observers = mutableListOf<() -> Unit>()

    fun observe(observer: () -> Unit)
    {
        observers.add(observer)
    }

    fun notifyObservers()
    {
        observers.forEach { it() }
    }

    operator fun invoke()
    {
        notifyObservers()
    }
}

interface InternalState<T>
{
    fun applyTo(component: T)
}

fun listen(): Notifier
{
    return Notifier()
}

class PeriodicState<T>(initialValue: T, period: Int, producer: () -> T)
{
    private val observers = mutableListOf<(T) -> Unit>()
    private val timer: Timer
    private var _value = initialValue
    private var value: T
        get() = _value
        set(newValue)
        {
            if(_value != newValue)
            {
                _value = newValue
                observers.forEach { it(newValue) }
            }
        }

    init
    {
        timer = Timer(period) {
            value = producer()
        }
    }

    fun start()
    {
        if(!timer.isRunning)
        {
            timer.start()
        }
    }

    fun stop()
    {
        if(timer.isRunning)
        {
            timer.stop()
        }
    }

    fun observe(observer: (T) -> Unit)
    {
        observers.add(observer)
    }
}

fun <T> refresh(initialValue: T, period: Int, producer: () -> T): PeriodicState<T>
{
    return PeriodicState(initialValue, period, producer)
}

fun <T> remember(initialValue: T): MutableState<T>
{
    return MutableState(initialValue)
}

abstract class ReferenceListModel<T>() : AbstractListModel<T>()
{
    fun notifyChanges(source: Any, index0: Int, index1: Int)
    {
        fireContentsChanged(source, index0, index1)
    }

    fun notifyAdded(source: Any, index0: Int, index1: Int)
    {
        fireIntervalAdded(source, index0, index1)
    }

    fun notifyRemoved(source: Any, index0: Int, index1: Int)
    {
        fireIntervalRemoved(source, index0, index1)
    }
}

fun <T> listModel(size: () -> Int, elementAt: (Int) -> T): ReferenceListModel<T>
{
    return object : ReferenceListModel<T>()
    {
        override fun getSize(): Int
        {
            return size()
        }

        override fun getElementAt(index: Int): T?
        {
            return elementAt(index)
        }
    }
}