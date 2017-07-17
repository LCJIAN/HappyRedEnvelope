package com.lcjian.happyredenvelope;

import android.app.Application;

import com.db.ta.sdk.TaSDK;
import com.lcjian.happyredenvelope.di.component.AppComponent;
import com.lcjian.happyredenvelope.di.component.DaggerAppComponent;
import com.lcjian.happyredenvelope.di.module.AppModule;
import com.lcjian.happyredenvelope.di.module.DbModule;
import com.lcjian.happyredenvelope.di.module.RestAPIModule;
import com.lcjian.happyredenvelope.di.module.RxBusModule;
import com.lcjian.happyredenvelope.di.module.SharedPreferenceModule;
import com.squareup.leakcanary.LeakCanary;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import timber.log.Timber;

public class App extends Application {

    private static App INSTANCE;

    private AppComponent mAppComponent;

    public static App getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .dbModule(new DbModule())
                .restAPIModule(new RestAPIModule())
                .rxBusModule(new RxBusModule())
                .sharedPreferenceModule(new SharedPreferenceModule())
                .build();

        Config.DEBUG = BuildConfig.DEBUG;
        PlatformConfig.setWeixin(Constants.WE_CHAT_ID, Constants.WE_CHAT_SECRET);
        UMShareAPI.get(this);
        TaSDK.init(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ErrorTree());
        }
        LeakCanary.install(this);
    }

    public AppComponent appComponent() {
        return mAppComponent;
    }

}
