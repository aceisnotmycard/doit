package io.aceisnotmycard.doit.pipeline.events;

import io.aceisnotmycard.doit.model.Task;
import io.aceisnotmycard.doit.pipeline.AbstactEvent;

/**
 * Created by sergey on 22/10/15.
 */
public class TaskUpdatedEvent extends AbstactEvent {

    Task data;

    public TaskUpdatedEvent(String title, String text) {
        data = new Task(title, text);
    }

    @Override
    public Task getData() {
        return data;
    }
}
