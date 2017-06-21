package com.lcjian.happyredenvelope.di.module;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.lcjian.happyredenvelope.data.db.AppSQLiteOpenHelper;
import com.lcjian.happyredenvelope.data.entity.SearchHistory;
import com.lcjian.happyredenvelope.data.entity.SearchHistorySQLiteTypeMapping;
import com.lcjian.happyredenvelope.di.scope.ApplicationScope;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    @Provides
    @NonNull
    @ApplicationScope
    public StorIOSQLite provideStorIOSQLite(@NonNull SQLiteOpenHelper sqLiteOpenHelper) {
        return DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .addTypeMapping(SearchHistory.class, new SearchHistorySQLiteTypeMapping())
                .build();
    }

    @Provides
    @NonNull
    @ApplicationScope
    public SQLiteOpenHelper provideSQLiteOpenHelper(@NonNull Application application) {
        return new AppSQLiteOpenHelper(application);
    }
}
