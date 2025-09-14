package net.exoad.filewatch.utils

abstract class Observable<T> {
    private val observers: MutableList<(T) -> Unit> = mutableListOf()

    fun notifyObservers(value: T) {
        observers.forEach { it(value) }
    }

    fun observe(observer: (T) -> Unit) {
        observers.add(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }
}

abstract class Notifiable {
    private val observers: MutableList<() -> Unit> = mutableListOf()

    fun notifyObservers() {
        observers.forEach { it() }
    }

    fun observe(observer: () -> Unit) {
        observers.add(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }
}