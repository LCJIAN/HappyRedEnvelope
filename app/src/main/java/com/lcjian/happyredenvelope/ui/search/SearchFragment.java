package com.lcjian.happyredenvelope.ui.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.db.table.SearchHistoryTable;
import com.lcjian.happyredenvelope.data.entity.Goods;
import com.lcjian.happyredenvelope.data.entity.GroupHeader;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.Room;
import com.lcjian.happyredenvelope.data.entity.SearchHistory;
import com.lcjian.happyredenvelope.data.entity.SearchHistoryHeader;
import com.lcjian.lib.entity.Displayable;
import com.lcjian.lib.recyclerview.LoadMoreAdapter;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class SearchFragment extends RecyclerFragment<Displayable> {

    private String mKeyword = "10";

    private SearchAdapter mAdapter;

    @Override
    public RecyclerView.Adapter onCreateAdapter(List<Displayable> data) {
        mAdapter = new SearchAdapter(data);
        return mAdapter;
    }

    @Override
    public void onLoadMoreAdapterCreated(LoadMoreAdapter loadMoreAdapter) {
        loadMoreAdapter.setEnabled(false);
    }

    @Override
    public Observable<PageResult<Displayable>> onCreatePageObservable(int currentPage) {
        if (TextUtils.isEmpty(mKeyword)) {
            return Observable.zip(
                    mStorIOSQLite.get().listOfObjects(SearchHistory.class)
                            .withQuery(Query.builder().table(SearchHistoryTable.TABLE_NAME)
                                    .orderBy(SearchHistoryTable.COLUMN_UPDATE_TIME + " DESC ")
                                    .build())
                            .prepare()
                            .asRxObservable()
                            .take(1),
                    mRestAPI.redEnvelopeService().getRecommendGoods(6),
                    new Func2<List<SearchHistory>, ResponseData<List<Goods>>, PageResult<Displayable>>() {
                        @Override
                        public PageResult<Displayable> call(List<SearchHistory> searchHistories,
                                                            ResponseData<List<Goods>> listResponseData) {
                            PageResult<Displayable> result = new PageResult<>();
                            result.total_pages = 1;
                            result.elements = new ArrayList<>();
                            if (searchHistories == null || searchHistories.isEmpty()) {
                                result.elements.add(new SearchHistoryHeader(
                                        getString(R.string.history_search), getString(R.string.no_search_history), true));
                            } else {
                                result.elements.add(new SearchHistoryHeader(
                                        getString(R.string.history_search), getString(R.string.no_search_history), false));
                                result.elements.addAll(searchHistories);
                            }
                            if (listResponseData.data != null && !listResponseData.data.isEmpty()) {
                                result.elements.add(new GroupHeader(getString(R.string.recommend_goods)));
                                result.elements.addAll(listResponseData.data);
                            }

                            return result;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } else {
            return Observable.zip(
                    mRestAPI.redEnvelopeService().searchRoom(mKeyword),
                    mRestAPI.redEnvelopeService().getRecommendGoods(6),
                    new Func2<ResponseData<Room>, ResponseData<List<Goods>>, PageResult<Displayable>>() {
                        @Override
                        public PageResult<Displayable> call(ResponseData<Room> roomResponseData,
                                                            ResponseData<List<Goods>> listResponseData) {
                            PageResult<Displayable> result = new PageResult<>();
                            result.total_pages = 1;
                            result.elements = new ArrayList<>();
                            if (roomResponseData.data == null) {
                                result.elements.add(new SearchHistoryHeader(
                                        getString(R.string.history_search), getString(R.string.no_search_result), true));
                            } else {
                                result.elements.add(roomResponseData.data);
                            }
                            if (listResponseData.data != null && !listResponseData.data.isEmpty()) {
                                result.elements.add(new GroupHeader(getString(R.string.recommend_goods)));
                                result.elements.addAll(listResponseData.data);
                            }

                            return result;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }

    @Override
    public void notifyDataChanged(List<Displayable> data) {
        mAdapter.replaceAll(data);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        swipe_refresh_layout.setColorSchemeResources(R.color.colorLightRed);

        recycler_view.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = recycler_view.getAdapter().getItemViewType(position);
                if (viewType == 1) {
                    return 1;
                } else {
                    return 2;
                }
            }
        });
        recycler_view.setLayoutManager(gridLayoutManager);

        super.onViewCreated(view, savedInstanceState);
    }
}