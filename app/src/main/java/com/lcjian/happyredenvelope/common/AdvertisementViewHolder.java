package com.lcjian.happyredenvelope.common;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.db.ta.sdk.TMNaTmView;
import com.db.ta.sdk.TmListener;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.Advertisement;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdvertisementViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.ad_small_native)
    TMNaTmView ad_small_native;

    private int mAdId;

    public AdvertisementViewHolder(ViewGroup parent, int adId) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.advertisement_item, parent, false));
        ButterKnife.bind(this, this.itemView);
        this.mAdId = adId;
    }

    public void bindTo(Advertisement advertisement) {
    }

    public void loadAd() {
        ad_small_native.setAdListener(new TmListener() {
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
        ad_small_native.loadAd(mAdId);
    }

    public void destroyAd() {
        if (ad_small_native != null) {
            ad_small_native.destroy();
        }
    }
}
