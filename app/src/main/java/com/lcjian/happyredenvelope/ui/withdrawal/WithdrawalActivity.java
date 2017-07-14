package com.lcjian.happyredenvelope.ui.withdrawal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.VipInfo;
import com.lcjian.happyredenvelope.data.entity.Withdrawal;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class WithdrawalActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;
    @BindView(R.id.tv_my_balance)
    TextView tv_my_balance;
    @BindView(R.id.btn_go_withdrawal)
    Button btn_go_withdrawal;
    @BindView(R.id.tv_withdrawal_tutorial)
    TextView tv_withdrawal_tutorial;
    @BindView(R.id.btn_copy_token)
    Button btn_copy_token;
    @BindView(R.id.tv_current_withdrawal_token)
    TextView tv_current_withdrawal_token;
    @BindView(R.id.ll_current_withdrawal)
    LinearLayout ll_current_withdrawal;

    private Boolean mIsFirstWithdrawal;
    private Withdrawal mCurrentWithdrawal;

    private ProgressDialog mProgressDialog;

    private Subscription mSubscription;
    private Subscription mSubscription2;
    private Subscription mSubscription3;
    private Subscription mSubscription4;

    private boolean mIsVip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        tv_top_bar_title.setText(R.string.withdrawal);
        tv_top_bar_right.setText(R.string.withdrawal_history);
        tv_my_balance.setText(String.format(Locale.getDefault(), "%s%s",
                "￥", new DecimalFormat("0.00").format(mUserInfoSp.getFloat("user_balance", 0))));

        btn_go_withdrawal.setVisibility(View.INVISIBLE);
        ll_current_withdrawal.setVisibility(View.INVISIBLE);

        btn_top_bar_left.setOnClickListener(this);
        tv_top_bar_right.setOnClickListener(this);
        tv_my_balance.setOnClickListener(this);
        btn_go_withdrawal.setOnClickListener(this);
        btn_copy_token.setOnClickListener(this);
        tv_withdrawal_tutorial.setOnClickListener(this);

        checkVip();
        refresh();
    }

    @Override
    protected void onDestroy() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        if (mSubscription2 != null) {
            mSubscription2.unsubscribe();
        }
        if (mSubscription3 != null) {
            mSubscription3.unsubscribe();
        }
        if (mSubscription4 != null) {
            mSubscription4.unsubscribe();
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
            case R.id.tv_top_bar_right: {
                startActivity(new Intent(this, WithdrawalHistoriesActivity.class));
            }
            break;
            case R.id.btn_go_withdrawal: {
                float balance = mUserInfoSp.getFloat("user_balance", 0);
                if (!mIsFirstWithdrawal || balance <= 0f) {
                    if (mIsVip) {
                        if (balance < 50) {
                            Toast.makeText(App.getInstance(), R.string.vip_withdrawal_msg, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (balance < 100) {
                            Toast.makeText(App.getInstance(), R.string.none_vip_withdrawal_msg, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                saveWithdrawal(balance);
            }
            break;
            case R.id.btn_copy_token: {
                CopyTokenDialogFragment.newInstance(mCurrentWithdrawal.token.tokenContent).show(getSupportFragmentManager(), "CopyTokenDialogFragment");
            }
            break;
            case R.id.tv_withdrawal_tutorial: {
            }
            break;
            default:
                break;
        }
    }

    private void refresh() {
        mProgressDialog.show();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mSubscription = Observable.zip(
                mRestAPI.redEnvelopeService().isFirstWithdrawal(mUserInfoSp.getLong("user_id", 0)),
                mRestAPI.redEnvelopeService().getCurrentWithdrawals(mUserInfoSp.getLong("user_id", 0)),
                new Func2<ResponseData<Boolean>, ResponseData<List<Withdrawal>>, Pair<ResponseData<Boolean>, ResponseData<List<Withdrawal>>>>() {
                    @Override
                    public Pair<ResponseData<Boolean>, ResponseData<List<Withdrawal>>> call(
                            ResponseData<Boolean> booleanResponseData,
                            ResponseData<List<Withdrawal>> listResponseData) {
                        return Pair.create(booleanResponseData, listResponseData);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<ResponseData<Boolean>, ResponseData<List<Withdrawal>>>>() {
                    @Override
                    public void call(Pair<ResponseData<Boolean>, ResponseData<List<Withdrawal>>> responseDataResponseDataPair) {
                        mProgressDialog.dismiss();
                        if (responseDataResponseDataPair.first.code != 0) {
                            Toast.makeText(App.getInstance(), responseDataResponseDataPair.first.msg, Toast.LENGTH_SHORT).show();
                        }
                        if (responseDataResponseDataPair.second.code != 0) {
                            Toast.makeText(App.getInstance(), responseDataResponseDataPair.second.msg, Toast.LENGTH_SHORT).show();
                        }
                        if (responseDataResponseDataPair.first.code == 0 && responseDataResponseDataPair.second.code == 0) {
                            mIsFirstWithdrawal = responseDataResponseDataPair.first.data;
                            if (responseDataResponseDataPair.second.data != null && !responseDataResponseDataPair.second.data.isEmpty()) {
                                mCurrentWithdrawal = responseDataResponseDataPair.second.data.get(0);
                            }
                        }
                        setupWithdrawal();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mProgressDialog.dismiss();
                        Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkVip() {
        mSubscription4 = mRestAPI.redEnvelopeService().isVip(mUserInfoSp.getLong("user_id", 0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<VipInfo>>() {
                    @Override
                    public void call(ResponseData<VipInfo> vipInfoResponseData) {
                        if (vipInfoResponseData.code == 0) {
                            mIsVip = vipInfoResponseData.data.isvip;
                        }
                    }
                });
    }

    private void saveWithdrawal(float withdrawalAmount) {
        mProgressDialog.show();
        if (mSubscription2 != null) {
            mSubscription2.unsubscribe();
        }
        mSubscription2 = mRestAPI.redEnvelopeService().saveWithdrawal(mUserInfoSp.getLong("user_id", 0), withdrawalAmount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<Withdrawal>>() {
                    @Override
                    public void call(ResponseData<Withdrawal> withdrawalResponseData) {
                        mProgressDialog.dismiss();
                        if (withdrawalResponseData.code == 0) {
                            refresh();
                        } else {
                            Toast.makeText(App.getInstance(), withdrawalResponseData.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mProgressDialog.dismiss();
                        Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupWithdrawal() {
        if (mCurrentWithdrawal != null) {
            btn_go_withdrawal.setVisibility(View.GONE);
            ll_current_withdrawal.setVisibility(View.VISIBLE);

            tv_current_withdrawal_token.setText(mCurrentWithdrawal.token.tokenContent);

            long left = mCurrentWithdrawal.token.tokenCreatetime + mCurrentWithdrawal.token.tokenTime * 1000 - System.currentTimeMillis();
            if (left > 0) {
                if (mSubscription3 != null) {
                    mSubscription3.unsubscribe();
                }
                mSubscription3 = Observable.combineLatest(
                        Observable.just(left),
                        Observable.interval(1, TimeUnit.SECONDS),
                        new Func2<Long, Long, Long>() {

                            @Override
                            public Long call(Long aLong, Long aLong2) {
                                return aLong / 1000 - aLong2;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                if (aLong >= 0) {
                                    btn_copy_token.setText(getString(R.string.copy_token, aLong));
                                } else {
                                    refresh();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
            }

            CopyTokenDialogFragment.newInstance(mCurrentWithdrawal.token.tokenContent).show(getSupportFragmentManager(), "CopyTokenDialogFragment");
        } else {
            btn_go_withdrawal.setVisibility(View.VISIBLE);
            ll_current_withdrawal.setVisibility(View.GONE);
        }
    }
}
