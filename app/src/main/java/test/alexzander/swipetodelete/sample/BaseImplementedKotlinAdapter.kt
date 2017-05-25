package test.alexzander.swipetodelete.sample

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.swipetodeletelib.SwipeConstants
import com.example.swipetodeletelib.SwipeToDeleteAdapter
import com.example.swipetodeletelib.interfaces.ISwipeToDeleteAdapter
import com.example.swipetodeletelib.interfaces.ISwipeToDeleteHolder
import kotlinx.android.synthetic.main.user_item.view.*
import test.alexzander.swipetodelete.R.layout.user_item

class BaseImplementedKotlinAdapter(context: Context, var mutableList: MutableList<User>) :
        RecyclerView.Adapter<BaseImplementedKotlinAdapter.Holder>(), ISwipeToDeleteAdapter<String, User, BaseImplementedKotlinAdapter.Holder> {

    val swipeToDeleteAdapter = SwipeToDeleteAdapter(context = context, items = mutableList, swipeToDeleteAdapter = this)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            Holder(LayoutInflater.from(parent?.context).inflate(user_item, parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        swipeToDeleteAdapter.onBindViewHolder(holder, mutableList[position].name, position)
    }

    override fun getItemCount() = mutableList.size

    override fun findItemPositionByKey(key: String) = (0..mutableList.lastIndex).firstOrNull { mutableList[it].name == key } ?: -1

    override fun onBindCommonItem(holder: Holder, key: String, item: User) {
        holder.userContainer.visibility = View.VISIBLE
        holder.userName.text = item.name
    }

    override fun removeItem(key: String) {
        swipeToDeleteAdapter.removeItem(key)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view), ISwipeToDeleteHolder<String> {

        var userContainer = view.user_container
        var userName = view.user_name

        override var direction: Int = SwipeConstants.NO_SWIPE

        override val topContainer = userContainer!!

        override var key: String = ""
    }
}