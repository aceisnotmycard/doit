package io.aceisnotmycard.doit.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.concurrent.TimeUnit;

import io.aceisnotmycard.doit.R;
import io.aceisnotmycard.doit.adapter.TasksAdapter;
import io.aceisnotmycard.doit.adapter.TasksAdapterTouchCallback;
import io.aceisnotmycard.doit.databinding.TasksListFragmentBinding;
import io.aceisnotmycard.doit.pipeline.Pipe;
import io.aceisnotmycard.doit.pipeline.events.NewTaskEvent;
import io.aceisnotmycard.doit.pipeline.events.SearchEvent;
import io.aceisnotmycard.doit.viewmodel.TasksListViewModel;

public class TasksListFragment extends BaseFragment {

    private TasksListFragmentBinding b;
    private TasksListViewModel viewModel;
    private TasksAdapter adapter;

    public static TasksListFragment newInstance() {
        return new TasksListFragment();
    }

    public TasksListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = TasksListFragmentBinding.inflate(inflater);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new TasksListViewModel(getActivity());
        adapter = new TasksAdapter(viewModel.getItems());
        b.setViewModel(viewModel);
        b.setHandlers(new Handlers());
        b.tasksListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        b.tasksListView.setAdapter(adapter);

        TasksAdapterTouchCallback touchCallback = new TasksAdapterTouchCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(touchCallback);
        touchHelper.attachToRecyclerView(b.tasksListView);
    }

    @Override
    public void onResume() {
        super.onResume();
        addSubscription(RxTextView.textChanges(b.tasksListSearch)
                .map(CharSequence::toString)
                .debounce(100L, TimeUnit.MICROSECONDS)
                .subscribe(text -> Pipe.sendEvent(new SearchEvent(text))));
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.onPause();
    }

    public class Handlers {
        public void onTaskAdd(View v) {
            Pipe.sendEvent(new NewTaskEvent());
        }
    }
}
