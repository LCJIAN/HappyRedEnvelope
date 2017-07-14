package com.lcjian.happyredenvelope.ui.room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.Video;
import com.lcjian.happyredenvelope.data.network.RestAPI;
import com.lcjian.happyredenvelope.ui.web.WebViewActivity;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RedEnvelopeSnatchedEmptyActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.fl_advertisement_content)
    FrameLayout fl_advertisement_content;
    @BindView(R.id.tv_change)
    TextView tv_change;
    @BindView(R.id.rv_videos)
    RecyclerView rv_videos;

    private List<Video> mData;
    private VideoAdapter mAdapter;

    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_envelope_snatched_empty);
        ButterKnife.bind(this);

        rv_videos.setHasFixedSize(true);
        rv_videos.setLayoutManager(new LinearLayoutManager(this));
        rv_videos.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(ContextCompat.getColor(this, R.color.colorDivider))
                .size(1)
                .build());

        mData = new ArrayList<>();
        mAdapter = new VideoAdapter(mData, mRestAPI);
        rv_videos.setAdapter(mAdapter);
        btn_top_bar_left.setOnClickListener(this);
        tv_change.setOnClickListener(this);

        refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_top_bar_left:
                onBackPressed();
                break;
            case R.id.tv_change:
                refresh();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    private void refresh() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mSubscription = mRestAPI.redEnvelopeService().getRecommendVideos(10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<List<Video>>>() {
                    @Override
                    public void call(ResponseData<List<Video>> listResponseData) {
                        mData.clear();
                        mData.addAll(listResponseData.data);
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    static class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

        private List<Video> mData;

        private RestAPI mRestAPI;

        private VideoAdapter(List<Video> data, RestAPI restAPI) {
            this.mData = data;
            this.mRestAPI = restAPI;
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VideoViewHolder(parent, mRestAPI);
        }

        @Override
        public void onBindViewHolder(VideoViewHolder holder, int position) {
            holder.bindTo(mData.get(position));
        }

        static class VideoViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.iv_video_thumbnail)
            ImageView iv_video_thumbnail;
            @BindView(R.id.iv_video_title)
            TextView iv_video_title;

            Video video;

            private RestAPI mRestAPI;

            VideoViewHolder(ViewGroup parent, RestAPI restAPI) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false));
                ButterKnife.bind(this, this.itemView);
                this.mRestAPI = restAPI;
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRestAPI.redEnvelopeService().addVideoHistory(video.id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<ResponseData<String>>() {
                                    @Override
                                    public void call(ResponseData<String> stringResponseData) {

                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {

                                    }
                                });
                        v.getContext().startActivity(new Intent(v.getContext(), WebViewActivity.class)
                                .putExtra("url", video.link));
                    }
                });
            }

            void bindTo(Video video) {
                this.video = video;
                Context context = itemView.getContext();
                Glide.with(context)
                        .load(video.picture)
                        .apply(Global.centerCrop)
                        .transition(Global.crossFade)
                        .into(iv_video_thumbnail);
                iv_video_title.setText(video.name);
            }
        }
    }

}
