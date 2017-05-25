package com.example.swipetodeletelib.interfaces

import android.view.View

interface ISwipeToDeleteHolder<K> {
    val topContainer: View
    var direction: Int
    var key: K
}