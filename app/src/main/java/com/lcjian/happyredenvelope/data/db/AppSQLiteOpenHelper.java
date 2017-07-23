package com.lcjian.happyredenvelope.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lcjian.happyredenvelope.data.db.table.PushMessageTable;
import com.lcjian.happyredenvelope.data.db.table.SearchHistoryTable;


public class AppSQLiteOpenHelper extends SQLiteOpenHelper {

    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "RedEnvelope.db";

    /**
     * 数据库版本
     */
    private static int DATABASE_VERSION = 1;

    public AppSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SearchHistoryTable.getCreateTableQuery());
        db.execSQL(PushMessageTable.getCreateTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
