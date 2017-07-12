package com.lcjian.happyredenvelope.ui.main;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.lcjian.happyredenvelope.common.RoomViewHolder;
import com.lcjian.happyredenvelope.data.entity.Room;

import java.util.List;

class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Room> mData;

    private SharedPreferences mUserInfoSp;
    private FragmentManager mFragmentManager;

    RoomAdapter(List<Room> data, SharedPreferences userInfoSp, FragmentManager fragmentManager) {
        this.mData = data;
        this.mUserInfoSp = userInfoSp;
        this.mFragmentManager = fragmentManager;
    }

    public void replaceAll(final List<Room> data) {
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RoomViewHolder(parent, mUserInfoSp, mFragmentManager);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RoomViewHolder) {
            ((RoomViewHolder) holder).bindTo(mData.get(position));
        }
    }
}
