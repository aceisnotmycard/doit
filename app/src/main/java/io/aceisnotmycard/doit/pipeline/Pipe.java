package io.aceisnotmycard.doit.pipeline;

import rx.Observable;
import rx.Observer;
import rx.subjects.PublishSubject;

/**
 * Created by sergey on 22/10/15.
 *
 */
public class Pipe {

    private static final PublishSubject<AbstactEvent> eventSubject = PublishSubject.create();

    private Pipe() {
        throw new UnsupportedOperationException();
    }

    public static Observer<AbstactEvent> getObserver() {
        return eventSubject;
    }

    public static Observable<AbstactEvent> getObservable() {
        return eventSubject;
    }
}
