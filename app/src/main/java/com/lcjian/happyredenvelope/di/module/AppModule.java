package com.lcjian.happyredenvelope.di.module;

import android.app.Application;
import android.support.annotation.NonNull;

import com.lcjian.happyredenvelope.di.scope.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @NonNull
    private final Application application;

    public AppModule(@NonNull Application application) {
        this.application = application;
    }

    @Provides
    @NonNull
    @ApplicationScope
    public Application provideApplication() {
        return application;
    }
}
