package com.lcjian.happyredenvelope.ui.room;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.db.ta.sdk.TMNaTmView;
import com.db.ta.sdk.TmListener;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.Message;
import com.lcjian.lib.util.common.DateUtils;
import com.lcjian.lib.widget.RatioLayout;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class RoomMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ROOM_ADVERTISEMENT_MESSAGE = 0;
    private static final int TYPE_ROOM_RED_ENVELOPE_MESSAGE = 1;
    private static final int TYPE_ROOM_NOTIFICATION_MESSAGE = 2;

    private List<Message> mData;

    private FragmentManager mFragmentManager;

    RoomMessageAdapter(List<Message> data, FragmentManager fragmentManager) {
        this.mData = data;
        this.mFragmentManager = fragmentManager;
    }

    void replaceAll(final List<Message> data) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {

            @Override
            public int getOldListSize() {
                return mData == null ? 0 : mData.size();
            }

            @Override
            public int getNewListSize() {
                return data == null ? 0 : data.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return mData.get(oldItemPosition) == data.get(newItemPosition);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return mData.get(oldItemPosition) == data.get(newItemPosition);
            }
        }, true);
        this.mData = data;
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = mData.get(position).type;
        return type == 1 ? TYPE_ROOM_NOTIFICATION_MESSAGE
                : (type == 2 ? TYPE_ROOM_ADVERTISEMENT_MESSAGE : TYPE_ROOM_RED_ENVELOPE_MESSAGE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == TYPE_ROOM_NOTIFICATION_MESSAGE ? new RoomNotificationMessageViewHolder(parent)
                : (viewType == TYPE_ROOM_ADVERTISEMENT_MESSAGE ? new RoomAdvertisementMessageViewHolder(parent)
                : new RoomRedEnvelopeMessageViewHolder(parent, mFragmentManager));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RoomNotificationMessageViewHolder) {
            ((RoomNotificationMessageViewHolder) holder).bindTo(mData.get(position));
        } else if (holder instanceof RoomAdvertisementMessageViewHolder) {
            ((RoomAdvertisementMessageViewHolder) holder).bindTo(mData.get(position));
        } else if (holder instanceof RoomRedEnvelopeMessageViewHolder) {
            ((RoomRedEnvelopeMessageViewHolder) holder).bindTo(mData.get(position));
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof RoomAdvertisementMessageViewHolder) {
            ((RoomAdvertisementMessageViewHolder) holder).destroyAd();
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof RoomAdvertisementMessageViewHolder) {
            ((RoomAdvertisementMessageViewHolder) holder).initAd();
        }
    }

    static class RoomNotificationMessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_notification_message)
        TextView tv_notification_message;

        RoomNotificationMessageViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_notification_message_item, parent, false));
            ButterKnife.bind(this, this.itemView);
        }

        void bindTo(Message message) {
            tv_notification_message.setText(message.desc);
        }
    }

    static class RoomAdvertisementMessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_message_user_avatar)
        ImageView iv_message_user_avatar;
        @BindView(R.id.tv_message_user_name)
        TextView tv_message_user_name;
        @BindView(R.id.tv_message_time)
        TextView tv_message_time;
        @BindView(R.id.rl_message_advertisement_content)
        RatioLayout rl_message_advertisement_content;

        Message message;

        RoomAdvertisementMessageViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_advertisement_message_item, parent, false));
            ButterKnife.bind(this, this.itemView);
        }

        void bindTo(Message message) {
            this.message = message;
            Context context = itemView.getContext();
            Glide.with(context)
                    .load(message.hblUser == null ? null : message.hblUser.userHeadimg)
                    .apply(Global.userAvatar)
                    .transition(Global.crossFade)
                    .into(iv_message_user_avatar);
            tv_message_user_name.setText(message.hblUser == null ? "" : message.hblUser.userNickname);
            tv_message_time.setText(DateUtils.convertDateToStr(new Date(message.createTime), DateUtils.YYYY_MM_DD_HH_MM_SS));
        }

        void destroyAd() {
            TMNaTmView adView = (TMNaTmView) rl_message_advertisement_content.findViewById(R.id.view_message_advertisement);
            adView.destroy();
            rl_message_advertisement_content.removeAllViews();
        }

        void initAd() {
            TMNaTmView adView = (TMNaTmView) LayoutInflater.from(rl_message_advertisement_content.getContext()).inflate(
                    R.layout.message_advertisement, rl_message_advertisement_content, false);
            rl_message_advertisement_content.addView(adView);
            adView.setAdListener(new TmListener() {
                @Override
                public void onReceiveAd() {

                }

                @Override
                public void onFailedToReceiveAd() {

                }

                @Override
                public void onLoadFailed() {

                }

                @Override
                public void onCloseClick() {

                }

                @Override
                public void onAdClick() {

                }

                @Override
                public void onAdExposure() {

                }
            });
            adView.loadAd(2682);
        }
    }

    static class RoomRedEnvelopeMessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_message_user_avatar)
        ImageView iv_message_user_avatar;
        @BindView(R.id.tv_message_user_name)
        TextView tv_message_user_name;
        @BindView(R.id.tv_message_time)
        TextView tv_message_time;
        @BindView(R.id.tv_message_content)
        TextView tv_message_content;
        @BindView(R.id.fl_message_content)
        LinearLayout fl_message_content;

        Message message;

        FragmentManager mFragmentManager;

        RoomRedEnvelopeMessageViewHolder(ViewGroup parent, FragmentManager fragmentManager) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_red_envelope_message_item, parent, false));
            ButterKnife.bind(this, this.itemView);
            this.mFragmentManager = fragmentManager;
            fl_message_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RedEnvelopeOpenFragment.newInstance(message).show(mFragmentManager, "RedEnvelopeOpenFragment");
                }
            });
        }

        void bindTo(Message message) {
            this.message = message;
            Context context = itemView.getContext();
            Glide.with(context)
                    .load(message.hblUser == null ? null : message.hblUser.userHeadimg)
                    .apply(Global.userAvatar)
                    .transition(Global.crossFade)
                    .into(iv_message_user_avatar);
            tv_message_user_name.setText(message.hblUser == null ? "" : message.hblUser.userNickname);
            tv_message_time.setText(DateUtils.convertDateToStr(new Date(message.createTime), DateUtils.YYYY_MM_DD_HH_MM_SS));
            tv_message_content.setText(message.desc);
        }
    }
}
