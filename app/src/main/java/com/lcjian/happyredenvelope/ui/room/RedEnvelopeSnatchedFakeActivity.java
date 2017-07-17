package com.lcjian.happyredenvelope.ui.room;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.GoodsViewHolder;
import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.entity.Goods;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.ui.web.WebViewActivity;
import com.lcjian.lib.recyclerview.LoadMoreAdapter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RedEnvelopeSnatchedFakeActivity extends BaseActivity {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_envelope_snatched_fake);
        ButterKnife.bind(this);

        btn_top_bar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_top_bar_title.setText(R.string.brand_coupon);
        tv_top_bar_right.setVisibility(View.GONE);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_red_env_fake,
                new RedEnvelopeSnatchedFakeFragment(), "RedEnvelopeSnatchedFakeFragment").commit();
    }

    public static class RedEnvelopeSnatchedFakeFragment extends RecyclerFragment<Goods> {

        private GoodsAdapter mAdapter;
        private ImageView iv_coupon;
        private ImageView iv_goods_avatar;
        private TextView tv_goods_name;
        private TextView tv_origin_price;
        private TextView tv_now_price;
        private TextView tv_coupon_amount;

        private Subscription mSubscription;
        private Subscription mSubscription2;

        private DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

        @Override
        public RecyclerView.Adapter onCreateAdapter(List<Goods> data) {
            mAdapter = new GoodsAdapter(data);
            return mAdapter;
        }

        @Override
        public void onLoadMoreAdapterCreated(LoadMoreAdapter loadMoreAdapter) {
            View header = LayoutInflater.from(getContext()).inflate(R.layout.coupon_header, recycler_view, false);

            iv_coupon = ButterKnife.findById(header, R.id.iv_coupon);
            iv_goods_avatar = ButterKnife.findById(header, R.id.iv_goods_avatar);
            tv_goods_name = ButterKnife.findById(header, R.id.tv_goods_name);
            tv_origin_price = ButterKnife.findById(header, R.id.tv_origin_price);
            tv_now_price = ButterKnife.findById(header, R.id.tv_now_price);
            tv_coupon_amount = ButterKnife.findById(header, R.id.tv_coupon_amount);

            loadMoreAdapter.addHeader(header);
            loadMoreAdapter.setEnabled(false);
        }

        @Override
        public Observable<PageResult<Goods>> onCreatePageObservable(int currentPage) {
            Observable<ResponseData<List<Goods>>> observable = mRestAPI.redEnvelopeService().getRecommendGoods(1).cache();
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
            mSubscription = observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ResponseData<List<Goods>>>() {
                        @Override
                        public void call(final ResponseData<List<Goods>> listResponseData) {
                            if (listResponseData.code == 0) {
                                Glide.with(getContext())
                                        .load(listResponseData.data.get(0).pic)
                                        .apply(Global.centerCrop)
                                        .transition(Global.crossFade)
                                        .into(iv_goods_avatar);
                                tv_goods_name.setText(listResponseData.data.get(0).title);
                                iv_goods_avatar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        v.getContext().startActivity(new Intent(v.getContext(), WebViewActivity.class)
                                                .putExtra("url", listResponseData.data.get(0).link));
                                    }
                                });

                                tv_origin_price.setText(getString(R.string.origin_price,
                                        "￥" + mDecimalFormat.format(listResponseData.data.get(0).price)));
                                tv_now_price.setText(String.format(Locale.getDefault(), "%s%s",
                                        "￥", mDecimalFormat.format(listResponseData.data.get(0).zkPrice)));
                                tv_coupon_amount.setText(getString(R.string.origin_price,
                                        "￥" + mDecimalFormat.format(listResponseData.data.get(0).price - listResponseData.data.get(0).zkPrice)));
                                iv_coupon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        receiveTicket(listResponseData.data.get(0).id);
                                    }
                                });
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            return observable
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
            swipe_refresh_layout.setEnabled(false);

            recycler_view.setHasFixedSize(true);
            recycler_view.setLayoutManager(new GridLayoutManager(getContext(), 2));

            super.onViewCreated(view, savedInstanceState);
        }

        @Override
        public void onDestroyView() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
            if (mSubscription2 != null) {
                mSubscription2.unsubscribe();
            }
            super.onDestroyView();
        }

        private void receiveTicket(long ticketId) {
            if (mSubscription2 != null) {
                mSubscription2.unsubscribe();
            }
            mSubscription2 = mRestAPI.redEnvelopeService().receiveTicket(mUserInfoSp.getLong("user_id", 0), ticketId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ResponseData<String>>() {
                        @Override
                        public void call(ResponseData<String> stringResponseData) {
                            if (stringResponseData.code == 0) {
                                Toast.makeText(App.getInstance(), R.string.receive_success, Toast.LENGTH_LONG).show();
                                iv_coupon.setEnabled(false);
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {

                        }
                    });
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
