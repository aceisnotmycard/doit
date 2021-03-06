package io.aceisnotmycard.yono.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import io.aceisnotmycard.yono.model.Task;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by sergey on 22/10/15
 */
public class TaskDao {
    private static TaskDao DAO;

    private static final String TAG = TaskDao.class.getSimpleName();

    private DoItHelper dbHelper;
    private SqlBrite sqlBrite;
    BriteDatabase db;

    private TaskDao(Context context) {
        sqlBrite = SqlBrite.create();
        dbHelper = new DoItHelper(context);
        db = sqlBrite.wrapDatabaseHelper(dbHelper);
    }

    public static TaskDao getDao(Context context) {
        if (DAO == null) {
            DAO = new TaskDao(context.getApplicationContext());
        }
        return DAO;
    }

    // todo: refactor
    public Observable<List<Task>> searchFor(String term) {
        return db.createQuery(DoItContract.Task.TABLE_NAME,
                "SELECT * FROM " + DoItContract.Task.TABLE_NAME
                        + " WHERE " + DoItContract.Task.COL_TEXT + " LIKE " + "'%" + term + "%'")
                .mapToList(MAPPER)
                .take(1);
    }

    // for debug
    private void printTasks(List<Task> tasks) {
        for (Task t : tasks) {
            Log.i("Task", "Position: " + t.getPosition() + " text: " + t.getText());
        }
    }

    public int insert(Task task) {
        ContentValues cv = new Builder()
                .text(task.getText())
                .important(task.isImportant())
                .build();
        return (int) db.insert(DoItContract.Task.TABLE_NAME, cv);
    }

    public int insert(int pos, Task task) {
        ContentValues cv = new Builder()
                .text(task.getText())
                .important(task.isImportant())
                .position(pos)
                .build();
        return (int) db.insert(DoItContract.Task.TABLE_NAME, cv);
    }

    public boolean update(Task task) {
        ContentValues cv = new Builder()
                .text(task.getText())
                .position(task.getPosition())
                .important(task.isImportant())
                .build();
        return db.update(DoItContract.Task.TABLE_NAME, cv, DoItContract.Task._ID + " = " + task.getPosition()) > 0;
    }

    public boolean delete(Task task) {
        return db.delete(DoItContract.Task.TABLE_NAME, DoItContract.Task._ID + " = " + task.getPosition()) > 0;
    }

    private static final Func1<Cursor, Task> MAPPER = c -> {
        int id = (int) c.getLong(c.getColumnIndex(DoItContract.Task._ID));
        String text = c.getString(c.getColumnIndex(DoItContract.Task.COL_TEXT));
        boolean important = c.getInt(c.getColumnIndex(DoItContract.Task.COL_IMPORTANT)) > 0;
        return new Task(id, text, important);
    };


    public static class Builder {
        private final ContentValues cv = new ContentValues();

        public Builder text(String text) {
            cv.put(DoItContract.Task.COL_TEXT, text);
            return this;
        }

        public Builder position(int pos) {
            cv.put(DoItContract.Task._ID, pos);
            return this;
        }

        public Builder important(boolean important) {
            cv.put(DoItContract.Task.COL_IMPORTANT, important ? 1 : 0);
            return this;
        }

        public ContentValues build() {
            return cv;
        }
    }
}
