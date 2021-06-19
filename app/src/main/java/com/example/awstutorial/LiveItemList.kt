package com.example.awstutorial

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LiveItemList<T> : Iterable<T>{
    private val _items = MutableLiveData<MutableList<T>>(mutableListOf())


    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.postValue(this.value)
    }

    fun addItem(item: T) {
        val items = _items.value
        if (items != null) {
            items.add(item)
            _items.notifyObserver()
        }
    }

    fun deleteItem(at: Int) : T? {
        val removedItem = _items.value?.removeAt(at)
        if (removedItem != null) {
            _items.notifyObserver()
        }
        return removedItem
    }

    fun clearItems() {
        val items = _items.value
        if (items != null) {
            items.clear()
            _items.notifyObserver()
        }
    }


    override fun iterator(): MutableIterator<T> {
        return _items.value?.iterator() ?: mutableListOf<T>().iterator()t
    }
}