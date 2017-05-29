package test.alexzander.swipetodelete.sample;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.swipetodeletelib.SwipeConstants;
import com.example.swipetodeletelib.SwipeToDeleteAdapter;
import com.example.swipetodeletelib.interfaces.ISwipeToDeleteAdapter;
import com.example.swipetodeletelib.interfaces.ISwipeToDeleteHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import test.alexzander.swipetodelete.R;


public class JavaAdapter extends RecyclerView.Adapter implements ISwipeToDeleteAdapter<String, User, JavaAdapter.Holder> {

    private List<User> users;
    private SwipeToDeleteAdapter swipeToDeleteAdapter;

    public JavaAdapter(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public SwipeToDeleteAdapter getSwipeToDeleteAdapter() {
        return swipeToDeleteAdapter;
    }

    public void setSwipeToDeleteAdapter(SwipeToDeleteAdapter swipeToDeleteAdapter) {
        this.swipeToDeleteAdapter = swipeToDeleteAdapter;
        swipeToDeleteAdapter.setDeletingDuration(5000L);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ISwipeToDeleteHolder) {
            swipeToDeleteAdapter.onBindViewHolder((ISwipeToDeleteHolder) holder, users.get(position).getName(), position);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public int findItemPositionByKey(String key) {
        for (int i = 0; i < users.size(); ++i) {
            if (users.get(i).getName().equals(key)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBindCommonItem(Holder holder, final String key, User item) {
        holder.userContainer.setVisibility(View.VISIBLE);
        holder.undoContainer.setVisibility(View.GONE);
        holder.userName.setText(item.getName());
    }

    @Override
    public void onBindPendingItem(Holder holder, final String key, User item) {
        holder.undoContainer.setVisibility(View.VISIBLE);
        holder.userContainer.setVisibility(View.GONE);
        holder.userButtonUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeToDeleteAdapter.onUndo(key);
            }
        });
    }

    @Override
    public void leftSwiped(Holder holder) {

    }

    @Override
    public void rightSwiped(Holder holder) {

    }

    @Override
    public void removeItem(String key) {
        swipeToDeleteAdapter.removeItem(key);
    }

    @Override
    public void onItemDeleted(User item) {

    }

    public class Holder extends RecyclerView.ViewHolder implements ISwipeToDeleteHolder<String> {

        Button userButtonUndo;
        FrameLayout userContainer;

        AppCompatTextView userName;
        FrameLayout undoContainer;
        String key;

        Holder(View view) {
            super(view);
            userName = (AppCompatTextView) view.findViewById(R.id.user_name);
            userContainer = (FrameLayout) view.findViewById(R.id.user_container);

            userButtonUndo = (Button) view.findViewById(R.id.user_button_undo);
            undoContainer = (FrameLayout) view.findViewById(R.id.user_undo_container);
        }

        int direction = SwipeConstants.NO_SWIPE;

        public void setDirection(int direction) {
            this.direction = direction;
        }

        @Override
        public int getDirection() {
            return direction;
        }

        @NotNull
        @Override
        public View getTopContainer() {
            if (direction == SwipeConstants.LEFT || direction == SwipeConstants.RIGHT) {
                return undoContainer;
            } else {
                return userContainer;
            }
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public void setKey(String s) {
            key = s;
        }
    }
}