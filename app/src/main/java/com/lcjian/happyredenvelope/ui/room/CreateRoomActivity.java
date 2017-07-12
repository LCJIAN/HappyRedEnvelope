package com.lcjian.happyredenvelope.ui.room;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.RoomIdInfo;
import com.lcjian.happyredenvelope.data.entity.VipInfo;
import com.lcjian.lib.util.ImageChooseHelper;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CreateRoomActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;
    @BindView(R.id.iv_room_avatar)
    ImageButton iv_room_avatar;
    @BindView(R.id.et_room_name)
    EditText et_room_name;
    @BindView(R.id.et_room_announcement)
    EditText et_room_announcement;
    @BindView(R.id.btn_create_room)
    Button btn_create_room;

    private ImageChooseHelper mImageChooseHelper;

    private String mPath;

    private Subscription mSubscription;
    private Subscription mSubscription2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        ButterKnife.bind(this);

        tv_top_bar_title.setText(R.string.create_room);
        tv_top_bar_right.setVisibility(View.GONE);

        btn_top_bar_left.setOnClickListener(this);
        iv_room_avatar.setOnClickListener(this);
        btn_create_room.setOnClickListener(this);

        mImageChooseHelper = ImageChooseHelper.create(this, new ImageChooseHelper.Callback() {
            @Override
            public void onResult(String path) {
                mPath = path;
                Glide.with(CreateRoomActivity.this)
                        .load(mPath)
                        .apply(Global.roomAvatar)
                        .transition(Global.crossFade)
                        .into(iv_room_avatar);
            }
        });

        mSubscription = mRestAPI.redEnvelopeService().isVip(mUserInfoSp.getLong("user_id", 0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<VipInfo>>() {
                    @Override
                    public void call(ResponseData<VipInfo> vipInfoResponseData) {
                        if (vipInfoResponseData.code == 0) {
                            if (!vipInfoResponseData.data.isvip) {
                                Toast.makeText(App.getInstance(), R.string.only_vip_can_create_room, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(App.getInstance(), vipInfoResponseData.msg, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageChooseHelper.handleActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        if (mSubscription2 != null) {
            mSubscription2.unsubscribe();
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
            case R.id.iv_room_avatar: {
                mImageChooseHelper.startGet(true);
            }
            break;
            case R.id.btn_create_room: {
                if (TextUtils.isEmpty(et_room_name.getText())) {
                    return;
                }
                if (TextUtils.isEmpty(et_room_announcement.getText())) {
                    return;
                }
                if (TextUtils.isEmpty(mPath)) {
                    return;
                }
                File file = new File(mPath);
                mSubscription2 = mRestAPI.redEnvelopeService().createVipRoom(mUserInfoSp.getLong("user_id", 0),
                        et_room_name.getText().toString(), et_room_announcement.getText().toString(),
                        MultipartBody.Part.createFormData("icon",
                                file.getName(),
                                RequestBody.create(MediaType.parse("image/*"), file)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseData<RoomIdInfo>>() {
                            @Override
                            public void call(ResponseData<RoomIdInfo> longResponseData) {
                                if (longResponseData.code == 0) {
                                    mRxBus.send("refresh_room_list");
                                    finish();
                                } else {
                                    Toast.makeText(App.getInstance(), longResponseData.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            break;
            default:
                break;
        }
    }
}
