package io.aceisnotmycard.yono.pipeline.events;

import io.aceisnotmycard.yono.pipeline.AbstactEvent;

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
