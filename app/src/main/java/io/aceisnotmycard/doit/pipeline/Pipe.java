package io.aceisnotmycard.doit.pipeline;

import android.util.Log;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class Pipe {

    public static final String TAG = Pipe.class.getName();

    private static final PublishSubject<AbstactEvent> eventSubject = PublishSubject.create();

    private Pipe() {
        throw new UnsupportedOperationException();
    }

    public static void sendEvent(AbstactEvent event) {
        eventSubject.onNext(event);
    }

    public static <T extends AbstactEvent> Subscription recvEvent(Class<T> eventType, Action1<T> func) {
        return eventSubject
                .filter(eventType::isInstance)
                .map(eventType::cast)
                .subscribe(func);
    }

    public static <T extends AbstactEvent> Subscription recvEvent(
            Class<T> eventType, Scheduler subsribeOn, Scheduler observeOn, Action1<T> func) {
        return eventSubject
                .filter(eventType::isInstance)
                .map(eventType::cast)
                .subscribeOn(subsribeOn)
                .observeOn(observeOn)
                .subscribe(func);
    }
}
