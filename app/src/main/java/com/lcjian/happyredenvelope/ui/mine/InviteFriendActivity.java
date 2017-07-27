package com.lcjian.happyredenvelope.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.ShareInfo;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class InviteFriendActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;
    @BindView(R.id.tv_your_invite_id)
    TextView tv_your_invite_id;
    @BindView(R.id.btn_invite_now)
    Button btn_invite_now;
    @BindView(R.id.iv_your_invite_qr_code)
    ImageView iv_your_invite_qr_code;

    private ShareInfo mShareInfo;

    private Subscription mSUbSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_invite_friend);
        ButterKnife.bind(this);

        tv_top_bar_title.setText(R.string.invite_friend_2);
        btn_top_bar_left.setOnClickListener(this);
        btn_invite_now.setOnClickListener(this);
        tv_top_bar_right.setVisibility(View.GONE);

        mSUbSubscription = mRestAPI.redEnvelopeService().getShareInfo(mUserInfoSp.getLong("user_id", 0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<ShareInfo>>() {
                    @Override
                    public void call(ResponseData<ShareInfo> shareInfoResponseData) {
                        if (shareInfoResponseData.code == 0) {
                            mShareInfo = shareInfoResponseData.data;
                            tv_your_invite_id.setText(mShareInfo.inviteCode);
                            Glide.with(InviteFriendActivity.this)
                                    .load(mShareInfo.inviteQrcode)
                                    .transition(Global.crossFade)
                                    .into(iv_your_invite_qr_code);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (mSUbSubscription != null) {
            mSUbSubscription.unsubscribe();
        }
        UMShareAPI.get(this).release();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_top_bar_left:
                onBackPressed();
                break;
            case R.id.btn_invite_now:
                new ShareAction(this)
                        .withMedia(new UMWeb(
                                (mShareInfo.shareInfo.link != null && !mShareInfo.shareInfo.link.startsWith("http")
                                        ? "http://" + mShareInfo.shareInfo.link
                                        : mShareInfo.shareInfo.link),
                                mShareInfo.shareInfo.title,
                                mShareInfo.shareInfo.content,
                                new UMImage(this, R.mipmap.ic_launcher)))
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(new UMShareListener() {
                            @Override
                            public void onStart(SHARE_MEDIA share_media) {
                            }

                            @Override
                            public void onResult(SHARE_MEDIA share_media) {
                            }

                            @Override
                            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                throwable.printStackTrace();
                            }

                            @Override
                            public void onCancel(SHARE_MEDIA share_media) {
                            }
                        }).share();
                break;
            default:
                break;
        }
    }
}
