package com.lcjian.happyredenvelope.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.GroupHeaderViewHolder;
import com.lcjian.happyredenvelope.data.entity.GoodsHistory;
import com.lcjian.happyredenvelope.data.entity.GroupHeader;
import com.lcjian.happyredenvelope.data.entity.RoomHistory;
import com.lcjian.happyredenvelope.data.entity.VideoHistory;
import com.lcjian.happyredenvelope.ui.room.RoomActivity;
import com.lcjian.happyredenvelope.ui.web.WebViewActivity;
import com.lcjian.lib.entity.Displayable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

class ViewHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ROOM = 0;
    private static final int TYPE_GOODS = 1;
    private static final int TYPE_VIDEO = 2;
    private static final int TYPE_GROUP_HEADER = 3;

    private List<Displayable> mData;

    ViewHistoryAdapter(List<Displayable> data) {
        this.mData = data;
    }

    void replaceAll(final List<Displayable> data) {
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
        return mData.get(position) instanceof RoomHistory ? TYPE_ROOM
                : (mData.get(position) instanceof GoodsHistory ? TYPE_GOODS
                : (mData.get(position) instanceof VideoHistory ? TYPE_VIDEO : TYPE_GROUP_HEADER));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == TYPE_ROOM ? new RoomHistoryViewHolder(parent)
                : (viewType == TYPE_GOODS ? new GoodsHistoryViewHolder(parent)
                : (viewType == TYPE_VIDEO ? new VideoHistoryViewHolder(parent)
                : new GroupHeaderViewHolder(parent)));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RoomHistoryViewHolder) {
            ((RoomHistoryViewHolder) holder).bindTo((RoomHistory) mData.get(position));
        } else if (holder instanceof GoodsHistoryViewHolder) {
            ((GoodsHistoryViewHolder) holder).bindTo((GoodsHistory) mData.get(position));
        } else if (holder instanceof VideoHistoryViewHolder) {
            ((VideoHistoryViewHolder) holder).bindTo((VideoHistory) mData.get(position));
        } else if (holder instanceof GroupHeaderViewHolder) {
            ((GroupHeaderViewHolder) holder).bindTo((GroupHeader) mData.get(position));
        }
    }

    static class RoomHistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_room_avatar)
        ImageView iv_room_avatar;
        @BindView(R.id.tv_room_name)
        TextView tv_room_name;

        RoomHistory roomHistory;

        RoomHistoryViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_history_item, parent, false));
            ButterKnife.bind(this, this.itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(v.getContext(), RoomActivity.class)
                            .putExtra("room_id", roomHistory.hongBaoRoom.id));
                }
            });
        }

        void bindTo(RoomHistory roomHistory) {
            this.roomHistory = roomHistory;
            Context context = itemView.getContext();
            Glide.with(context)
                    .load(roomHistory.hongBaoRoom.pic)
                    .apply(Global.roomAvatar)
                    .transition(Global.crossFade)
                    .into(iv_room_avatar);
            tv_room_name.setText(roomHistory.hongBaoRoom.name);
        }
    }

    static class GoodsHistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_goods_avatar)
        ImageView iv_goods_avatar;
        @BindView(R.id.tv_goods_name)
        TextView tv_goods_name;
        @BindView(R.id.tv_goods_price)
        TextView tv_goods_price;
        @BindView(R.id.tv_goods_sale_count)
        TextView tv_goods_sale_count;

        GoodsHistory goodsHistory;

        private DecimalFormat mDecimalFormat;

        GoodsHistoryViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_item, parent, false));
            ButterKnife.bind(this, this.itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(v.getContext(), WebViewActivity.class)
                            .putExtra("url", goodsHistory.quanLink));
                }
            });
            this.mDecimalFormat = new DecimalFormat("0.00");
        }

        void bindTo(GoodsHistory goodsHistory) {
            this.goodsHistory = goodsHistory;
            Context context = itemView.getContext();
            Glide.with(context)
                    .load(goodsHistory.pic)
                    .transition(Global.crossFade)
                    .into(iv_goods_avatar);
            tv_goods_name.setText(goodsHistory.title);
            tv_goods_price.setText(String.format(Locale.getDefault(), "%s%s", "￥", mDecimalFormat.format(goodsHistory.price)));
            tv_goods_sale_count.setText(context.getString(R.string.sale_count, goodsHistory.saleCount));
        }
    }

    static class VideoHistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_video_thumbnail)
        ImageView iv_video_thumbnail;
        @BindView(R.id.iv_video_title)
        TextView iv_video_title;
        @BindView(R.id.tv_video_tag)
        TextView tv_video_tag;
        @BindView(R.id.tv_video_play_count)
        TextView tv_video_play_count;

        VideoHistory videoHistory;

        VideoHistoryViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false));
            ButterKnife.bind(this, this.itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(v.getContext(), WebViewActivity.class)
                            .putExtra("url", videoHistory.ku6ShortVideo.link));
                }
            });
        }

        void bindTo(VideoHistory videoHistory) {
            this.videoHistory = videoHistory;
            Context context = itemView.getContext();
            Glide.with(context)
                    .load(videoHistory.ku6ShortVideo.picture)
                    .apply(Global.centerCrop)
                    .transition(Global.crossFade)
                    .into(iv_video_thumbnail);
            iv_video_title.setText(videoHistory.ku6ShortVideo.name);
            tv_video_tag.setText(videoHistory.ku6ShortVideo.tag);
            tv_video_play_count.setText(context.getString(R.string.play_count, videoHistory.ku6ShortVideo.playCount));
        }
    }
}
