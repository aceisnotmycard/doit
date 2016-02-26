package io.aceisnotmycard.yono.viewmodel;

import android.content.Context;
import android.databinding.Bindable;
import android.util.Log;

import io.aceisnotmycard.yono.db.TaskDao;
import io.aceisnotmycard.yono.model.Task;
import io.aceisnotmycard.yono.pipeline.Pipe;
import io.aceisnotmycard.yono.pipeline.events.TaskUpdatedEvent;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sergey on 22/10/15.
 *
 */
public class EditTaskViewModel extends BaseViewModel {

    public static final String TAG = EditTaskViewModel.class.getSimpleName();

    private Task task;
    private boolean isNew;

    public EditTaskViewModel(Task task) {
        if (task != null) {
            this.task = task;
            isNew = false;
        } else {
            this.task = new Task();
            isNew = true;
        }
    }

    public void onResume(Context ctx) {
        super.onResume();
        addSubscription(Pipe.recvEvent(TaskUpdatedEvent.class, AndroidSchedulers.mainThread(), Schedulers.io(),
                taskUpdatedEvent -> createOrUpdate(taskUpdatedEvent.getData(), ctx)));
    }

    private void createOrUpdate(Task updater, Context ctx) {
        task.setImportant(updater.isImportant());
        task.setText(updater.getText());
        if (!task.getText().isEmpty()) {
            if (isNew) {
                isNew = false;
                int id = TaskDao.getDao(ctx).insert(updater);
                task.setPosition(id);
            } else {
                if (!TaskDao.getDao(ctx).update(task)) {
                    Log.e(TAG, "Task is not updated for some reason");
                }
            }
        }
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
    }
}
