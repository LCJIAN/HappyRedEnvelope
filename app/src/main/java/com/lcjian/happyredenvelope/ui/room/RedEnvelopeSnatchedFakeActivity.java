package com.lcjian.happyredenvelope.ui.room;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.GoodsViewHolder;
import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.entity.Goods;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.lib.recyclerview.LoadMoreAdapter;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RedEnvelopeSnatchedFakeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_envelope_snatched_fake);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_red_env_fake,
                new RedEnvelopeSnatchedFakeFragment(), "RedEnvelopeSnatchedFakeFragment").commit();
    }

    public static class RedEnvelopeSnatchedFakeFragment extends RecyclerFragment<Goods> {

        private GoodsAdapter mAdapter;

        @Override
        public RecyclerView.Adapter onCreateAdapter(List<Goods> data) {
            mAdapter = new GoodsAdapter(data);
            return mAdapter;
        }

        @Override
        public void onLoadMoreAdapterCreated(LoadMoreAdapter loadMoreAdapter) {
            View header = LayoutInflater.from(getContext()).inflate(R.layout.coupon_header, recycler_view, false);
            loadMoreAdapter.addHeader(header);
            loadMoreAdapter.setEnabled(false);
        }

        @Override
        public Observable<PageResult<Goods>> onCreatePageObservable(int currentPage) {
            return mRestAPI.redEnvelopeService()
                    .getRecommendGoods(1)
                    .flatMap(new Func1<ResponseData<List<Goods>>, Observable<PageResult<Goods>>>() {
                        @Override
                        public Observable<PageResult<Goods>> call(ResponseData<List<Goods>> listResponseData) {
                            return mRestAPI.redEnvelopeService()
                                    .guessYouLike(listResponseData.data.get(0).id, 10)
                                    .map(new Func1<ResponseData<List<Goods>>, PageResult<Goods>>() {
                                        @Override
                                        public PageResult<Goods> call(ResponseData<List<Goods>> listResponseData) {
                                            PageResult<Goods> result = new PageResult<>();
                                            result.elements = listResponseData.data;
                                            result.total_pages = 1;
                                            return result;
                                        }
                                    });
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        @Override
        public void notifyDataChanged(List<Goods> data) {
            mAdapter.replaceAll(data);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            swipe_refresh_layout.setColorSchemeResources(R.color.colorLightRed);

            recycler_view.setHasFixedSize(true);
            recycler_view.setLayoutManager(new GridLayoutManager(getContext(), 2));

            super.onViewCreated(view, savedInstanceState);


        }
    }

    private static class GoodsAdapter extends RecyclerView.Adapter<GoodsViewHolder> {

        private List<Goods> mData;

        GoodsAdapter(List<Goods> data) {
            this.mData = data;
        }

        void replaceAll(final List<Goods> data) {
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
        public GoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GoodsViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(GoodsViewHolder holder, int position) {
            holder.bindTo(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }
}
