package com.lcjian.happyredenvelope.data.entity;

import android.support.annotation.Nullable;

import com.lcjian.happyredenvelope.data.db.table.PushMessageTable;
import com.lcjian.lib.entity.Displayable;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

@StorIOSQLiteType(table = PushMessageTable.TABLE_NAME)
public class PushMessage implements Displayable {

    /**
     * If object was not inserted into db, id will be null
     */
    @Nullable
    @StorIOSQLiteColumn(name = PushMessageTable.COLUMN_ID, key = true)
    public Long id;

    @StorIOSQLiteColumn(name = PushMessageTable.COLUMN_TITLE)
    public String title;

    @StorIOSQLiteColumn(name = PushMessageTable.COLUMN_TEXT)
    public String text;

    @StorIOSQLiteColumn(name = PushMessageTable.COLUMN_CREATE_TIME)
    public Long createTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PushMessage pushMessage = (PushMessage) o;

        if (id == null || pushMessage.id == null) {
            return title.equals(pushMessage.title) && text.equals(pushMessage.text);
        } else {
            return id.equals(pushMessage.id);
        }
    }

    @Override
    public int hashCode() {
        return (title + text).hashCode();
    }
}