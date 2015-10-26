package io.aceisnotmycard.doit.adapter;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.aceisnotmycard.doit.databinding.TaskItemBinding;
import io.aceisnotmycard.doit.model.Task;
import io.aceisnotmycard.doit.pipeline.Pipe;
import io.aceisnotmycard.doit.pipeline.events.TaskRemovedEvent;
import io.aceisnotmycard.doit.pipeline.events.TasksListClickEvent;
import io.aceisnotmycard.doit.viewmodel.ListItemViewModel;

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
                notifyItemRangeChanged(positionStart, itemCount);
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
        holder.getBinding().setViewModel(new ListItemViewModel(t));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onItemSwiped(RecyclerView.ViewHolder viewHolder) {
        final Task t = ((ViewHolder) viewHolder).getBinding().getViewModel().getTask();
        Pipe.getObserver().onNext(new TaskRemovedEvent(t));
        items.remove(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean onItemMoved(int fromPosition, int toPosition) {
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TaskItemBinding binding;

        public ViewHolder(TaskItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(v ->
                Pipe.getObserver().onNext(new TasksListClickEvent(binding.getViewModel().getTask()))
            );
        }

        public TaskItemBinding getBinding() {
            return binding;
        }
    }
}