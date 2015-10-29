package io.aceisnotmycard.doit.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;

import io.aceisnotmycard.doit.db.TaskDao;
import io.aceisnotmycard.doit.model.Task;
import io.aceisnotmycard.doit.pipeline.Pipe;
import io.aceisnotmycard.doit.pipeline.events.SearchEvent;
import io.aceisnotmycard.doit.pipeline.events.TaskRemovedEvent;
import io.aceisnotmycard.doit.pipeline.events.TaskUpdatedEvent;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TasksListViewModel extends BaseViewModel {

    private ObservableArrayList<Task> items;
    private Context context;
    private String searchTerm;

    public TasksListViewModel(Context context) {
        super();
        this.context = context;
        items = new ObservableArrayList<>();

        if (searchTerm == null) {
            getTasks("");
        }

        addSubscription(Pipe.recvEvent(TaskRemovedEvent.class, AndroidSchedulers.mainThread(), Schedulers.io(),
                taskRemovedEvent -> TaskDao.getDao(context).delete(taskRemovedEvent.getData())));

        addSubscription(Pipe.recvEvent(TaskUpdatedEvent.class, AndroidSchedulers.mainThread(), Schedulers.io(),
                taskUpdatedEvent -> TaskDao.getDao(context).update(taskUpdatedEvent.getData())));

        addSubscription(Pipe.recvEvent(SearchEvent.class, AndroidSchedulers.mainThread(), Schedulers.io(),
                searchEvent -> getTasks(searchEvent.getData())));
    }

    private Subscription getTasks(String term) {
        return TaskDao.getDao(context).searchFor(term)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(tasks1 -> searchTerm = term)
                .subscribe(tasks -> {
                    items.clear();
                    items.addAll(tasks);
                });
    }

    public ObservableArrayList<Task> getItems() {
        return items;
    }

    @Override
    public void onPause() {
        super.onPause();
        context = null;
    }
}

