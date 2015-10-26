package io.aceisnotmycard.doit.viewmodel;

import android.databinding.BaseObservable;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseViewModel extends BaseObservable {

    private CompositeSubscription subscriptions;

    BaseViewModel() {
        subscriptions = new CompositeSubscription();
    }

    void addSubscription(Subscription s) {
        subscriptions.add(s);
    }

    public void onPause() {
        subscriptions.unsubscribe();
    }
}
