package com.lcjian.happyredenvelope.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.Room;
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

    Room room;

    public RoomViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item, parent, false));
        ButterKnife.bind(this, this.itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                .apply(RequestOptions.placeholderOf(R.drawable.shape_room_no_avatar_bg).transform(new RoundedCorners(4)))
                .transition(DrawableTransitionOptions.withCrossFade())
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
        }
        if (room.rankInfo == null || room.rankInfo.isEmpty()) {
            ll_room_users.setVisibility(View.GONE);
            tv_no_room_billboard_yet.setVisibility(View.VISIBLE);
        } else {
            ll_room_users.setVisibility(View.VISIBLE);
            tv_no_room_billboard_yet.setVisibility(View.GONE);
        }
    }

}