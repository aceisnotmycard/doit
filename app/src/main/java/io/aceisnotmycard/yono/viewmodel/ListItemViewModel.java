package io.aceisnotmycard.yono.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import io.aceisnotmycard.yono.model.Task;


public class ListItemViewModel extends BaseObservable {
    private Task task;

    public ListItemViewModel(Task task) {
        this.task = task;
    }

    @Bindable
    public String getText() {
        return task.getText();
    }

    @Bindable
    public boolean getImportant() {
        return task.isImportant();
    }

    public Task getTask() {
        return task;
    }
}
