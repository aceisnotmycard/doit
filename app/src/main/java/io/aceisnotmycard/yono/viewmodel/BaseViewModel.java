package io.aceisnotmycard.yono.viewmodel;

import android.databinding.BaseObservable;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseViewModel extends BaseObservable {

    private CompositeSubscription subscriptions;

    BaseViewModel() {}

    void addSubscription(Subscription s) {
        subscriptions.add(s);
    }

    public void onResume() {
        subscriptions = new CompositeSubscription();
    }

    public void onPause() {
        subscriptions.unsubscribe();
    }
}
