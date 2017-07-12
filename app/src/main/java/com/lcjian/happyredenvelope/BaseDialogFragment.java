package com.lcjian.happyredenvelope;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;

import com.lcjian.happyredenvelope.data.network.RestAPI;
import com.lcjian.happyredenvelope.di.component.AppComponent;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import javax.inject.Inject;
import javax.inject.Named;

public class BaseDialogFragment extends AppCompatDialogFragment {

    @Inject
    protected StorIOSQLite mStorIOSQLite;
    @Inject
    protected RestAPI mRestAPI;
    @Inject
    protected RxBus mRxBus;
    @Inject
    @Named("user_info")
    protected SharedPreferences mUserInfoSp;
    @Inject
    @Named("setting")
    protected SharedPreferences mSettingSp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateComponent(App.getInstance().appComponent());
    }

    protected void onCreateComponent(AppComponent appComponent) {
        appComponent.inject(this);
    }
}
