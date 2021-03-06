package com.lcjian.happyredenvelope.ui.room;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.db.ta.sdk.TMBrTmView;
import com.db.ta.sdk.TmListener;
import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.Constants;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.UserSummary;

import java.text.DecimalFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class UserActivity extends BaseActivity {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;
    @BindView(R.id.iv_user_avatar)
    ImageView iv_user_avatar;
    @BindView(R.id.tv_day_count)
    TextView tv_day_count;
    @BindView(R.id.tv_week_count)
    TextView tv_week_count;
    @BindView(R.id.tv_month_count)
    TextView tv_month_count;
    @BindView(R.id.tv_total_amount)
    TextView tv_total_amount;
    @BindView(R.id.ad_small_banner)
    TMBrTmView ad_small_banner;

    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        btn_top_bar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_top_bar_right.setVisibility(View.GONE);

        loadAd();

        mSubscription = mRestAPI.redEnvelopeService().getUserInfo(getIntent().getLongExtra("user_id", 0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<UserSummary>>() {
                    @Override
                    public void call(ResponseData<UserSummary> userSummaryResponseData) {
                        if (userSummaryResponseData.code == 0) {
                            Glide.with(UserActivity.this)
                                    .load(userSummaryResponseData.data.hblUser.userHeadimg)
                                    .apply(Global.userAvatar)
                                    .transition(Global.crossFade)
                                    .into(iv_user_avatar);
                            tv_top_bar_title.setText(userSummaryResponseData.data.hblUser.userNickname);
                            tv_day_count.setText(String.valueOf(userSummaryResponseData.data.dayCount));
                            tv_week_count.setText(String.valueOf(userSummaryResponseData.data.weekCount));
                            tv_month_count.setText(String.valueOf(userSummaryResponseData.data.monthCount));
                            tv_total_amount.setText(String.format(Locale.getDefault(), "%s%s",
                                    "￥", new DecimalFormat("0.00").format(userSummaryResponseData.data.total)));
                        } else {
                            Toast.makeText(App.getInstance(), userSummaryResponseData.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAd() {
        ad_small_banner.setAdListener(new TmListener() {
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
        ad_small_banner.loadAd(Constants.USER_BANNER_AD);
    }

    private void destroyAd() {
        if (ad_small_banner != null) {
            ad_small_banner.destroy();
        }
    }

    @Override
    protected void onDestroy() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        destroyAd();
        super.onDestroy();
    }
}
