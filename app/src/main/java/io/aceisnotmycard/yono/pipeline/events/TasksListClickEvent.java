package io.aceisnotmycard.yono.pipeline.events;

import io.aceisnotmycard.yono.model.Task;
import io.aceisnotmycard.yono.pipeline.AbstactEvent;

/**
 * Created by sergey on 22/10/15.
 */
public class TasksListClickEvent extends AbstactEvent {

    private Task task;

    public TasksListClickEvent(Task task) {
        this.task = task;
    }

    @Override
    public Task getData() {
        return task;
    }
}
