package io.aceisnotmycard.doit.pipeline.events;

import io.aceisnotmycard.doit.model.Task;
import io.aceisnotmycard.doit.pipeline.AbstactEvent;

/**
 * Created by sergey on 06/11/15.
 */
public class TaskRestoredEvent extends AbstactEvent {
    private int position;

    public TaskRestoredEvent(int position) {
        this.position = position;
    }

    @Override
    public Integer getData() {
        return position;
    }
}
