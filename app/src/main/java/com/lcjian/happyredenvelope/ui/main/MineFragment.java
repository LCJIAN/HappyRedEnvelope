package com.lcjian.happyredenvelope.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseFragment;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.User;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

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
    @BindView(R.id.rl_signed_in)
    RelativeLayout rl_signed_in;

    @BindView(R.id.tv_buy_lucky_card)
    TextView tv_buy_lucky_card;
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
        btn_sign_in.setOnClickListener(this);
        tv_buy_lucky_card.setOnClickListener(this);
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
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in: {
                signIn("1234567890", "AndroidTest", "https://mfs.ykimg.com/051400005885B69967BC3C1C580523FD",
                        "China", "cq", "cq", "1");
                UMShareAPI.get(getContext()).getPlatformInfo(getActivity(), SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        Toast.makeText(App.getInstance(), "Authorize succeeded", Toast.LENGTH_SHORT).show();
                        signIn(map.get("openid"), map.get("name"), map.get("iconurl"),
                                map.get("country"), map.get("province"), map.get("city"), map.get("gender"));
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
            case R.id.tv_buy_lucky_card: {
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
            tv_buy_lucky_card.setVisibility(View.VISIBLE);
            tv_buy_vip.setVisibility(View.VISIBLE);
            tv_view_history.setVisibility(View.VISIBLE);
            tv_my_coupon.setVisibility(View.VISIBLE);
            tv_helping_center.setVisibility(View.VISIBLE);

            fl_not_signed_in.setVisibility(View.GONE);

            Glide.with(this)
                    .load(mUserInfoSp.getString("user_avatar", ""))
                    .apply(RequestOptions.placeholderOf(R.drawable.shape_user_no_avatar_bg).circleCrop())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(iv_user_avatar);
        } else {
            fl_not_signed_in.setVisibility(View.VISIBLE);

            rl_signed_in.setVisibility(View.GONE);
            btn_go_message.setVisibility(View.GONE);
            tv_buy_lucky_card.setVisibility(View.GONE);
            tv_buy_vip.setVisibility(View.GONE);
            tv_view_history.setVisibility(View.GONE);
            tv_my_coupon.setVisibility(View.GONE);
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
                                    .putBoolean("signed_in", true)
                                    .apply();
                            setupSignedIn();
                        } else {
                            Toast.makeText(getActivity(), userResponseData.message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
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

                .putLong("user_id", 122)
                .putLong("user_create_time", 0)
                .putLong("user_last_sign_in_time", 0)
                .putString("user_avatar", avatar)
                .putString("user_nick_name", name)
                .putString("user_open_id", openId)
                .putBoolean("signed_in", true)
                .apply();
        setupSignedIn();
    }
}
