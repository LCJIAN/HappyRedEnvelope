package com.lcjian.happyredenvelope.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.db.ta.sdk.TMShTmListener;
import com.db.ta.sdk.TMShTmView;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.Constants;
import com.lcjian.happyredenvelope.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.TMSh_container)
    TMShTmView TMSh_container;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            TMSh_container.setTargetClass(SplashActivity.this, MainActivity.class);
            TMSh_container.setAdListener(new TMShTmListener() {
                @Override
                public void onTimeOut() {

                }

                @Override
                public void onReceiveAd() {

                }

                @Override
                public void onFailedToReceiveAd() {

                }

                @Override
                public void onLoadFailed() {

                }

                @Override
                public void onCloseClick() {

                }

                @Override
                public void onAdClick() {

                }

                @Override
                public void onAdExposure() {

                }
            });
            TMSh_container.loadAd(Constants.SPLASH_AD);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        getWindow().getDecorView().postDelayed(mRunnable, 1000);
    }

    @Override
    protected void onDestroy() {
        if (TMSh_container != null) {
            TMSh_container.destroy();
        }
        getWindow().getDecorView().removeCallbacks(mRunnable);
        super.onDestroy();
    }
}
