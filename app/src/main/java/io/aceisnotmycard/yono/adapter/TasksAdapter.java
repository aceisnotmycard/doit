package io.aceisnotmycard.yono.adapter;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collections;

import io.aceisnotmycard.yono.databinding.TaskItemBinding;
import io.aceisnotmycard.yono.model.Task;
import io.aceisnotmycard.yono.pipeline.Pipe;
import io.aceisnotmycard.yono.pipeline.events.TaskRemovedEvent;
import io.aceisnotmycard.yono.pipeline.events.TaskUpdatedEvent;
import io.aceisnotmycard.yono.pipeline.events.TasksListClickEvent;
import io.aceisnotmycard.yono.viewmodel.ListItemViewModel;

/**
 * Created by sergey on 19/10/15.
 * Observes items and reacts to changes.
 */
public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> implements TasksAdapterTouchCallback.DraggableAdapter {

    private static final String TAG = TasksAdapter.class.getSimpleName();

    ObservableArrayList<Task> items;

    public TasksAdapter(ObservableArrayList<Task> items) {
        this.items = items;
        items.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<Task>>() {
            @Override
            public void onChanged(ObservableList<Task> sender) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<Task> sender, int positionStart, int itemCount) {
                //notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(ObservableList<Task> sender, int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(ObservableList<Task> sender, int fromPosition, int toPosition, int itemCount) {
                // Not sure that it is right solution
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onItemRangeRemoved(ObservableList<Task> sender, int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(TaskItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Task t = items.get(position);
        holder.setTask(t);
        holder.getBinding().setViewModel(new ListItemViewModel(t));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onItemSwiped(RecyclerView.ViewHolder viewHolder) {
        Pipe.sendEvent(new TaskRemovedEvent(items.get(viewHolder.getAdapterPosition()),
                viewHolder.getAdapterPosition()));
        items.remove(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean onItemMoved(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
        int tmp = items.get(fromPosition).getPosition();
        items.get(fromPosition).setPosition(items.get(toPosition).getPosition());
        items.get(toPosition).setPosition(tmp);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TaskItemBinding b;

        private Task task;

        public ViewHolder(TaskItemBinding b) {
            super(b.getRoot());
            this.b = b;
            itemView.setOnClickListener(v -> Pipe.sendEvent(new TasksListClickEvent(getTask())));
        }

        public TaskItemBinding getBinding() {
            return b;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }
    }
}
