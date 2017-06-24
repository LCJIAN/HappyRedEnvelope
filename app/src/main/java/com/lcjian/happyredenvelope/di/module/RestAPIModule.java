package com.lcjian.happyredenvelope.di.module;

import android.support.annotation.NonNull;

import com.lcjian.happyredenvelope.data.network.RestAPI;
import com.lcjian.happyredenvelope.di.scope.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module
public class RestAPIModule {

    @Provides
    @NonNull
    @ApplicationScope
    public RestAPI provideYesTV() {
        return new RestAPI();
    }
}
