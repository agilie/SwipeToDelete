package com.example.swipetodeletelib.interfaces


interface ISwipeToDeleteAdapter<K, in V, in H> {

    fun notifyItemRemoved(position: Int)

    fun notifyItemChanged(position: Int)

    fun findItemPositionByKey(key: K): Int

    fun onBindCommonItem(holder: H, key: K, item: V)

    fun onBindPendingItem(holder: H, key: K, item: V) {}

    fun onItemDeleted(item: V) {}

    fun leftSwiped(holder: H)

    fun rightSwiped(holder: H)

    fun removeItem(key: K)
}