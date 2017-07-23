package com.lcjian.happyredenvelope.data.db.table;

import android.support.annotation.NonNull;

public class PushMessageTable {

    @NonNull
    public static final String TABLE_NAME = "PUSH_MESSAGE";

    @NonNull
    public static final String COLUMN_ID = "_id";
    @NonNull
    public static final String COLUMN_TITLE = "_title";
    @NonNull
    public static final String COLUMN_TEXT = "_text";
    @NonNull
    public static final String COLUMN_CREATE_TIME = "_create_time";

    /* Better than static final field -> allows VM to unload useless String
    Because you need this string only once per application life on the device */
    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + PushMessageTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PushMessageTable.COLUMN_TITLE + " TEXT,"
                + PushMessageTable.COLUMN_TEXT + " TEXT,"
                + PushMessageTable.COLUMN_CREATE_TIME + " INTEGER)";
    }
}
