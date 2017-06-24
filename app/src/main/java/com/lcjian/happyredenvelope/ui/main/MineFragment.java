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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.BaseFragment;
import com.lcjian.happyredenvelope.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MineFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.btn_go_message) ImageButton btn_go_message;
    @BindView(R.id.iv_not_signed_in) ImageView iv_not_signed_in;
    @BindView(R.id.btn_sign_in) Button btn_sign_in;
    @BindView(R.id.fl_not_signed_in) FrameLayout fl_not_signed_in;

    @BindView(R.id.iv_user_avatar) ImageView iv_user_avatar;
    @BindView(R.id.tv_user_name) TextView tv_user_name;
    @BindView(R.id.tv_time_left) TextView tv_time_left;
    @BindView(R.id.tv_balance) TextView tv_balance;
    @BindView(R.id.btn_withdrawal) TextView btn_withdrawal;
    @BindView(R.id.rl_signed_in) RelativeLayout rl_signed_in;

    @BindView(R.id.tv_buy_lucky_card) TextView tv_buy_lucky_card;
    @BindView(R.id.tv_buy_vip) TextView tv_buy_vip;
    @BindView(R.id.tv_view_history) TextView tv_view_history;
    @BindView(R.id.tv_my_coupon) TextView tv_my_coupon;
    @BindView(R.id.tv_helping_center) TextView tv_helping_center;
    @BindView(R.id.tv_red_envelope_sponsor) TextView tv_red_envelope_sponsor;
    @BindView(R.id.tv_settings) TextView tv_settings;

    Unbinder unbinder;

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
        if (mUserInfoSp.getBoolean("signed_in", false)) {
            rl_signed_in.setVisibility(View.VISIBLE);
            fl_not_signed_in.setVisibility(View.GONE);
        } else {
            rl_signed_in.setVisibility(View.GONE);
            fl_not_signed_in.setVisibility(View.VISIBLE);

            btn_go_message.setVisibility(View.GONE);
            tv_buy_lucky_card.setVisibility(View.GONE);
            tv_buy_vip.setVisibility(View.GONE);
            tv_view_history.setVisibility(View.GONE);
            tv_my_coupon.setVisibility(View.GONE);
            tv_helping_center.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.not_signed_in_bg).into(iv_not_signed_in);
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in: {
                mUserInfoSp.edit().putBoolean("signed_in", true).apply();
            }
            break;
            case R.id.tv_buy_lucky_card: {
            }
            break;
            default:
                break;
        }
    }
}
