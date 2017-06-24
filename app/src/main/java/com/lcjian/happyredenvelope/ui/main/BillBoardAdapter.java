package com.lcjian.happyredenvelope.ui.main;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.AdvertisementViewHolder;
import com.lcjian.happyredenvelope.data.entity.Advertisement;
import com.lcjian.happyredenvelope.data.entity.Billboard;
import com.lcjian.lib.entity.Displayable;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class BillBoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ADVERTISEMENT = 0;
    private static final int TYPE_BILLBOARD = 1;

    private List<Displayable> mData;

    private long mMyUserId;

    BillBoardAdapter(List<Displayable> data, long myUserId) {
        this.mData = data;
        this.mMyUserId = myUserId;
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
        return mData.get(position) instanceof Billboard ? TYPE_BILLBOARD : TYPE_ADVERTISEMENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == TYPE_BILLBOARD ? new BillboardViewHolder(parent, mMyUserId) : new AdvertisementViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BillboardViewHolder) {
            ((BillboardViewHolder) holder).bindTo((Billboard) mData.get(position));
        } else if (holder instanceof AdvertisementViewHolder) {
            ((AdvertisementViewHolder) holder).bindTo((Advertisement) mData.get(position));
        }
    }

    static class BillboardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user_avatar)
        ImageView iv_user_avatar;
        @BindView(R.id.tv_user_name)
        TextView tv_user_name;
        @BindView(R.id.tv_billboard_info)
        TextView tv_billboard_info;
        @BindView(R.id.tv_billboard_amount)
        TextView tv_billboard_amount;

        Billboard billboard;

        long mMyUserId;

        private DecimalFormat mDecimalFormat;

        BillboardViewHolder(ViewGroup parent, long myUserId) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.billboard_item, parent, false));
            this.mMyUserId = myUserId;
            this.mDecimalFormat = new DecimalFormat("0.00");
            ButterKnife.bind(this, this.itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        void bindTo(Billboard billboard) {
            this.billboard = billboard;
            Context context = itemView.getContext();
            Glide.with(context)
                    .load(billboard.billboardUser.userHeadimg)
                    .apply(RequestOptions.placeholderOf(R.drawable.shape_no_avatar_bg).centerCrop())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(iv_user_avatar);
            tv_user_name.setText(billboard.billboardUser.userNickname);
            if (mMyUserId == billboard.billboardUser.userId) {
                tv_user_name.setTextColor(ContextCompat.getColor(context, R.color.colorLightRed));
            } else {
                tv_user_name.setTextColor(ContextCompat.getColor(context, R.color.colorTextBlack));

            }
            tv_billboard_info.setText(context.getString(R.string.total_count, billboard.totalCount)
                    + "    " + context.getString(R.string.total_luck_count, billboard.totalLuck));
            tv_billboard_amount.setText(context.getString(R.string.total_amount, mDecimalFormat.format(billboard.total)));
        }
    }
}
