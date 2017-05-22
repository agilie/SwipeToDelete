package com.example.swipetodeletelib

import android.support.v7.widget.RecyclerView
import android.view.View


/**
 * Created by AlexZandR on 4/24/17.
 */

interface SwipeToDeleteHolder<K> {

    var listener: UndoClickListener<K>

    var isPendingDelete: Boolean?
    get() = isPendingDelete ?: false
    set(value) {isPendingDelete = value}

    val topContainer: View


//    var deletedName = view.user_name_deleted
//    var name = view.user_name
//    var phone = view.user_phone_number
//    var progressIndicator = view.progress_indicator
//    var itemContainer = view.contact_container
//    var undoContainer = view.undo_container
//    var undoData = view.undo_data

//    init {
//        view.button_undo.setOnClickListener { listener.onUndoClick(adapterPosition) }
//    }

//    val topContainer: View
//        get() =
//        if (isPendingDelete) undoContainer
//        else itemContainer


}