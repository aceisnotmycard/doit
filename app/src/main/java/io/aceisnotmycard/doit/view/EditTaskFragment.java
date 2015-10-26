package io.aceisnotmycard.doit.view;


import android.app.ActionBar;
import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.aceisnotmycard.doit.databinding.FragmentEditTaskBinding;
import io.aceisnotmycard.doit.model.Task;
import io.aceisnotmycard.doit.pipeline.Pipe;
import io.aceisnotmycard.doit.pipeline.events.TaskEditCompleteEvent;
import io.aceisnotmycard.doit.pipeline.events.TaskUpdatedEvent;
import io.aceisnotmycard.doit.viewmodel.EditTaskViewModel;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditTaskFragment extends BaseFragment {

    private static final String TAG = EditTaskFragment.class.getSimpleName();

    private static final String ARG_TASK = "arg_task";

    private EditTaskViewModel viewModel;
    private FragmentEditTaskBinding b;
    private android.support.v7.app.ActionBar actionBar;

    public static EditTaskFragment newInstance(Task t) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_TASK, t);
        EditTaskFragment fragment = new EditTaskFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static EditTaskFragment newInstance() {
        return new EditTaskFragment();
    }

    public EditTaskFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle args = getArguments();
        if (args != null) {
            viewModel = new EditTaskViewModel(args.getParcelable(ARG_TASK), getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentEditTaskBinding.inflate(inflater);
        b.setViewModel(viewModel);

        ((AppCompatActivity) getActivity()).setSupportActionBar(b.editTaskToolbar);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        b.editTaskToolbar.setNavigationOnClickListener(v ->
                        Pipe.getObserver().onNext(new TaskEditCompleteEvent())
        );
        return b.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        rx.Observable<String> titleObs = RxTextView.textChanges(b.editTaskTitle)
                .map(CharSequence::toString)
                .filter(s -> !s.isEmpty());
        rx.Observable<String> textObs = RxTextView.textChanges(b.editTaskText)
                .map(CharSequence::toString);

        addSubscription(rx.Observable.combineLatest(titleObs, textObs, (title, text) -> new TaskUpdatedEvent(title, text))
                .debounce(250L, TimeUnit.MILLISECONDS)
                .subscribe(taskUpdatedEvent -> {
                    Pipe.getObserver().onNext(taskUpdatedEvent);
                }));
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.onPause();
    }
}
