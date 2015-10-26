package io.aceisnotmycard.doit.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import io.aceisnotmycard.doit.db.TaskDao;
import io.aceisnotmycard.doit.model.Task;
import io.aceisnotmycard.doit.pipeline.Pipe;
import io.aceisnotmycard.doit.pipeline.events.TaskUpdatedEvent;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by sergey on 22/10/15.
 *
 */
public class EditTaskViewModel extends BaseObservable {

    public static final String TAG = EditTaskViewModel.class.getSimpleName();

    private Task task;
    private Context context;
    private boolean isNew;

    private CompositeSubscription subscriptions;

    public EditTaskViewModel(Task task, Context context) {
        this.task = task;
        this.context = context;
        isNew = false;
        init();
    }

    public EditTaskViewModel(Context context) {
        task = new Task();
        this.context = context;
        isNew = true;
        init();
    }

    private void createOrUpdate(Task updater) {
        if (isNew) {
            isNew = false;
            int id = TaskDao.getDao(context).insert(updater.getTitle(), updater.getText());
            Log.i(TAG, String.valueOf(id));
            task.setText(updater.getText());
            task.setTitle(updater.getTitle());
            task.setPosition(id);
        } else {
            task.setText(updater.getText());
            task.setTitle(updater.getTitle());
            if (!TaskDao.getDao(context).update(task)) {
                Log.d(TAG, "Task is not updated for some reason");
            }
        }
    }

    @Bindable
    public String getTitle() {
        return task.getTitle();
    }

    @Bindable
    public String getText() {
        return task.getText();
    }

    private void init() {
        subscriptions = new CompositeSubscription();
        subscriptions.add(Pipe.getObservable()
                .filter(abstactEvent -> abstactEvent instanceof TaskUpdatedEvent)
                .map(abstactEvent1 -> (TaskUpdatedEvent) abstactEvent1)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(taskUpdatedEvent -> {
                    createOrUpdate(taskUpdatedEvent.getData());
                }));
    }

    public void onPause() {
        subscriptions.unsubscribe();
    }
}
