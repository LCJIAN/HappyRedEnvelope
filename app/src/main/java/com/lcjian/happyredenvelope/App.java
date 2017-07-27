package com.lcjian.happyredenvelope;

import android.app.Application;
import android.content.Context;

import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeInitCallback;
import com.db.ta.sdk.TaSDK;
import com.lcjian.happyredenvelope.data.entity.PushMessage;
import com.lcjian.happyredenvelope.di.component.AppComponent;
import com.lcjian.happyredenvelope.di.component.DaggerAppComponent;
import com.lcjian.happyredenvelope.di.module.AppModule;
import com.lcjian.happyredenvelope.di.module.DbModule;
import com.lcjian.happyredenvelope.di.module.RestAPIModule;
import com.lcjian.happyredenvelope.di.module.RxBusModule;
import com.lcjian.happyredenvelope.di.module.SharedPreferenceModule;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.squareup.leakcanary.LeakCanary;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import javax.inject.Inject;

import timber.log.Timber;

public class App extends Application {

    private static App INSTANCE;
    @Inject
    protected StorIOSQLite mStorIOSQLite;
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
        mAppComponent.inject(this);

        Config.DEBUG = BuildConfig.DEBUG;
        PlatformConfig.setWeixin(Constants.WE_CHAT_ID, Constants.WE_CHAT_SECRET);
        UMShareAPI.get(this);
        TaSDK.init(this);

        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.setDebugMode(BuildConfig.DEBUG);
        mPushAgent.setResourcePackageName("com.lcjian.happyredenvelope");
        mPushAgent.setMessageHandler(new UmengMessageHandler() {
            @Override
            public void handleMessage(Context context, UMessage uMessage) {
                PushMessage pushMessage = new PushMessage();
                pushMessage.title = uMessage.title;
                pushMessage.text = uMessage.text;
                pushMessage.createTime = System.currentTimeMillis();
                mStorIOSQLite.put().object(pushMessage).prepare().asRxObservable().subscribe();
                super.handleMessage(context, uMessage);
            }
        });
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });
        AlibcTradeSDK.asyncInit(this, new AlibcTradeInitCallback() {
            @Override
            public void onSuccess() {
                //初始化成功，设置相关的全局配置参数
            }

            @Override
            public void onFailure(int code, String msg) {
                //初始化失败，可以根据code和msg判断失败原因，详情参见错误说明
            }
        });

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
