package com.lcjian.happyredenvelope.di.component;

import android.app.Application;
import android.content.SharedPreferences;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.BaseDialogFragment;
import com.lcjian.happyredenvelope.BaseFragment;
import com.lcjian.happyredenvelope.RxBus;
import com.lcjian.happyredenvelope.data.network.RestAPI;
import com.lcjian.happyredenvelope.di.module.AppModule;
import com.lcjian.happyredenvelope.di.module.DbModule;
import com.lcjian.happyredenvelope.di.module.RestAPIModule;
import com.lcjian.happyredenvelope.di.module.RxBusModule;
import com.lcjian.happyredenvelope.di.module.SharedPreferenceModule;
import com.lcjian.happyredenvelope.di.scope.ApplicationScope;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import javax.inject.Named;

import dagger.Component;

@ApplicationScope
@Component(modules = {AppModule.class, DbModule.class, RestAPIModule.class, RxBusModule.class, SharedPreferenceModule.class})
public interface AppComponent {

    Application application();

    StorIOSQLite storIOSQLite();

    RestAPI restAPI();

    RxBus rxBus();

    @Named("user_info")
    SharedPreferences userInfoSharedPreferences();

    @Named("setting")
    SharedPreferences settingSharedPreferences();

    void inject(BaseActivity baseActivity);

    void inject(BaseFragment baseFragment);

    void inject(BaseDialogFragment baseDialogFragment);

    void inject(App app);

}
