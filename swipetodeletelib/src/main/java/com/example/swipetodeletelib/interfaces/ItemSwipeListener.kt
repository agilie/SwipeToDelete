package com.example.swipetodeletelib.interfaces

interface ItemSwipeListener<H> {
    fun onItemSwiped(viewHolder: H, swipeDir: Int)
}
