package io.aceisnotmycard.doit.pipeline.events;

import io.aceisnotmycard.doit.model.Task;
import io.aceisnotmycard.doit.pipeline.AbstactEvent;

/**
 * Created by sergey on 06/11/15.
 */
public class TaskRestoredEvent extends AbstactEvent {
    private Task task;

    public TaskRestoredEvent(Task task) {
        this.task = task;
    }

    @Override
    public Task getData() {
        return task;
    }
}
