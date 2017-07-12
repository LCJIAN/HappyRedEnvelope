package com.lcjian.happyredenvelope.ui.room;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class RoomMemberAdapter extends RecyclerView.Adapter<RoomMemberAdapter.RoomMemberViewHolder> {

    private List<User> mData;

    RoomMemberAdapter(List<User> data) {
        this.mData = data;
    }

    void replaceAll(final List<User> data) {
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
                return mData.get(oldItemPosition).userId == data.get(newItemPosition).userId;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return TextUtils.equals(mData.get(oldItemPosition).userHeadimg, data.get(newItemPosition).userHeadimg);
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
    public RoomMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RoomMemberViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RoomMemberViewHolder holder, int position) {
        holder.bindTo(mData.get(position));
    }

    static class RoomMemberViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_room_member_avatar)
        ImageView iv_room_member_avatar;

        RoomMemberViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_member_item, parent, false));
            ButterKnife.bind(this, this.itemView);
        }

        void bindTo(User user) {
            Glide.with(itemView.getContext())
                    .load(user.userHeadimg)
                    .apply(Global.userAvatar)
                    .transition(Global.crossFade)
                    .into(iv_room_member_avatar);
        }
    }
}
