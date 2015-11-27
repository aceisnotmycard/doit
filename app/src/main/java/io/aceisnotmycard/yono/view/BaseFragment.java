package io.aceisnotmycard.yono.view;

import android.app.Fragment;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by sergey on 26/10/15.
 *
 */
public abstract class BaseFragment extends Fragment {
    private CompositeSubscription subscriptions;


    @Override
    public void onResume() {
        super.onResume();
        subscriptions = new CompositeSubscription();
    }

    void addSubscription(Subscription s) {
        subscriptions.add(s);
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.unsubscribe();
    }
}
