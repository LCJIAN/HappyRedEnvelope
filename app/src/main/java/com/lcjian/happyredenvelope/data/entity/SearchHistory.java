package com.lcjian.happyredenvelope.data.entity;

import android.support.annotation.Nullable;

import com.lcjian.happyredenvelope.data.db.table.SearchHistoryTable;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

@StorIOSQLiteType(table = SearchHistoryTable.TABLE_NAME)
public class SearchHistory {

    /**
     * If object was not inserted into db, id will be null
     */
    @Nullable
    @StorIOSQLiteColumn(name = SearchHistoryTable.COLUMN_ID, key = true)
    public Long id;

    @StorIOSQLiteColumn(name = SearchHistoryTable.COLUMN_TEXT)
    public String text;

    @StorIOSQLiteColumn(name = SearchHistoryTable.COLUMN_UPDATE_TIME)
    public Long updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchHistory video = (SearchHistory) o;

        if (id == null || video.id == null) {
            return text.equals(video.text);
        } else {
            return id.equals(video.id);
        }
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }
}