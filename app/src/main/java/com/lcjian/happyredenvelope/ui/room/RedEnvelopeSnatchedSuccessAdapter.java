package com.lcjian.happyredenvelope.ui.room;

import android.content.Context;
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
import com.lcjian.happyredenvelope.common.AdvertisementViewHolder;
import com.lcjian.happyredenvelope.data.entity.Advertisement;
import com.lcjian.happyredenvelope.data.entity.SnatchingDetail;
import com.lcjian.lib.entity.Displayable;
import com.lcjian.lib.util.common.DateUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


class RedEnvelopeSnatchedSuccessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ADVERTISEMENT = 0;
    private static final int TYPE_NORMAL = 1;

    private List<Displayable> mData;

    RedEnvelopeSnatchedSuccessAdapter(List<Displayable> data) {
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
        return mData.get(position) instanceof SnatchingDetail.Rank ? TYPE_NORMAL : TYPE_ADVERTISEMENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == TYPE_NORMAL ? new RedEnvelopeSnatchedSuccessViewHolder(parent) : new AdvertisementViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RedEnvelopeSnatchedSuccessViewHolder) {
            ((RedEnvelopeSnatchedSuccessViewHolder) holder).bindTo((SnatchingDetail.Rank) mData.get(position));
        } else if (holder instanceof AdvertisementViewHolder) {
            ((AdvertisementViewHolder) holder).bindTo((Advertisement) mData.get(position));
        }
    }

    static class RedEnvelopeSnatchedSuccessViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.iv_user_avatar)
        ImageView iv_user_avatar;
        @BindView(R.id.tv_user_name)
        TextView tv_user_name;
        @BindView(R.id.tv_snatched_time)
        TextView tv_snatched_time;
        @BindView(R.id.tv_snatched_count)
        TextView tv_snatched_count;
        @BindView(R.id.tv_luckiest)
        TextView tv_luckiest;

        RedEnvelopeSnatchedSuccessViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.red_envelope_success_item, parent, false));
            ButterKnife.bind(this, this.itemView);
        }

        void bindTo(SnatchingDetail.Rank rank) {
            Context context = itemView.getContext();
            Glide.with(context)
                    .load(rank.hblUser.userHeadimg)
                    .apply(Global.userAvatar)
                    .transition(Global.crossFade)
                    .into(iv_user_avatar);

            tv_user_name.setText(rank.hblUser.userNickname);
            tv_snatched_time.setText(DateUtils.convertDateToStr(new Date(rank.time), "yyyy-MM-dd HH:mm:ss"));
            tv_snatched_count.setText(context.getString(R.string.total_count, rank.totalCount));
            switch (rank.luck) {
                case 0:
                    tv_luckiest.setVisibility(View.GONE);
                    break;
                case 1:
                    tv_luckiest.setVisibility(View.VISIBLE);
                    tv_luckiest.setText(R.string.luckiest);
                    break;
                case 2:
                    tv_luckiest.setVisibility(View.VISIBLE);
                    tv_luckiest.setText(R.string.luckier);
                    break;
                case 3:
                    tv_luckiest.setVisibility(View.VISIBLE);
                    tv_luckiest.setText(R.string.lucky);
                    break;
                default:
                    tv_luckiest.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
