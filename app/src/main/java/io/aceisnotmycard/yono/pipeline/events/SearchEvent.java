package io.aceisnotmycard.yono.pipeline.events;

import io.aceisnotmycard.yono.pipeline.AbstactEvent;

/**
 * Created by sergey on 28/10/15.
 */
public class SearchEvent extends AbstactEvent {

    private String data;

    public SearchEvent(String data) {
        this.data = data;
    }

    @Override
    public String getData() {
        return data;
    }
}
