package com.lcjian.happyredenvelope.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.Room;
import com.lcjian.happyredenvelope.data.entity.User;
import com.lcjian.happyredenvelope.ui.SignInDialogFragment;
import com.lcjian.happyredenvelope.ui.room.RoomActivity;
import com.lcjian.happyredenvelope.ui.room.UserActivity;
import com.lcjian.lib.util.common.DateUtils;

import java.sql.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_room_id)
    TextView tv_room_id;
    @BindView(R.id.tv_room_create_time)
    TextView tv_room_create_time;
    @BindView(R.id.iv_room_avatar)
    ImageView iv_room_avatar;
    @BindView(R.id.tv_room_name)
    TextView tv_room_name;
    @BindView(R.id.tv_room_red_envelope_count)
    TextView tv_room_red_envelope_count;
    @BindView(R.id.tv_room_user_count)
    TextView tv_room_user_count;
    @BindView(R.id.ll_room_users)
    LinearLayout ll_room_users;
    @BindView(R.id.tv_no_room_billboard_yet)
    TextView tv_no_room_billboard_yet;
    @BindView(R.id.btn_room_action)
    Button btn_room_action;

    private Room room;

    private SharedPreferences mUserInfoSp;

    private FragmentManager mFragmentManager;

    public RoomViewHolder(ViewGroup parent, SharedPreferences userInfoSp, FragmentManager fragmentManager) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item, parent, false));
        ButterKnife.bind(this, this.itemView);
        this.mUserInfoSp = userInfoSp;
        this.mFragmentManager = fragmentManager;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        btn_room_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserInfoSp.getBoolean("signed_in", false)) {
                    v.getContext().startActivity(new Intent(v.getContext(), RoomActivity.class)
                            .putExtra("room_id", room.id));
                } else {
                    new SignInDialogFragment().show(mFragmentManager, "SignInDialogFragment");
                }
            }
        });
    }

    public void bindTo(Room room) {
        this.room = room;
        Context context = itemView.getContext();
        tv_room_id.setText(context.getString(R.string.room_id, room.id));
        tv_room_create_time.setText(DateUtils.convertDateToStr(new Date(room.createTime), "yyyy-MM-dd HH:mm"));
        Glide.with(context)
                .load(room.pic)
                .apply(Global.roomAvatar)
                .transition(Global.crossFade)
                .into(iv_room_avatar);
        tv_room_name.setText(room.name);
        tv_room_red_envelope_count.setText(context.getString(R.string.room_red_envelope_count, room.hongBaoCount));
        tv_room_user_count.setText(context.getString(R.string.room_user_count, room.nowNumber));

        if (room.state == 1) {
            btn_room_action.setBackgroundResource(R.drawable.shape_button_medium_green_bg);
            btn_room_action.setText(R.string.enter);
        } else if (room.state == 2) {
            btn_room_action.setBackgroundResource(R.drawable.shape_button_medium_bg);
            btn_room_action.setText(R.string.start_snatch);
        } else if (room.state == 3) {
            btn_room_action.setBackgroundResource(R.drawable.shape_button_medium_gray_bg);
            btn_room_action.setText(R.string.locked);
        } else {
            btn_room_action.setBackgroundResource(R.drawable.shape_button_medium_green_bg);
            btn_room_action.setText(R.string.enter);
        }
        if (room.rankInfo == null || room.rankInfo.isEmpty()) {
            ll_room_users.setVisibility(View.GONE);
            tv_no_room_billboard_yet.setVisibility(View.VISIBLE);
        } else {
            ll_room_users.setVisibility(View.VISIBLE);
            tv_no_room_billboard_yet.setVisibility(View.GONE);
            ll_room_users.removeAllViews();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            for (int i = 0; i < room.rankInfo.size(); i++) {
                final User user = room.rankInfo.get(i);
                View view = layoutInflater.inflate(R.layout.room_item_billboard_item, ll_room_users, false);
                ImageView iv_user_billboard_bg = (ImageView) view.findViewById(R.id.iv_user_billboard_bg);
                ImageView iv_user_avatar = (ImageView) view.findViewById(R.id.iv_user_avatar);
                ImageView iv_user_billboard_fg = (ImageView) view.findViewById(R.id.iv_user_billboard_fg);
                if (i == 0 || i == 1 || i == 2) {
                    iv_user_billboard_bg.setVisibility(View.VISIBLE);
                    iv_user_billboard_fg.setVisibility(View.VISIBLE);
                    if (i == 0) {
                        iv_user_billboard_bg.setBackgroundResource(R.drawable.number_one_bg);
                        iv_user_billboard_fg.setBackgroundResource(R.drawable.ic_number_one);
                    }
                    if (i == 1) {
                        iv_user_billboard_bg.setBackgroundResource(R.drawable.number_two_bg);
                        iv_user_billboard_fg.setBackgroundResource(R.drawable.ic_number_two);
                    }
                    if (i == 2) {
                        iv_user_billboard_bg.setBackgroundResource(R.drawable.number_three_bg);
                        iv_user_billboard_fg.setBackgroundResource(R.drawable.ic_number_three);
                    }
                } else {
                    iv_user_billboard_bg.setVisibility(View.GONE);
                    iv_user_billboard_fg.setVisibility(View.GONE);
                }
                Glide.with(context)
                        .load(user.userHeadimg)
                        .apply(Global.userAvatar)
                        .transition(Global.crossFade)
                        .into(iv_user_avatar);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.getContext().startActivity(new Intent(v.getContext(), UserActivity.class)
                                .putExtra("user_id", user.userId));
                    }
                });
                ll_room_users.addView(view);
            }
        }
    }

}