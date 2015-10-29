package io.aceisnotmycard.doit.view;


import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxSearchView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.RxToolbar;

import java.util.concurrent.TimeUnit;

import io.aceisnotmycard.doit.R;
import io.aceisnotmycard.doit.databinding.FragmentEditTaskBinding;
import io.aceisnotmycard.doit.model.Task;
import io.aceisnotmycard.doit.pipeline.Pipe;
import io.aceisnotmycard.doit.pipeline.events.NewTaskEvent;
import io.aceisnotmycard.doit.pipeline.events.SearchEvent;
import io.aceisnotmycard.doit.pipeline.events.TaskEditCompleteEvent;
import io.aceisnotmycard.doit.pipeline.events.TaskUpdatedEvent;
import io.aceisnotmycard.doit.viewmodel.EditTaskViewModel;

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

    public EditTaskFragment() {
    }


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
        b.editTaskToolbar.setNavigationOnClickListener(v -> Pipe.sendEvent(new TaskEditCompleteEvent()));
    }

    @Override
    public void onResume() {
        super.onResume();

        rx.Observable<String> titleObs = RxTextView.textChanges(b.editTaskTitle)
                .map(CharSequence::toString)
                .filter(s -> !s.isEmpty());
        rx.Observable<String> textObs = RxTextView.textChanges(b.editTaskText)
                .map(CharSequence::toString);

        rx.Observable<Boolean> importantObs = RxView.clicks(b.editTaskImportant)
                .map(viewClickEvent -> !viewModel.getImportant())
                .startWith(viewModel.getImportant())
                .doOnNext(this::setImportantDesign);

        addSubscription(rx.Observable.combineLatest(titleObs, textObs, importantObs, (title, text, important) ->
                new TaskUpdatedEvent(new Task(title, text, important)))
                .debounce(250L, TimeUnit.MILLISECONDS)
                .subscribe(taskUpdatedEvent -> {
                    Log.i(TAG, String.valueOf(taskUpdatedEvent.getData().isImportant()));
                    Pipe.sendEvent(taskUpdatedEvent);
                }));
    }

    private void setImportantDesign(boolean important) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            enterReveal(important);
        } else {
            b.editTaskLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),
                    important ? R.color.colorAccent : R.color.colorPrimary));
        }

        b.editTaskImportant.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                important ? R.drawable.ic_bookmark_24dp : R.drawable.ic_bookmark_outline_24dp));
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.onPause();
    }

    private void enterReveal(boolean important) {
        int cx = (int) b.editTaskImportant.getX() + (b.editTaskImportant.getMeasuredWidth() / 2);
        int cy = (int) b.editTaskImportant.getY() + (b.editTaskImportant.getMeasuredHeight() / 2);

        int radius = Math.max(b.editTaskBackImportant.getHeight(), b.editTaskBackImportant.getWidth());

        Animator anim = ViewAnimationUtils.createCircularReveal(important ? b.editTaskBackImportant : b.editTaskBackUsual,
                cx, cy, 0, radius);
        Log.e("ANIM", "CX:" + cx + " CY: " + cy + " radius: " + radius);
        if (important) {
            b.editTaskBackImportant.setVisibility(View.VISIBLE);
            b.editTaskBackUsual.setVisibility(View.INVISIBLE);
        } else {
            b.editTaskBackImportant.setVisibility(View.INVISIBLE);
            b.editTaskBackUsual.setVisibility(View.VISIBLE);
        }
        anim.start();
    }
}
