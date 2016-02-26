package io.aceisnotmycard.yono.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.util.Log;

import io.aceisnotmycard.yono.db.TaskDao;
import io.aceisnotmycard.yono.model.Task;
import io.aceisnotmycard.yono.pipeline.Pipe;
import io.aceisnotmycard.yono.pipeline.events.SearchEvent;
import io.aceisnotmycard.yono.pipeline.events.TaskRemovedEvent;
import io.aceisnotmycard.yono.pipeline.events.TaskRestoredEvent;
import io.aceisnotmycard.yono.pipeline.events.TaskUpdatedEvent;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TasksListViewModel extends BaseViewModel {

    public static final String TAG = TasksListViewModel.class.getName();

    private ObservableArrayList<Task> items;
    private String searchTerm;
    private Task lastRemovedItem;

    public TasksListViewModel(Context ctx) {
        super();
        items = new ObservableArrayList<>();

        if (searchTerm == null) {
            getTasks("", ctx);
        }
    }

    public void onResume(Context ctx) {
        super.onResume();
        addSubscription(Pipe.recvEvent(TaskRemovedEvent.class, AndroidSchedulers.mainThread(), Schedulers.io(),
                taskRemovedEvent -> {
                    lastRemovedItem = taskRemovedEvent.getData();
                    TaskDao.getDao(ctx).delete(lastRemovedItem);
                }));
        addSubscription(Pipe.recvEvent(TaskRestoredEvent.class, taskRestoredEvent -> {
            if (lastRemovedItem != null) {
                TaskDao.getDao(ctx).insert(lastRemovedItem.getPosition(), lastRemovedItem);
                items.add(taskRestoredEvent.getData(), lastRemovedItem);
            } else {
                Log.e(TAG, "Tried to restore null item");
            }
        }));

        addSubscription(Pipe.recvEvent(SearchEvent.class, AndroidSchedulers.mainThread(), Schedulers.io(),
                searchEvent -> getTasks(searchEvent.getData(), ctx)));
    }

    public ObservableArrayList<Task> getItems() {
        return items;
    }

    public void onPause(Context ctx) {
        super.onPause();
        for (Task task : items) {
            if (task.isChanged()) {
                TaskDao.getDao(ctx).update(task);
            }
        }
    }

    private Subscription getTasks(String term, Context context) {
        return TaskDao.getDao(context).searchFor(term)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(tasks1 -> searchTerm = term)
                .subscribe(tasks -> {
                    items.clear();
                    items.addAll(tasks);
                });
    }
}

