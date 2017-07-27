package com.lcjian.happyredenvelope.ui.room;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.entity.Advertisement;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.SnatchingDetail;
import com.lcjian.happyredenvelope.ui.mine.InviteFriendActivity;
import com.lcjian.lib.entity.Displayable;
import com.lcjian.lib.recyclerview.LoadMoreAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RedEnvelopeSnatchedSuccessActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_envelope_snatched_success);
        ButterKnife.bind(this);

        btn_top_bar_left.setOnClickListener(this);
        tv_top_bar_right.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container,
                RedEnvelopeSnatchedSuccessFragment.newInstance(getIntent().getLongExtra("msg_id", 0)), "RedEnvelopeSnatchedSuccessFragment").commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_top_bar_left:
                onBackPressed();
                break;
            case R.id.tv_top_bar_right:
                startActivity(new Intent(v.getContext(), InviteFriendActivity.class));
                break;
            default:
                break;
        }
    }

    public static class RedEnvelopeSnatchedSuccessFragment extends RecyclerFragment<Displayable> {

        private TextView tv_red_envelope_amount;
        private ImageView iv_brand_avatar;
        private TextView tv_brand_name;
        private TextView tv_summary_one;
        private TextView tv_summary_two;

        private long mMsgId;

        private RedEnvelopeSnatchedSuccessAdapter mAdapter;

        public static RedEnvelopeSnatchedSuccessFragment newInstance(long msgId) {
            RedEnvelopeSnatchedSuccessFragment fragment = new RedEnvelopeSnatchedSuccessFragment();
            Bundle args = new Bundle();
            args.putLong("msg_id", msgId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mMsgId = getArguments().getLong("msg_id");
            }
        }

        @Override
        public RecyclerView.Adapter onCreateAdapter(List<Displayable> data) {
            mAdapter = new RedEnvelopeSnatchedSuccessAdapter(data);
            return mAdapter;
        }

        @Override
        public void onLoadMoreAdapterCreated(LoadMoreAdapter loadMoreAdapter) {
            View header = LayoutInflater.from(getContext()).inflate(R.layout.red_envelope_snatched_success_header, recycler_view, false);

            tv_red_envelope_amount = ButterKnife.findById(header, R.id.tv_red_envelope_amount);
            iv_brand_avatar = ButterKnife.findById(header, R.id.iv_brand_avatar);
            tv_brand_name = ButterKnife.findById(header, R.id.tv_brand_name);
            tv_summary_one = ButterKnife.findById(header, R.id.tv_summary_one);
            tv_summary_two = ButterKnife.findById(header, R.id.tv_summary_two);
            loadMoreAdapter.addHeader(header);
            loadMoreAdapter.setEnabled(false);
        }

        @Override
        public Observable<PageResult<Displayable>> onCreatePageObservable(int currentPage) {
            Observable<ResponseData<SnatchingDetail>> observable = mRestAPI.redEnvelopeService()
                    .getSnatchingDetail(mUserInfoSp.getLong("user_id", 0), mMsgId, currentPage, 100).cache();
            observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ResponseData<SnatchingDetail>>() {
                        @Override
                        public void call(ResponseData<SnatchingDetail> snatchingDetailResponseData) {
                            Glide.with(getContext())
                                    .load(snatchingDetailResponseData.data.supprotInfo.icon)
                                    .apply(Global.userAvatar)
                                    .transition(Global.crossFade)
                                    .into(iv_brand_avatar);
                            tv_red_envelope_amount.setText(String.format(Locale.getDefault(), "%s%s",
                                    "￥", new DecimalFormat("0.00").format(snatchingDetailResponseData.data.money)));
                            tv_brand_name.setText(String.valueOf(snatchingDetailResponseData.data.supprotInfo.name));
                            tv_summary_one.setText(getString(R.string.snatch_summary_one,
                                    snatchingDetailResponseData.data.alreadyReceiveCount, snatchingDetailResponseData.data.totalCount));
                            tv_summary_two.setText(getString(R.string.snatch_summary_two,
                                    snatchingDetailResponseData.data.position));
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {

                        }
                    });
            return observable
                    .map(new Func1<ResponseData<SnatchingDetail>, PageResult<Displayable>>() {
                        @Override
                        public PageResult<Displayable> call(ResponseData<SnatchingDetail> snatchingDetailResponseData) {
                            PageResult<Displayable> result = new PageResult<>();
                            result.total_pages = 1;
                            result.elements = new ArrayList<>();
                            result.elements.addAll(snatchingDetailResponseData.data.rankList);
                            if (result.elements.size() > 4) {
                                result.elements.add(4, new Advertisement());
                            } else {
                                result.elements.add(new Advertisement());
                            }
                            return result;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        @Override
        public void notifyDataChanged(List<Displayable> data) {
            mAdapter.replaceAll(data);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            swipe_refresh_layout.setColorSchemeResources(R.color.colorLightRed);

            recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
            recycler_view.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                    .color(ContextCompat.getColor(getContext(), R.color.colorDivider))
                    .size(1)
                    .build());

            super.onViewCreated(view, savedInstanceState);
        }
    }
}
