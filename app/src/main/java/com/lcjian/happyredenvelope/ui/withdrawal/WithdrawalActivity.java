package com.lcjian.happyredenvelope.ui.withdrawal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.lcjian.happyredenvelope.data.entity.UserSummary;
import com.lcjian.happyredenvelope.data.entity.VipInfo;
import com.lcjian.happyredenvelope.data.entity.Withdrawal;
import com.lcjian.happyredenvelope.ui.web.WebViewActivity;

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
import rx.functions.Func4;
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
    private UserSummary mUserSummary;

    private ProgressDialog mProgressDialog;

    private Subscription mSubscription;
    private Subscription mSubscription2;
    private Subscription mSubscription3;

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

        btn_go_withdrawal.setVisibility(View.INVISIBLE);
        ll_current_withdrawal.setVisibility(View.INVISIBLE);

        btn_top_bar_left.setOnClickListener(this);
        tv_top_bar_right.setOnClickListener(this);
        tv_my_balance.setOnClickListener(this);
        btn_go_withdrawal.setOnClickListener(this);
        btn_copy_token.setOnClickListener(this);
        tv_withdrawal_tutorial.setOnClickListener(this);

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
                float balance = mUserSummary.hblUser.userBalance;
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
                startActivity(new Intent(v.getContext(), WebViewActivity.class)
                        .putExtra("url", mUserInfoSp.getString("withdraw_tutorial_link", "")));
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
                mRestAPI.redEnvelopeService().isVip(mUserInfoSp.getLong("user_id", 0)),
                mRestAPI.redEnvelopeService().getUserSummary(mUserInfoSp.getLong("user_id", 0)),
                mRestAPI.redEnvelopeService().isFirstWithdrawal(mUserInfoSp.getLong("user_id", 0)),
                mRestAPI.redEnvelopeService().getCurrentWithdrawals(mUserInfoSp.getLong("user_id", 0)),
                new Func4<ResponseData<VipInfo>, ResponseData<UserSummary>, ResponseData<Boolean>, ResponseData<List<Withdrawal>>, WithdrawalPredicate>() {
                    @Override
                    public WithdrawalPredicate call(ResponseData<VipInfo> vipInfoResponseData,
                                                    ResponseData<UserSummary> userSummaryResponseData,
                                                    ResponseData<Boolean> booleanResponseData,
                                                    ResponseData<List<Withdrawal>> listResponseData) {
                        return new WithdrawalPredicate(vipInfoResponseData.data,
                                userSummaryResponseData.data,
                                booleanResponseData.data,
                                listResponseData.data
                        );
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WithdrawalPredicate>() {
                    @Override
                    public void call(WithdrawalPredicate withdrawalPredicate) {
                        mProgressDialog.dismiss();
                        mIsVip = withdrawalPredicate.vipInfo.isvip;
                        mIsFirstWithdrawal = withdrawalPredicate.isFirstWithdrawal;
                        mUserSummary = withdrawalPredicate.userSummary;
                        mCurrentWithdrawal = withdrawalPredicate.withdrawals == null || withdrawalPredicate.withdrawals.isEmpty()
                                ? null : withdrawalPredicate.withdrawals.get(0);

                        tv_my_balance.setText(String.format(Locale.getDefault(), "%s%s",
                                "￥", new DecimalFormat("0.00").format(mUserSummary.hblUser.userBalance)));
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

    private static class WithdrawalPredicate {
        private VipInfo vipInfo;
        private UserSummary userSummary;
        private Boolean isFirstWithdrawal;
        private List<Withdrawal> withdrawals;

        private WithdrawalPredicate(VipInfo vipInfo,
                                    UserSummary userSummary,
                                    Boolean isFirstWithdrawal,
                                    List<Withdrawal> withdrawals) {
            this.vipInfo = vipInfo;
            this.userSummary = userSummary;
            this.isFirstWithdrawal = isFirstWithdrawal;
            this.withdrawals = withdrawals;
        }
    }
}
