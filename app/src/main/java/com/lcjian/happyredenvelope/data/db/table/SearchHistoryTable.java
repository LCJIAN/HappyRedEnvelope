package com.lcjian.happyredenvelope.data.db.table;

import android.support.annotation.NonNull;

public class SearchHistoryTable {

    @NonNull
    public static final String TABLE_NAME = "SEARCH_HISTORY";

    @NonNull
    public static final String COLUMN_ID = "_id";
    @NonNull
    public static final String COLUMN_TEXT = "_text";
    @NonNull
    public static final String COLUMN_UPDATE_TIME = "_update_time";

    /* Better than static final field -> allows VM to unload useless String
    Because you need this string only once per application life on the device */
    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + SearchHistoryTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SearchHistoryTable.COLUMN_TEXT + " TEXT,"
                + SearchHistoryTable.COLUMN_UPDATE_TIME + " INTEGER)";
    }
}
