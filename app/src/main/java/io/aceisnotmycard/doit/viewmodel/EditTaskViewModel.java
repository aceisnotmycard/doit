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
public class EditTaskViewModel extends BaseViewModel {

    public static final String TAG = EditTaskViewModel.class.getSimpleName();

    private Task task;
    private Context context;
    private boolean isNew;

    public EditTaskViewModel(Task task, Context context) {
        super();
        if (task != null) {
            this.task = task;
            isNew = false;
        } else {
            this.task = new Task();
            isNew = true;
        }

        this.context = context;
        addSubscription(Pipe.getObservable()
                .filter(abstactEvent -> abstactEvent instanceof TaskUpdatedEvent)
                .map(abstactEvent1 -> (TaskUpdatedEvent) abstactEvent1)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(taskUpdatedEvent -> {
                    createOrUpdate(taskUpdatedEvent.getData());
                }));
    }

    private void createOrUpdate(Task updater) {
        if (isNew) {
            isNew = false;
            int id = TaskDao.getDao(context).insert(updater.getTitle(), updater.getText());
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

    public void onPause() {
        super.onPause();
        context = null;
    }
}
