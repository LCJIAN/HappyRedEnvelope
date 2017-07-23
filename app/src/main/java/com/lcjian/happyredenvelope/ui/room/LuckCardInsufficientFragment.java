package com.lcjian.happyredenvelope.ui.room;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseDialogFragment;
import com.lcjian.happyredenvelope.Constants;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.FreeLuckCard;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.ui.mine.BuyLuckCardActivity;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.video.VideoAd;
import com.youdao.sdk.video.YouDaoVideo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LuckCardInsufficientFragment extends BaseDialogFragment implements View.OnClickListener {

    @BindView(R.id.btn_close)
    ImageButton btn_close;
    @BindView(R.id.btn_receive_free)
    Button btn_receive_free;
    @BindView(R.id.btn_buy_now)
    Button btn_buy_now;
    Unbinder unbinder;
    private YouDaoVideo youDaoVideo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_luck_card_insuficient, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btn_close.setOnClickListener(this);
        btn_receive_free.setOnClickListener(this);
        btn_buy_now.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        if (youDaoVideo != null) {
            youDaoVideo.destroy();
        }
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                mRxBus.send("close_room_activity");
                break;
            case R.id.btn_receive_free:
                loadAdvertisement();
                break;
            case R.id.btn_buy_now:
                startActivity(new Intent(getContext(), BuyLuckCardActivity.class));
                break;
            default:
                break;
        }
    }

    private void loadAdvertisement() {
        if (youDaoVideo != null) {
            youDaoVideo.destroy();
        }
        youDaoVideo = new YouDaoVideo(Constants.YD_VIDEO_AD,
                String.valueOf(mUserInfoSp.getLong("user_id", 0)), getActivity(), new YouDaoVideo.YouDaoVideoListener() {

            @Override
            public void onFail(NativeErrorCode errorCode) {
                Toast.makeText(App.getInstance(), "广告加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(VideoAd ad) {
                btn_receive_free.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        youDaoVideo.play();
                    }
                }, 1000);
                Toast.makeText(App.getInstance(), "广告加载成功", Toast.LENGTH_SHORT).show();
            }
        });

        YouDaoVideo.YouDaoVideoEventListener youDaoVideoEventListener = new YouDaoVideo.YouDaoVideoEventListener() {
            @Override
            public void onReady(VideoAd ad) {
                Toast.makeText(App.getInstance(), "视频预加载成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VideoAd ad, NativeErrorCode errorCode) {
                Toast.makeText(App.getInstance(), "视频预加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlayStart(VideoAd ad) {
                Toast.makeText(App.getInstance(), "视频开始播放", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlayEnd(VideoAd ad, String userId) {
                ad.isReady();
                mRestAPI.redEnvelopeService().getFreeLuckCard(mUserInfoSp.getLong("user_id", 0))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseData<FreeLuckCard>>() {
                            @Override
                            public void call(ResponseData<FreeLuckCard> freeLuckCardResponseData) {
                                if (freeLuckCardResponseData.code == 0) {
                                    Toast.makeText(App.getInstance(),
                                            getString(R.string.receive_luck_card_success, freeLuckCardResponseData.data.fukaTime),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
            }

            @Override
            public void onImpression(VideoAd ad) {
                ad.isReady();
                Toast.makeText(App.getInstance(), "广告被展示", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClick(VideoAd ad) {
                Toast.makeText(App.getInstance(), "广告被点击", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlayStop(VideoAd ad) {
                Toast.makeText(App.getInstance(), "视频播被关闭", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClosed(VideoAd ad) {
                Toast.makeText(App.getInstance(), "视频被关闭", Toast.LENGTH_SHORT).show();
            }
        };
        youDaoVideo.setmYouDaoVideoEventListener(youDaoVideoEventListener);

        RequestParameters requestParameters = new RequestParameters.Builder()
                .location(null).build();
        youDaoVideo.loadAd(requestParameters);
    }
}
