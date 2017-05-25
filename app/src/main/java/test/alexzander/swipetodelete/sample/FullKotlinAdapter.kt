package test.alexzander.swipetodelete.sample

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.swipetodeletelib.ModelOptions
import com.example.swipetodeletelib.SwipeToDeleteAdapter
import com.example.swipetodeletelib.interfaces.IAnimationUpdateListener
import com.example.swipetodeletelib.interfaces.IAnimatorListener
import com.example.swipetodeletelib.interfaces.ISwipeToDeleteAdapter
import com.example.swipetodeletelib.interfaces.ISwipeToDeleteHolder
import kotlinx.android.synthetic.main.list_item.view.*
import test.alexzander.swipetodelete.MainActivityNavigation
import test.alexzander.swipetodelete.R.layout.list_item


class FullKotlinAdapter(val context: Context, val mutableList: MutableList<User>, val mainActivityNavigation: MainActivityNavigation) :
        RecyclerView.Adapter<FullKotlinAdapter.MyHolder>(), ISwipeToDeleteAdapter<Int, User, FullKotlinAdapter.MyHolder>, IAnimationUpdateListener, IAnimatorListener {

    val swipeToDeleteAdapter = SwipeToDeleteAdapter(context = context, items = mutableList, swipeToDeleteAdapter = this)

    init { swipeToDeleteAdapter.deletingDuration = 6000 }

    override fun getItemCount() = mutableList.size

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        swipeToDeleteAdapter.onBindViewHolder(holder, mutableList[position].id, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            MyHolder(LayoutInflater.from(parent?.context).inflate(list_item, parent, false))

    override fun removeItem(key: Int) {
        swipeToDeleteAdapter.removeItem(key)
    }

    override fun onAnimationEnd(animation: Animator?, options: ModelOptions<*>) {
        swipeToDeleteAdapter.holders[options.key]?.progressBar?.visibility = View.GONE
    }

    override fun onAnimationUpdate(animation: ValueAnimator?, options: ModelOptions<*>) {
        val posX = animation?.animatedValue as Float
        swipeToDeleteAdapter.holders[options.key]?.progressBar?.x = posX
        options.posX = posX
    }

    override fun onAnimationStart(animation: Animator?, options: ModelOptions<*>) {
        swipeToDeleteAdapter.holders[options.key]?.progressBar?.visibility = View.VISIBLE
    }

    override fun findItemPositionByKey(key: Int) = (0..mutableList.lastIndex).firstOrNull { mutableList[it].id == key } ?: -1

    override fun onBindCommonItem(holder: MyHolder, key: Int, item: User) {
        holder.name.text = item.name
        holder.id.text = item.id.toString()
        holder.itemContainer.visibility = View.VISIBLE
        holder.undoData.visibility = View.GONE
        holder.progressBar.visibility = View.GONE
        if (holder.key % 2 == 0) holder.itemContainer.setOnClickListener { mainActivityNavigation.navigateToBaseKotlinActivity() }
        else holder.itemContainer.setOnClickListener { mainActivityNavigation.navigateToJavaActivity() }
    }

    override fun onBindPendingItem(holder: MyHolder, key: Int, item: User) {
        holder.deletedName.text = "You have just deleted {$item.name}"
        holder.itemContainer.visibility = View.GONE
        holder.undoData.visibility = View.VISIBLE
        holder.progressBar.visibility = View.VISIBLE
        holder.undoButton.setOnClickListener { swipeToDeleteAdapter.onUndo(key) }
    }

    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view), ISwipeToDeleteHolder<Int> {

        var deletedName = view.user_name_deleted
        var name = view.user_name
        var id = view.user_id
        var itemContainer = view.contact_container
        var undoContainer = view.undo_container
        var undoData = view.undo_data
        var undoButton = view.button_undo
        var progressBar = view.progress_indicator

        override val topContainer: View
            get() =
            if (isPendingDelete) undoContainer
            else itemContainer
        override var key: Int = -1
        override var isPendingDelete: Boolean = false
    }
}