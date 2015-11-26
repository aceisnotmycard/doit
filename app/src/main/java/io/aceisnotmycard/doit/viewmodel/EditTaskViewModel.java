package io.aceisnotmycard.doit.viewmodel;

import android.content.Context;
import android.databinding.Bindable;
import android.util.Log;

import io.aceisnotmycard.doit.db.TaskDao;
import io.aceisnotmycard.doit.model.Task;
import io.aceisnotmycard.doit.pipeline.Pipe;
import io.aceisnotmycard.doit.pipeline.events.TaskUpdatedEvent;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        if (task != null) {
            this.task = task;
            isNew = false;
        } else {
            this.task = new Task();
            isNew = true;
        }
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        addSubscription(Pipe.recvEvent(TaskUpdatedEvent.class, AndroidSchedulers.mainThread(), Schedulers.io(),
                taskUpdatedEvent -> createOrUpdate(taskUpdatedEvent.getData())));
    }

    private void createOrUpdate(Task updater) {
        task.setImportant(updater.isImportant());
        task.setText(updater.getText());
        task.setTitle(updater.getTitle());
        if (!task.getTitle().isEmpty()) {
            if (isNew) {
                isNew = false;
                int id = TaskDao.getDao(context).insert(updater);
                task.setPosition(id);
            } else {
                if (!TaskDao.getDao(context).update(task)) {
                    Log.e(TAG, "Task is not updated for some reason");
                }
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

    @Bindable
    public Boolean getImportant() {
        return task.isImportant();
    }

    public void onPause() {
        super.onPause();
        context = null;
    }
}
