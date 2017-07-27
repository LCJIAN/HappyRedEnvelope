package com.lcjian.happyredenvelope.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseFragment;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.LeftTimeInfo;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.User;
import com.lcjian.happyredenvelope.data.entity.UserSummary;
import com.lcjian.happyredenvelope.ui.mine.BuyVipActivity;
import com.lcjian.happyredenvelope.ui.mine.MessageActivity;
import com.lcjian.happyredenvelope.ui.mine.RedEnvelopeHistoriesActivity;
import com.lcjian.happyredenvelope.ui.mine.SetInviteFragment;
import com.lcjian.happyredenvelope.ui.mine.SettingsActivity;
import com.lcjian.happyredenvelope.ui.mine.UserLuckCardActivity;
import com.lcjian.happyredenvelope.ui.mine.ViewHistoriesActivity;
import com.lcjian.happyredenvelope.ui.web.WebViewActivity;
import com.lcjian.happyredenvelope.ui.withdrawal.WithdrawalActivity;
import com.lcjian.lib.util.common.StringUtils;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MineFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.btn_go_message)
    ImageButton btn_go_message;
    @BindView(R.id.tv_my_red_envelope_history)
    TextView tv_my_red_envelope_history;
    @BindView(R.id.iv_not_signed_in)
    ImageView iv_not_signed_in;
    @BindView(R.id.btn_sign_in)
    Button btn_sign_in;
    @BindView(R.id.fl_not_signed_in)
    FrameLayout fl_not_signed_in;

    @BindView(R.id.iv_user_avatar)
    ImageView iv_user_avatar;
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;
    @BindView(R.id.tv_time_left)
    TextView tv_time_left;
    @BindView(R.id.tv_balance)
    TextView tv_balance;
    @BindView(R.id.btn_withdrawal)
    TextView btn_withdrawal;
    @BindView(R.id.tv_day_count)
    TextView tv_day_count;
    @BindView(R.id.tv_week_count)
    TextView tv_week_count;
    @BindView(R.id.tv_month_count)
    TextView tv_month_count;
    @BindView(R.id.tv_total_count)
    TextView tv_total_count;
    @BindView(R.id.rl_signed_in)
    RelativeLayout rl_signed_in;

    @BindView(R.id.tv_buy_luck_card)
    TextView tv_buy_luck_card;
    @BindView(R.id.tv_left_luck_card_total)
    TextView tv_left_luck_card_total;
    @BindView(R.id.tv_buy_vip)
    TextView tv_buy_vip;
    @BindView(R.id.tv_view_history)
    TextView tv_view_history;
    @BindView(R.id.tv_my_coupon)
    TextView tv_my_coupon;
    @BindView(R.id.tv_helping_center)
    TextView tv_helping_center;
    @BindView(R.id.tv_red_envelope_sponsor)
    TextView tv_red_envelope_sponsor;
    @BindView(R.id.tv_settings)
    TextView tv_settings;

    Unbinder unbinder;

    private Subscription mSubscription;
    private Subscription mSubscription2;
    private Subscription mSubscription3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btn_go_message.setOnClickListener(this);
        tv_my_red_envelope_history.setOnClickListener(this);
        btn_sign_in.setOnClickListener(this);
        btn_withdrawal.setOnClickListener(this);
        tv_buy_luck_card.setOnClickListener(this);
        tv_buy_vip.setOnClickListener(this);
        tv_view_history.setOnClickListener(this);
        tv_my_coupon.setOnClickListener(this);
        tv_helping_center.setOnClickListener(this);
        tv_red_envelope_sponsor.setOnClickListener(this);
        tv_settings.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        setupSignedIn();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        if (mSubscription2 != null) {
            mSubscription2.unsubscribe();
        }
        if (mSubscription3 != null) {
            mSubscription3.unsubscribe();
        }
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in: {
                UMShareAPI.get(getContext()).getPlatformInfo(getActivity(), SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        Toast.makeText(App.getInstance(), "Authorize succeeded", Toast.LENGTH_SHORT).show();
                        signIn(map.get("openid"), map.get("name"), map.get("iconurl"),
                                map.get("country"), map.get("province"), map.get("city"),
                                TextUtils.equals("男", map.get("gender")) ? "1" : (TextUtils.equals("女", map.get("gender")) ? "2" : "0"));
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                        Toast.makeText(App.getInstance(), "Authorize failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {
                        Toast.makeText(App.getInstance(), "Authorize canceled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            break;
            case R.id.btn_withdrawal: {
                startActivity(new Intent(getContext(), WithdrawalActivity.class));
            }
            break;
            case R.id.tv_buy_luck_card: {
                startActivity(new Intent(getContext(), UserLuckCardActivity.class));
            }
            break;
            case R.id.tv_buy_vip: {
                startActivity(new Intent(getContext(), BuyVipActivity.class));
            }
            break;
            case R.id.tv_view_history: {
                startActivity(new Intent(getContext(), ViewHistoriesActivity.class));
            }
            break;
            case R.id.tv_my_red_envelope_history: {
                startActivity(new Intent(getContext(), RedEnvelopeHistoriesActivity.class));
            }
            break;
            case R.id.tv_helping_center: {
                startActivity(new Intent(getContext(), WebViewActivity.class)
                        .putExtra("url", mUserInfoSp.getString("helping_center_link", "")));
            }
            break;
            case R.id.tv_red_envelope_sponsor: {
                startActivity(new Intent(getContext(), WebViewActivity.class)
                        .putExtra("url", mUserInfoSp.getString("red_envelop_sponsor_link", "")));
            }
            break;
            case R.id.tv_settings: {
                startActivity(new Intent(getContext(), SettingsActivity.class));
            }
            break;
            case R.id.btn_go_message: {
                startActivity(new Intent(getContext(), MessageActivity.class));
            }
            break;
            default:
                break;
        }
    }

    private void setupSignedIn() {
        if (mUserInfoSp.getBoolean("signed_in", false)) {
            rl_signed_in.setVisibility(View.VISIBLE);
            btn_go_message.setVisibility(View.VISIBLE);
            tv_my_red_envelope_history.setVisibility(View.VISIBLE);
            tv_buy_luck_card.setVisibility(View.VISIBLE);
            tv_buy_vip.setVisibility(View.VISIBLE);
            tv_view_history.setVisibility(View.VISIBLE);
//            tv_my_coupon.setVisibility(View.VISIBLE);
            tv_helping_center.setVisibility(View.VISIBLE);

            fl_not_signed_in.setVisibility(View.GONE);

            Glide.with(this)
                    .load(mUserInfoSp.getString("user_avatar", ""))
                    .apply(Global.userAvatar)
                    .transition(Global.crossFade)
                    .into(iv_user_avatar);
            tv_user_name.setText(mUserInfoSp.getString("user_nick_name", ""));
            getUserSummary();
            getLeftTime();
        } else {
            fl_not_signed_in.setVisibility(View.VISIBLE);

            rl_signed_in.setVisibility(View.GONE);
            btn_go_message.setVisibility(View.GONE);
            tv_my_red_envelope_history.setVisibility(View.GONE);
            tv_buy_luck_card.setVisibility(View.GONE);
            tv_buy_vip.setVisibility(View.GONE);
            tv_view_history.setVisibility(View.GONE);
//            tv_my_coupon.setVisibility(View.GONE);
            tv_helping_center.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.not_signed_in_bg).into(iv_not_signed_in);
        }
    }

    private void signIn(String openId, String name, String avatar, String country, String province, String city, String sex) {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mSubscription = mRestAPI.redEnvelopeService()
                .register(openId, name, avatar, country, province, city, sex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<User>>() {
                    @Override
                    public void call(ResponseData<User> userResponseData) {
                        if (userResponseData.code == 0) {
                            mUserInfoSp.edit()
                                    .putLong("user_id", userResponseData.data.userId)
                                    .putLong("user_create_time", userResponseData.data.userCreatetime)
                                    .putLong("user_last_sign_in_time", userResponseData.data.userLastlogintime)
                                    .putString("user_avatar", userResponseData.data.userHeadimg)
                                    .putString("user_nick_name", userResponseData.data.userNickname)
                                    .putString("user_open_id", userResponseData.data.userOpenid)
                                    .putString("user_country", userResponseData.data.userCountry)
                                    .putInt("user_sex", userResponseData.data.userSex)
                                    .putBoolean("signed_in", true)
                                    .apply();
                            PushAgent.getInstance(App.getInstance())
                                    .addAlias(
                                            String.valueOf(userResponseData.data.userId),
                                            "uid",
                                            new UTrack.ICallBack() {
                                                @Override
                                                public void onMessage(boolean isSuccess, String message) {
                                                }
                                            });
                            setupSignedIn();
                            if (!mSettingSp.getBoolean("invited", false)) {
                                new SetInviteFragment().show(getFragmentManager(), "SetInviteFragment");
                            }
                        } else {
                            Toast.makeText(App.getInstance(), userResponseData.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        mUserInfoSp.edit()
                .putString("openid", openId)
                .putString("name", name)
                .putString("avatar", avatar)
                .putString("country", country)
                .putString("province", province)
                .putString("city", city)
                .putString("sex", sex)
                .apply();
    }

    private void getUserSummary() {
        if (mSubscription2 != null) {
            mSubscription2.unsubscribe();
        }
        mSubscription2 = mRestAPI.redEnvelopeService().getUserSummary(mUserInfoSp.getLong("user_id", 0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<UserSummary>>() {
                    @Override
                    public void call(ResponseData<UserSummary> userSummaryResponseData) {
                        if (userSummaryResponseData.code == 0) {
                            tv_day_count.setText(String.valueOf(userSummaryResponseData.data.dayCount));
                            tv_week_count.setText(String.valueOf(userSummaryResponseData.data.weekCount));
                            tv_month_count.setText(String.valueOf(userSummaryResponseData.data.monthCount));
                            tv_total_count.setText(String.valueOf(userSummaryResponseData.data.totalCount));
                            tv_balance.setText(String.format(Locale.getDefault(), "%s%s",
                                    "￥", new DecimalFormat("0.00").format(userSummaryResponseData.data.hblUser.userBalance)));
                            if (userSummaryResponseData.data.hblUser.userVipendtime > System.currentTimeMillis()) {
                                tv_user_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_vip, 0);
                            } else {
                                tv_user_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            }
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

    private void getLeftTime() {
        if (mSubscription3 != null) {
            mSubscription3.unsubscribe();
        }
        mSubscription3 = mRestAPI.redEnvelopeService().getLuckCardTotalLeftTime(mUserInfoSp.getLong("user_id", 0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<LeftTimeInfo>>() {
                    @Override
                    public void call(ResponseData<LeftTimeInfo> leftTimeInfoResponseData) {
                        if (leftTimeInfoResponseData.code == 0) {
                            tv_time_left.setText(StringUtils.stringForTime((int) leftTimeInfoResponseData.data.totalLeftTime * 1000));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
