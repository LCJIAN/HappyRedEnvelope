package com.lcjian.happyredenvelope.ui.room;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseDialogFragment;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.Message;
import com.lcjian.happyredenvelope.data.entity.ResponseData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RedEnvelopeOpenFragment extends BaseDialogFragment implements View.OnClickListener {

    @BindView(R.id.ll_red_envelope_open)
    LinearLayout ll_red_envelope_open;
    @BindView(R.id.btn_close)
    ImageButton btn_close;
    @BindView(R.id.iv_brand_avatar)
    ImageView iv_brand_avatar;
    @BindView(R.id.tv_brand_name)
    TextView tv_brand_name;
    @BindView(R.id.tv_brand_des)
    TextView tv_brand_des;
    Unbinder unbinder;

    private Message mMessage;

    private ResponseData<Object> responseData;

    public static RedEnvelopeOpenFragment newInstance(Message message) {
        RedEnvelopeOpenFragment fragment = new RedEnvelopeOpenFragment();
        Bundle args = new Bundle();
        args.putSerializable("message", message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMessage = (Message) getArguments().getSerializable("message");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_red_envelope_open, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (mMessage.supportInfo != null) {
            Glide.with(getContext())
                    .load(mMessage.supportInfo.icon)
                    .apply(Global.userAvatar)
                    .transition(Global.crossFade)
                    .into(iv_brand_avatar);
            tv_brand_name.setText(mMessage.supportInfo.name);
            tv_brand_des.setText(mMessage.supportInfo.desc);
        }

        ll_red_envelope_open.setOnClickListener(this);
        btn_close.setOnClickListener(this);

        if (mMessage.type == 3) {
            mRestAPI.redEnvelopeService().openRedEnvelope(mUserInfoSp.getLong("user_id", 0), mMessage.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ResponseData<Object>>() {
                        @Override
                        public void call(ResponseData<Object> objectResponseData) {
                            responseData = objectResponseData;
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_red_envelope_open:
                if (mMessage.type == 4) {
                    startActivity(new Intent(v.getContext(), RedEnvelopeSnatchedEmptyActivity.class));
                } else if (mMessage.type == 5) {
                    startActivity(new Intent(v.getContext(), RedEnvelopeSnatchedFakeActivity.class));
                } else if (mMessage.type == 3) {
                    if (responseData != null) {
                        if (responseData.code == 0) {
                            startActivity(new Intent(v.getContext(), RedEnvelopeSnatchedSuccessActivity.class)
                                    .putExtra("msg_id", mMessage.id));
                        } else {
                            RedEnvelopeSnatchFailedFragment.newInstance(responseData.msg)
                                    .show(getFragmentManager(), "RedEnvelopeSnatchFailedFragment");
                        }
                    }
                }
                dismiss();
                break;
            case R.id.btn_close:
                dismiss();
                break;
            default:
                break;
        }
    }
}
