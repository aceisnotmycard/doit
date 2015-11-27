package io.aceisnotmycard.yono.pipeline.events;

import io.aceisnotmycard.yono.model.Task;
import io.aceisnotmycard.yono.pipeline.AbstactEvent;

/**
 * Created by sergey on 22/10/15.
 */
public class TaskRemovedEvent extends AbstactEvent {

    private Task task;
    private int adapterPosition;

    public TaskRemovedEvent(Task task, int adapterPosition) {
        this.task = task;
        this.adapterPosition = adapterPosition;
    }

    @Override
    public Task getData() {
        return task;
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }
}
