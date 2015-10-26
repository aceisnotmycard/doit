package io.aceisnotmycard.doit;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.aceisnotmycard.doit.pipeline.Pipe;
import io.aceisnotmycard.doit.pipeline.events.NewTaskEvent;
import io.aceisnotmycard.doit.pipeline.events.TaskEditCompleteEvent;
import io.aceisnotmycard.doit.pipeline.events.TasksListClickEvent;
import io.aceisnotmycard.doit.view.EditTaskFragment;
import io.aceisnotmycard.doit.view.TasksListFragment;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private CompositeSubscription subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeSubscription();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            setFragment(TasksListFragment.newInstance());
        }
    }

    private void setFragment(Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscriptions.add(Pipe.getObservable().filter(event -> event instanceof NewTaskEvent)
                .map(event -> (NewTaskEvent) event)
                .subscribe(event -> {
                    setFragment(EditTaskFragment.newInstance());
                }));
        subscriptions.add(Pipe.getObservable().filter(event -> event instanceof TasksListClickEvent)
                .map(event -> (TasksListClickEvent) event)
                .subscribe(event -> {
                    setFragment(EditTaskFragment.newInstance(event.getData()));
                }));
        subscriptions.add(Pipe.getObservable().filter(event -> event instanceof TaskEditCompleteEvent)
                .map(event -> (TaskEditCompleteEvent) event)
                .subscribe(event -> {
                    setFragment(TasksListFragment.newInstance());
                }));
    }

    @Override
    protected void onPause() {
        super.onPause();
        subscriptions.unsubscribe();
    }
}
