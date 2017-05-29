package test.alexzander.swipetodelete.sample

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.swipetodeletelib.ModelOptions
import com.example.swipetodeletelib.SwipeConstants
import com.example.swipetodeletelib.SwipeToDeleteAdapter
import com.example.swipetodeletelib.interfaces.IAnimationUpdateListener
import com.example.swipetodeletelib.interfaces.IAnimatorListener
import com.example.swipetodeletelib.interfaces.ISwipeToDeleteAdapter
import com.example.swipetodeletelib.interfaces.ISwipeToDeleteHolder
import kotlinx.android.synthetic.main.both_swipe_item.view.*
import test.alexzander.swipetodelete.MainActivityNavigation
import test.alexzander.swipetodelete.R.layout.both_swipe_item


class FullKotlinAdapter(val context: Context, val mutableList: MutableList<User>, val mainActivityNavigation: MainActivityNavigation) :
        RecyclerView.Adapter<FullKotlinAdapter.MyHolder>(), ISwipeToDeleteAdapter<Int, User, FullKotlinAdapter.MyHolder>, IAnimationUpdateListener, IAnimatorListener {

    val swipeToDeleteAdapter = SwipeToDeleteAdapter(context = context, items = mutableList, swipeToDeleteAdapter = this)

    init {
        swipeToDeleteAdapter.deletingDuration = 6000
    }

    override fun getItemCount() = mutableList.size

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        swipeToDeleteAdapter.onBindViewHolder(holder, mutableList[position].id, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            MyHolder(LayoutInflater.from(parent?.context).inflate(both_swipe_item, parent, false))

    override fun removeItem(key: Int) {
        swipeToDeleteAdapter.removeItem(key)
    }

    override fun onAnimationEnd(animation: Animator?, options: ModelOptions<*>) {
        swipeToDeleteAdapter.holders[options.key]?.undoProgressBar?.visibility = View.GONE
    }

    override fun onAnimationUpdate(animation: ValueAnimator?, options: ModelOptions<*>) {
        val posX = animation?.animatedValue as Float
        swipeToDeleteAdapter.holders[options.key]?.undoProgressBar?.x = posX
        options.posX = posX
    }

    override fun onAnimationStart(animation: Animator?, options: ModelOptions<*>) {
        swipeToDeleteAdapter.holders[options.key]?.undoProgressBar?.visibility = View.VISIBLE
    }

    override fun findItemPositionByKey(key: Int) = (0..mutableList.lastIndex).firstOrNull { mutableList[it].id == key } ?: -1

    override fun onBindCommonItem(holder: MyHolder, key: Int, item: User) {
        holder.itemContainer.visibility = View.VISIBLE
        holder.itemText.text = "Common text $key"
        if (holder.key % 2 == 0) holder.itemContainer.setOnClickListener { mainActivityNavigation.navigateToBaseKotlinActivity() }
        else holder.itemContainer.setOnClickListener { mainActivityNavigation.navigateToJavaActivity() }
    }

    override fun leftSwiped(holder: MyHolder) {
        Log.d("swipeListener", "leftSwiped")
    }

    override fun rightSwiped(holder: MyHolder) {
        Log.d("swipeListener", "rightSwiped")
    }

    override fun onBindPendingItem(holder: MyHolder, key: Int, item: User) {
        if (holder.direction == SwipeConstants.LEFT) {
            holder.itemContainer.visibility = View.GONE
            holder.undoContainer.visibility = View.VISIBLE
            holder.undoProgressBar.visibility = View.VISIBLE
            holder.sendContainer.visibility = View.GONE
            holder.undoText.text = "Undo Text $key"
            holder.undoButton.setOnClickListener { swipeToDeleteAdapter.onUndo(key) }
//            Log.d("testLog", "LEFT \n undo container ${holder.undoContainer.id}  ${holder.undoContainer.visibility}")
//            Log.d("testLog", "item container ${holder.itemContainer.id}  ${holder.itemContainer.visibility}")
//            Log.d("testLog", "send container ${holder.sendContainer.id}  ${holder.sendContainer.visibility}")
        } else if (holder.direction == SwipeConstants.RIGHT) {
            holder.itemContainer.visibility = View.GONE
            holder.undoContainer.visibility = View.GONE
            holder.sendContainer.visibility = View.VISIBLE
            holder.sendText.text = "SendText $key"
            holder.sendButton.setOnClickListener { swipeToDeleteAdapter.onUndo(key) }
//            Log.d("testLog", "RIGHT \n undo container ${holder.undoContainer.id}  ${holder.undoContainer.visibility}")
//            Log.d("testLog", "item container ${holder.itemContainer.id}  ${holder.itemContainer.visibility}")
//            Log.d("testLog", "send container ${holder.sendContainer.id}  ${holder.sendContainer.visibility}")
        }
    }

    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view), ISwipeToDeleteHolder<Int> {

        var itemContainer = view.user_container1
        var itemText = view.user_name1
        var itemId = view.user_id1

        var undoContainer = view.undo_container1
        var undoProgressBar = view.progress_indicator1
        var undoButton = view.button_undo1
        var undoText = view.user_name_deleted1

        var sendContainer = view.send_container1
        var sendButton = view.send_button1
        var sendText = view.send_name1


        override var key: Int = -1
        override var direction: Int = SwipeConstants.NO_SWIPE

        override val topContainer: View
            get() =
            when (direction) {
                SwipeConstants.LEFT -> undoContainer
                SwipeConstants.RIGHT -> sendContainer
                else -> itemContainer
            }

    }
}