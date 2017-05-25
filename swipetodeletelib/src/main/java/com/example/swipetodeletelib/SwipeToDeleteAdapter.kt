package com.example.swipetodeletelib

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.swipetodeletelib.interfaces.*
import java.lang.IndexOutOfBoundsException

class SwipeToDeleteAdapter<K, in V, H : ISwipeToDeleteHolder<K>>(private val items: MutableList<V>,
                                                                 val context: Context, val swipeToDeleteAdapter: ISwipeToDeleteAdapter<K, V, H>) : ItemSwipeListener<K>, IUndoClickListener<K> {
    var deletingDuration: Long? = null
    val itemTouchCallBack = ContactItemTouchCallback(this)
    val handler = Handler(Looper.getMainLooper())
    val pendingRemoveActions = HashMap<K, Runnable?>(1)
    val animatorsMap = HashMap<K, ValueAnimator>(1)
    val modelOptions = HashMap<K, ModelOptions<K>>()
    val holders = HashMap<K, H>()
    var animationUpdateListener: IAnimationUpdateListener? = null
    var animatorListener: IAnimatorListener? = null

    init {
        if (swipeToDeleteAdapter is IAnimatorListener) animatorListener = swipeToDeleteAdapter
        if (swipeToDeleteAdapter is IAnimationUpdateListener) animationUpdateListener = swipeToDeleteAdapter
    }

    fun onBindViewHolder(holder: H, key: K, position: Int) {
        try {
            holder.key = key
            if (!modelOptions.containsKey(key)) modelOptions.put(key, ModelOptions(key, deletingDuration ?: 0))
            val item = items[position]
            if (item == null) {
                items.removeAt(position)
                swipeToDeleteAdapter.notifyItemRemoved(position)
            } else {
                holders[key] = holder
                holder.direction = modelOptions[key]!!.direction ?: SwipeConstants.NO_SWIPE

                if (modelOptions[key]!!.isPendingDelete) onBindPendingContact(holder, key, item, animatorListener, animationUpdateListener)
                else onBindCommonContact(holder, key, item)
            }
        } catch (exc: IndexOutOfBoundsException) {
            exc.printStackTrace()
        }
    }

    override fun onItemSwiped(viewHolder: ISwipeToDeleteHolder<K>, swipeDir: Int) {
        val key = viewHolder.key
        if (modelOptions[key]?.isPendingDelete ?: false) removeItemByKey(key)
        else {
            modelOptions[key]?.isPendingDelete = true
            modelOptions[key]?.setDirection(swipeDir)
            swipeToDeleteAdapter.notifyItemChanged(swipeToDeleteAdapter.findItemPositionByKey(key))
        }
    }

    override fun onUndo(key: K) {
        val position = swipeToDeleteAdapter.findItemPositionByKey(key)
        handler.removeCallbacks(pendingRemoveActions[key])
        modelOptions[key]?.isPendingDelete = false
        swipeToDeleteAdapter.notifyItemChanged(position)
        SwipeToDeleteAdapterUtils.clearAnimator(animatorsMap[key])
    }

    fun onBindCommonContact(holder: H, key: K, item: V) {
        swipeToDeleteAdapter.onBindCommonItem(holder, key, item)
    }

    fun onBindPendingContact(holder: H, key: K, item: V, IAnimatorListener: IAnimatorListener? = null, IAnimationUpdateListener: IAnimationUpdateListener? = null) {
        swipeToDeleteAdapter.onBindPendingItem(holder, key, item)
        pendingRemoveActions[key] ?: pendingRemoveActions.put(key, Runnable { removeItemByKey(key) })
        handler.postDelayed(pendingRemoveActions[key], modelOptions[key]!!.pendingDuration)
        val animator: ValueAnimator?
        if (animatorsMap[key] != null) {
            animator = animatorsMap[key]
            SwipeToDeleteAdapterUtils.initAnimator(modelOptions[key]!!, context, IAnimatorListener, IAnimationUpdateListener, animator)
        } else {
            animator = SwipeToDeleteAdapterUtils.initAnimator(modelOptions[key]!!, context, IAnimatorListener, IAnimationUpdateListener)
            animatorsMap.put(key, animator)
        }
        animator?.start()
    }

    fun removeItemByKey(key: K) {
        swipeToDeleteAdapter.removeItem(key)
    }

    fun removeItem(key: K){
        val position = swipeToDeleteAdapter.findItemPositionByKey(key)
        removeItemFromList(key, items.removeAt(position), position)
    }

    fun removeItemFromList(key: K, item: V, position: Int) {
        handler.removeCallbacks(pendingRemoveActions.remove(key))
        pendingRemoveActions.remove(key)
        items.remove(item)
        holders.remove(key)
        modelOptions.remove(key)
        swipeToDeleteAdapter.notifyItemRemoved(position)
        SwipeToDeleteAdapterUtils.clearOptions(modelOptions[key])
        SwipeToDeleteAdapterUtils.clearAnimator(animatorsMap.remove(key))
        swipeToDeleteAdapter.onItemDeleted(item)
    }

    fun clearAnimation(key: K, view: View?) {
        SwipeToDeleteAdapterUtils.clearAnimator(animatorsMap[key])
        SwipeToDeleteAdapterUtils.clearOptions(modelOptions[key])
        SwipeToDeleteAdapterUtils.clearView(view)
    }
}