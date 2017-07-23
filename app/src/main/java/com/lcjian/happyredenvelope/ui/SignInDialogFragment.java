package com.lcjian.happyredenvelope.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseDialogFragment;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.User;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
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

public class SignInDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    @BindView(R.id.btn_close)
    ImageButton btn_close;
    @BindView(R.id.btn_sign_in)
    Button btn_sign_in;
    Unbinder unbinder;

    private Subscription mSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btn_close.setOnClickListener(this);
        btn_sign_in.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_sign_in:
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
                break;
            default:
                break;
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
                                    .putFloat("user_balance", userResponseData.data.userBalance)
                                    .putBoolean("signed_in", true)
                                    .apply();
                            PushAgent.getInstance(App.getInstance())
                                    .addAlias(
                                            String.valueOf(userResponseData.data.userId),
                                            "we_chat",
                                            new UTrack.ICallBack() {
                                                @Override
                                                public void onMessage(boolean isSuccess, String message) {
                                                }
                                            });
                            dismiss();
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
}
