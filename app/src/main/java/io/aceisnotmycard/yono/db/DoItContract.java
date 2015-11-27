package io.aceisnotmycard.yono.db;

import android.provider.BaseColumns;

/**
 * Created by sergey on 19/10/15.
 */
public class DoItContract {
    public static abstract class Task implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COL_TEXT = "text";
        public static final String COL_TITLE = "title";
        public static final String COL_IMPORTANT = "important";
    }
}
