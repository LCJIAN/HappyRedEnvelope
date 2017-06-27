package com.lcjian.happyredenvelope.common;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.Advertisement;

public class AdvertisementViewHolder extends RecyclerView.ViewHolder {

    public AdvertisementViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.advertisement_item, parent, false));
    }

    public void bindTo(Advertisement advertisement) {
    }
}
