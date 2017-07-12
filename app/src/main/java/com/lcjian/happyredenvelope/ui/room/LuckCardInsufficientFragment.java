package com.lcjian.happyredenvelope.ui.room;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.lcjian.happyredenvelope.BaseDialogFragment;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.FreeLuckCard;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.ui.mine.BuyLuckCardActivity;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_luck_card_insuficient, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btn_close.setOnClickListener(this);
        btn_receive_free.setOnClickListener(this);
        btn_buy_now.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
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
                mRestAPI.redEnvelopeService().getFreeLuckCard(mUserInfoSp.getLong("user_id", 0))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseData<FreeLuckCard>>() {
                            @Override
                            public void call(ResponseData<FreeLuckCard> freeLuckCardResponseData) {

                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
                dismiss();
                break;
            case R.id.btn_buy_now:
                startActivity(new Intent(getContext(), BuyLuckCardActivity.class));
                break;
            default:
                break;
        }
    }
}
