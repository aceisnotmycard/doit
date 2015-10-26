package io.aceisnotmycard.doit.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.io.IOException;
import java.util.List;

import io.aceisnotmycard.doit.model.Task;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by sergey on 22/10/15
 */
public class TaskDao {
    private static TaskDao DAO;

    private static final String TAG = TaskDao.class.getSimpleName();

    private enum Action {
        READ,
        INSERT,
        UPDATE,
        DELETE
    }

    private Action action;
    private DoItHelper dbHelper;
    private SqlBrite sqlBrite;
    BriteDatabase db;

    private TaskDao(Context context) {
        action = Action.READ;
        sqlBrite = SqlBrite.create();
        dbHelper = new DoItHelper(context);
        db = sqlBrite.wrapDatabaseHelper(dbHelper);
    }

    public static TaskDao getDao(Context context) {
        if (DAO == null) {
            DAO = new TaskDao(context);
        }
        return DAO;
    }

    public void close() {
        dbHelper.close();
        try {
            db.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    public Observable<List<Task>> getTasks(){
        action = Action.READ;
        return db.createQuery(DoItContract.Task.TABLE_NAME,
                "SELECT * FROM " + DoItContract.Task.TABLE_NAME)
                .mapToList(MAPPER)
                .take(1);
    }

    public int insert(Task task) {
        action = Action.INSERT;
        ContentValues cv = new Builder()
                .text(task.getText())
                .title(task.getTitle())
                .build();
        return (int) db.insert(DoItContract.Task.TABLE_NAME, cv);
    }

    public int insert(String title, String text) {
        action = Action.INSERT;
        ContentValues cv = new Builder()
                .text(text)
                .title(title)
                .build();
        return (int) db.insert(DoItContract.Task.TABLE_NAME, cv);
    }

    public boolean update(Task task) {
        action = Action.DELETE;
        ContentValues cv = new Builder()
                .text(task.getText())
                .title(task.getTitle())
                .position(task.getPosition())
                .build();
        return db.update(DoItContract.Task.TABLE_NAME, cv, DoItContract.Task._ID + " = " + task.getPosition()) > 0;
    }

    public boolean delete(Task task) {
        action = Action.DELETE;
        return db.delete(DoItContract.Task.TABLE_NAME, DoItContract.Task._ID + " = " + task.getPosition()) > 0;
    }

    private static final Func1<Cursor, Task> MAPPER = c -> {
        int id = (int) c.getLong(c.getColumnIndex(DoItContract.Task._ID));
        String title = c.getString(c.getColumnIndex(DoItContract.Task.COL_TITLE));
        String text = c.getString(c.getColumnIndex(DoItContract.Task.COL_TEXT));
        return new Task(title, id, text, false);
    };


    public static class Builder {
        private final ContentValues cv = new ContentValues();

        public Builder title(String title) {
            cv.put(DoItContract.Task.COL_TITLE, title);
            return this;
        }

        public Builder text(String text) {
            cv.put(DoItContract.Task.COL_TEXT, text);
            return this;
        }

        public Builder position(int pos) {
            cv.put(DoItContract.Task._ID, pos);
            return this;
        }

        public ContentValues build() {
            return cv;
        }
    }
}
