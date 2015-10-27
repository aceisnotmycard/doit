package io.aceisnotmycard.doit.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import io.aceisnotmycard.doit.model.Task;

/**
 * Created by sergey on 19/10/15.
 */
public class ListItemViewModel extends BaseObservable {
    private Task task;

    public ListItemViewModel(Task task) {
        this.task = task;
    }

    @Bindable
    public String getTitle() {
        return task.getTitle();
    }

    @Bindable
    public boolean getImportant() {
        return task.isImportant();
    }

    public Task getTask() {
        return task;
    }

}
