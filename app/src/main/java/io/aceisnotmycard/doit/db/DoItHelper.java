package io.aceisnotmycard.doit.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sergey on 19/10/15.
 */
public class DoItHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "nottodo.db";
    public static final int DATABASE_VERSION = 1;

    public DoItHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DoItContract.Task.TABLE_NAME + "(" +
                        DoItContract.Task.COL_TEXT + " TEXT, " +
                        DoItContract.Task.COL_TITLE + " TEXT NOT NULL, " +
                        DoItContract.Task._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ");"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

