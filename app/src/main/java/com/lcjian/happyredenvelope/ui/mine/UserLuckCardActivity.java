package com.lcjian.happyredenvelope.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.LuckCardSummary;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.ui.web.WebViewActivity;
import com.lcjian.lib.util.common.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class UserLuckCardActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;
    @BindView(R.id.tv_time_left)
    TextView tv_time_left;
    @BindView(R.id.tv_total_buy_count)
    TextView tv_total_buy_count;
    @BindView(R.id.tv_total_buy_time)
    TextView tv_total_buy_time;
    @BindView(R.id.tv_total_present_count)
    TextView tv_total_present_count;
    @BindView(R.id.tv_total_present_time)
    TextView tv_total_present_time;
    @BindView(R.id.btn_go_buy_luck_card)
    Button btn_go_buy_luck_card;
    @BindView(R.id.tv_luck_card_tutorial)
    TextView tv_luck_card_tutorial;

    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_lucky_card);
        ButterKnife.bind(this);

        btn_top_bar_left.setOnClickListener(this);
        btn_go_buy_luck_card.setOnClickListener(this);
        tv_luck_card_tutorial.setOnClickListener(this);

        tv_top_bar_title.setText(R.string.luck_card);
        tv_top_bar_right.setText(R.string.luck_card_history);
        tv_top_bar_right.setVisibility(View.GONE);

        mSubscription = mRestAPI.redEnvelopeService()
                .getLuckCardSummary(mUserInfoSp.getLong("user_id", 0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<LuckCardSummary>>() {
                    @Override
                    public void call(ResponseData<LuckCardSummary> luckCardSummaryResponseData) {
                        if (luckCardSummaryResponseData.code == 0) {
                            LuckCardSummary luckCardSummary = luckCardSummaryResponseData.data;
                            tv_time_left.setText(StringUtils.stringForTime((int) luckCardSummary.lefttime * 1000));
                            tv_total_buy_count.setText(getString(R.string.luck_card_buy_total, luckCardSummary.sell.totalCount));
                            tv_total_buy_time.setText(getString(R.string.luck_card_time_total, luckCardSummary.sell.totalTime));
                            tv_total_present_count.setText(getString(R.string.luck_card_present_total, luckCardSummary.send.totalCount));
                            tv_total_present_time.setText(getString(R.string.luck_card_time_total, luckCardSummary.send.totalTime / 60));
                        } else {
                            Toast.makeText(App.getInstance(), luckCardSummaryResponseData.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_top_bar_left: {
                onBackPressed();
            }
            break;
            case R.id.btn_go_buy_luck_card: {
                startActivity(new Intent(this, BuyLuckCardActivity.class));
            }
            break;
            case R.id.tv_luck_card_tutorial: {
                startActivity(new Intent(this, WebViewActivity.class)
                        .putExtra("url", mUserInfoSp.getString("luck_card_tutorial_link", "")));
            }
            break;
            default:
                break;
        }
    }
}
