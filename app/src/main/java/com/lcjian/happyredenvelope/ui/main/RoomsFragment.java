package com.lcjian.happyredenvelope.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.Room;
import com.lcjian.lib.util.common.DimenUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RoomsFragment extends RecyclerFragment<Room> {

    private RoomAdapter mAdapter;

    private boolean mIsVip;

    private int mCurrentPage;

    public static RoomsFragment newInstance(boolean isVip) {
        RoomsFragment fragment = new RoomsFragment();
        Bundle args = new Bundle();
        args.putBoolean("is_vip", isVip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIsVip = getArguments().getBoolean("is_vip");
        }
    }

    @Override
    public RecyclerView.Adapter onCreateAdapter(List<Room> data) {
        mAdapter = new RoomAdapter(data);
        return mAdapter;
    }

    @Override
    public Observable<PageResult<Room>> onCreatePageObservable(int currentPage) {
        mCurrentPage = currentPage;
        return (mIsVip ? mRestAPI.redEnvelopeService().getVipRooms(currentPage, 10) : mRestAPI.redEnvelopeService()
                .getNormalRooms(currentPage, 10))
                .map(new Func1<ResponseData<PageResult<Room>>, PageResult<Room>>() {
                    @Override
                    public PageResult<Room> call(ResponseData<PageResult<Room>> pageResultResponseData) {
                        return pageResultResponseData.data;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<PageResult<Room>>() {
                    @Override
                    public void call(PageResult<Room> displayablePageResult) {
                        if (mCurrentPage == 1) {
                            setupHeader();
                        }
                    }
                })
                .cache();
    }

    @Override
    public void notifyDataChanged(List<Room> data) {
        mAdapter.replaceAll(data);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        swipe_refresh_layout.setColorSchemeResources(R.color.colorLightRed);

        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .color(ContextCompat.getColor(getContext(), R.color.colorDivider))
                .size((int) DimenUtils.dipToPixels(8, getContext()))
                .build());

        super.onViewCreated(view, savedInstanceState);
    }

    private void setupHeader() {
    }

}

