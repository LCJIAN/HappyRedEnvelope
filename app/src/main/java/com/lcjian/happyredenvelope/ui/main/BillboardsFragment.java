package com.lcjian.happyredenvelope.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.entity.Advertisement;
import com.lcjian.happyredenvelope.data.entity.Billboard;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.lib.entity.Displayable;
import com.lcjian.lib.recyclerview.LoadMoreAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BillboardsFragment extends RecyclerFragment<Displayable> {

    private ImageView iv_user_avatar_one;
    private ImageView iv_user_avatar_two;
    private ImageView iv_user_avatar_three;
    private TextView tv_user_name_one;
    private TextView tv_user_name_two;
    private TextView tv_user_name_three;
    private TextView tv_billboard_amount_one;
    private TextView tv_billboard_amount_two;
    private TextView tv_billboard_amount_three;

    private BillBoardAdapter mAdapter;

    private int mType;

    private Billboard one;
    private Billboard two;
    private Billboard three;

    private DecimalFormat mDecimalFormat;

    private int mCurrentPage;

    public static BillboardsFragment newInstance(int type) {
        BillboardsFragment fragment = new BillboardsFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt("type");
        }
        this.mDecimalFormat = new DecimalFormat("0.00");
    }

    @Override
    public RecyclerView.Adapter onCreateAdapter(List<Displayable> data) {
        mAdapter = new BillBoardAdapter(data, mUserInfoSp.getLong("user_id", 0));
        return mAdapter;
    }

    @Override
    public void onLoadMoreAdapterCreated(LoadMoreAdapter loadMoreAdapter) {
        View header = LayoutInflater.from(getContext()).inflate(R.layout.billboard_header, recycler_view, false);
        iv_user_avatar_one = ButterKnife.findById(header, R.id.iv_user_avatar_one);
        iv_user_avatar_two = ButterKnife.findById(header, R.id.iv_user_avatar_two);
        iv_user_avatar_three = ButterKnife.findById(header, R.id.iv_user_avatar_three);
        tv_user_name_one = ButterKnife.findById(header, R.id.tv_user_name_one);
        tv_user_name_two = ButterKnife.findById(header, R.id.tv_user_name_two);
        tv_user_name_three = ButterKnife.findById(header, R.id.tv_user_name_three);
        tv_billboard_amount_one = ButterKnife.findById(header, R.id.tv_billboard_amount_one);
        tv_billboard_amount_two = ButterKnife.findById(header, R.id.tv_billboard_amount_two);
        tv_billboard_amount_three = ButterKnife.findById(header, R.id.tv_billboard_amount_three);
        loadMoreAdapter.addHeader(header);
    }

    @Override
    public Observable<PageResult<Displayable>> onCreatePageObservable(int currentPage) {
        mCurrentPage = currentPage;
        return mRestAPI.redEnvelopeService()
                .getBillboard(mUserInfoSp.getLong("user_id", 0), mType, currentPage, 10)
                .map(new Func1<ResponseData<PageResult<Billboard>>, PageResult<Displayable>>() {
                    @Override
                    public PageResult<Displayable> call(ResponseData<PageResult<Billboard>> pageResultResponseData) {
                        List<Billboard> billboards = pageResultResponseData.data.elements;
                        if (mCurrentPage == 1) {
                            one = billboards.get(0);
                            two = billboards.get(1);
                            three = billboards.get(2);
                            billboards.remove(one);
                            billboards.remove(two);
                            billboards.remove(three);
                        }
                        PageResult<Displayable> result = new PageResult<>();
                        List<Displayable> displayableList = new ArrayList<>();
                        displayableList.addAll(billboards);
                        if (mCurrentPage == 1 && displayableList.size() > 3) {
                            displayableList.add(3, new Advertisement());
                        } else {
                            displayableList.add(new Advertisement());
                        }
                        result.elements = displayableList;
                        result.total_pages = pageResultResponseData.data.total_pages;
                        return result;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<PageResult<Displayable>>() {
                    @Override
                    public void call(PageResult<Displayable> displayablePageResult) {
                        if (mCurrentPage == 1) {
                            setupHeaderItem(one, iv_user_avatar_one, tv_user_name_one, tv_billboard_amount_one);
                            setupHeaderItem(two, iv_user_avatar_two, tv_user_name_two, tv_billboard_amount_two);
                            setupHeaderItem(three, iv_user_avatar_three, tv_user_name_three, tv_billboard_amount_three);
                        }
                    }
                })
                .cache();
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

        if (one != null && two != null && three != null) {
            setupHeaderItem(one, iv_user_avatar_one, tv_user_name_one, tv_billboard_amount_one);
            setupHeaderItem(two, iv_user_avatar_two, tv_user_name_two, tv_billboard_amount_two);
            setupHeaderItem(three, iv_user_avatar_three, tv_user_name_three, tv_billboard_amount_three);
        }
    }

    private void setupHeaderItem(Billboard billboard, ImageView avatar, TextView userName, TextView billboardAmount) {
        Glide.with(getContext())
                .load(billboard.hblUser.userHeadimg)
                .apply(Global.userAvatar)
                .transition(Global.crossFade)
                .into(avatar);
        userName.setText(billboard.hblUser.userNickname);
        billboardAmount.setText(getString(R.string.total_amount, mDecimalFormat.format(billboard.total)));
    }

}
