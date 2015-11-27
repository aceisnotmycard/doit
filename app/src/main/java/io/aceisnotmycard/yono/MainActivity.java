package io.aceisnotmycard.yono;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.aceisnotmycard.yono.pipeline.Pipe;
import io.aceisnotmycard.yono.pipeline.events.NewTaskEvent;
import io.aceisnotmycard.yono.pipeline.events.TaskEditCompleteEvent;
import io.aceisnotmycard.yono.pipeline.events.TasksListClickEvent;
import io.aceisnotmycard.yono.view.EditTaskFragment;
import io.aceisnotmycard.yono.view.TasksListFragment;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private CompositeSubscription subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            setFragment(TasksListFragment.newInstance());
        }
    }

    private void setFragment(Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscriptions = new CompositeSubscription();
        subscriptions.add(Pipe.recvEvent(NewTaskEvent.class, event -> setFragment(EditTaskFragment.newInstance())));
        subscriptions.add(Pipe.recvEvent(TasksListClickEvent.class, event -> setFragment(EditTaskFragment.newInstance(event.getData()))));
        subscriptions.add(Pipe.recvEvent(TaskEditCompleteEvent.class, event -> setFragment(TasksListFragment.newInstance())));
    }

    @Override
    protected void onPause() {
        super.onPause();
        subscriptions.unsubscribe();
    }
}
