package io.aceisnotmycard.yono.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding.view.RxView;

import io.aceisnotmycard.yono.R;
import io.aceisnotmycard.yono.adapter.TasksAdapter;
import io.aceisnotmycard.yono.adapter.TasksAdapterTouchCallback;
import io.aceisnotmycard.yono.databinding.FragmentTasksListBinding;
import io.aceisnotmycard.yono.pipeline.Pipe;
import io.aceisnotmycard.yono.pipeline.events.NewTaskEvent;
import io.aceisnotmycard.yono.pipeline.events.SearchEvent;
import io.aceisnotmycard.yono.pipeline.events.TaskRemovedEvent;
import io.aceisnotmycard.yono.pipeline.events.TaskRestoredEvent;
import io.aceisnotmycard.yono.viewmodel.TasksListViewModel;

public class TasksListFragment extends BaseFragment {

    public static final String TAG = TasksListFragment.class.getName();

    private FragmentTasksListBinding b;
    private TasksListViewModel viewModel;
    private TasksAdapter adapter;

    private SearchView searchView;

    public static TasksListFragment newInstance() { return new TasksListFragment(); }

    public TasksListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentTasksListBinding.inflate(inflater);
        viewModel = new TasksListViewModel(getActivity());
        b.setViewModel(viewModel);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAdapter();
        b.tasksListToolbar.inflateMenu(R.menu.menu_tasks_list);
        b.tasksListToolbar.setTitle(R.string.app_name);
        MenuItem searchItem = b.tasksListToolbar.getMenu().findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onResume(getActivity());

        addSubscription(RxSearchView.queryTextChanges(searchView)
                .map(CharSequence::toString)
                .subscribe(text -> Pipe.sendEvent(new SearchEvent(text))));

        addSubscription(RxView.clicks(b.fab).subscribe(o -> Pipe.sendEvent(new NewTaskEvent())));

        addSubscription(Pipe.recvEvent(TaskRemovedEvent.class, taskRemovedEvent ->
            Snackbar.make(b.getRoot(), R.string.task_removed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo_action, v -> {
                        Pipe.sendEvent(new TaskRestoredEvent(taskRemovedEvent.getAdapterPosition()));
                    })
                    .show()
        ));
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.onPause(getActivity());
    }

    private void setupAdapter() {
        adapter = new TasksAdapter(viewModel.getItems());
        b.tasksListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        b.tasksListView.setAdapter(adapter);
        TasksAdapterTouchCallback touchCallback = new TasksAdapterTouchCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(touchCallback);
        touchHelper.attachToRecyclerView(b.tasksListView);
    }
}
