package com.lcjian.happyredenvelope.di.module;

import android.support.annotation.NonNull;

import com.lcjian.happyredenvelope.RxBus;
import com.lcjian.happyredenvelope.di.scope.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module
public class RxBusModule {

    @Provides
    @NonNull
    @ApplicationScope
    public RxBus provideRxBus() {
        return new RxBus();
    }
}
