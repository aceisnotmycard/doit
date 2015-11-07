package io.aceisnotmycard.doit.view;


import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.aceisnotmycard.doit.R;
import io.aceisnotmycard.doit.databinding.FragmentEditTaskBinding;
import io.aceisnotmycard.doit.model.Task;
import io.aceisnotmycard.doit.pipeline.Pipe;
import io.aceisnotmycard.doit.pipeline.events.TaskEditCompleteEvent;
import io.aceisnotmycard.doit.pipeline.events.TaskUpdatedEvent;
import io.aceisnotmycard.doit.viewmodel.EditTaskViewModel;

public class EditTaskFragment extends BaseFragment {

    private static final String TAG = EditTaskFragment.class.getSimpleName();

    private static final String ARG_TASK = "arg_task";


    private EditTaskViewModel viewModel;
    private FragmentEditTaskBinding b;


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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentEditTaskBinding.inflate(inflater);

        Bundle args = getArguments();
        viewModel = new EditTaskViewModel(args != null ? args.getParcelable(ARG_TASK) : null,
                getActivity());
        b.setViewModel(viewModel);
        setBackgroundColor(viewModel.getImportant());
        setBackgroundView(viewModel.getImportant());
        return b.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        b.editTaskToolbar.setTitle("");

        ((AppCompatActivity) getActivity()).setSupportActionBar(b.editTaskToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        b.editTaskToolbar.setNavigationOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(b.getRoot().getWindowToken(), 0);
            Pipe.sendEvent(new TaskEditCompleteEvent());
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit_task, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                .doOnNext(this::setImportantDesign);

        addSubscription(rx.Observable.combineLatest(titleObs, textObs, importantObs, (title, text, important) ->
                new TaskUpdatedEvent(new Task(title, text, important)))
                .debounce(250L, TimeUnit.MILLISECONDS)
                .subscribe(Pipe::sendEvent));
    }

    private void setImportantDesign(boolean important) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            enterReveal(important);
        } else {
            setBackgroundColor(important);
        }

        b.editTaskImportant.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                important ? R.drawable.ic_bookmark_24dp : R.drawable.ic_bookmark_outline_24dp));
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.onPause();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void enterReveal(boolean important) {
        int cx = (int) b.editTaskImportant.getX() + (b.editTaskImportant.getMeasuredWidth() / 2);
        int cy = (int) b.editTaskImportant.getY() + (b.editTaskImportant.getMeasuredHeight() / 2);

        int radius = Math.max(b.editTaskBackImportant.getHeight(), b.editTaskBackImportant.getWidth());

        Animator anim = ViewAnimationUtils.createCircularReveal(important ? b.editTaskBackImportant : b.editTaskBackUsual,
                cx, cy, 0, radius);

        setBackgroundView(important);
        anim.start();
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setBackgroundColor(important);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private void setBackgroundColor(boolean important) {
        b.editTaskLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),
                important ? R.color.colorAccent : R.color.colorPrimary));
    }

    private void setBackgroundView(boolean important) {
        if (important) {
            b.editTaskBackImportant.setVisibility(View.VISIBLE);
            b.editTaskBackUsual.setVisibility(View.INVISIBLE);
        } else {
            b.editTaskBackImportant.setVisibility(View.INVISIBLE);
            b.editTaskBackUsual.setVisibility(View.VISIBLE);
        }
    }

    private void shareTask() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TITLE, b.editTaskTitle.getText().toString());
        sendIntent.putExtra(Intent.EXTRA_TEXT, b.editTaskText.getText().toString());
        sendIntent.setType("text/plain");
        getActivity().startActivity(Intent.createChooser(sendIntent, getActivity().getResources().getText(R.string.action_share)));
    }
}
