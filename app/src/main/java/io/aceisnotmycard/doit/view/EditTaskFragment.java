package io.aceisnotmycard.doit.view;


import android.app.ActionBar;
import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.jakewharton.rxbinding.widget.CompoundButtonCheckedChangeEvent;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.aceisnotmycard.doit.R;
import io.aceisnotmycard.doit.databinding.FragmentEditTaskBinding;
import io.aceisnotmycard.doit.model.Task;
import io.aceisnotmycard.doit.pipeline.Pipe;
import io.aceisnotmycard.doit.pipeline.events.TaskEditCompleteEvent;
import io.aceisnotmycard.doit.pipeline.events.TaskUpdatedEvent;
import io.aceisnotmycard.doit.viewmodel.EditTaskViewModel;
import rx.Observable;
import rx.subjects.PublishSubject;
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

    private boolean importantChecked;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentEditTaskBinding.inflate(inflater);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        viewModel = new EditTaskViewModel(args != null ? args.getParcelable(ARG_TASK) : null,
                getActivity());

        b.setViewModel(viewModel);

        ((AppCompatActivity) getActivity()).setSupportActionBar(b.editTaskToolbar);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        b.editTaskToolbar.setNavigationOnClickListener(v -> Pipe.getObserver().onNext(new TaskEditCompleteEvent()));
    }

    @Override
    public void onResume() {
        super.onResume();

        rx.Observable<String> titleObs = RxTextView.textChanges(b.editTaskTitle)
                .map(CharSequence::toString)
                .filter(s -> !s.isEmpty());
        rx.Observable<String> textObs = RxTextView.textChanges(b.editTaskText)
                .map(CharSequence::toString);

        rx.Observable<Boolean> importantObs = RxCompoundButton.checkedChanges(b.editTaskImportant);

        addSubscription(rx.Observable.combineLatest(titleObs, textObs, importantObs, (title, text, important) ->
                new TaskUpdatedEvent(new Task(title, text, important)))
                .debounce(250L, TimeUnit.MILLISECONDS)
                .subscribe(taskUpdatedEvent -> {
                    Log.i(TAG, String.valueOf(taskUpdatedEvent.getData().isImportant()));
                    Pipe.getObserver().onNext(taskUpdatedEvent);
                }));
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.onPause();
    }
}
