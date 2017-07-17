package com.lcjian.happyredenvelope.ui.mine;

import android.app.ProgressDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.entity.LuckCardCombo;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.WeChatPayOrder;
import com.lcjian.lib.recyclerview.LoadMoreAdapter;
import com.lcjian.lib.util.common.DimenUtils;
import com.lqpinxuan.lqpx.wxapi.WeChatPay;

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

public class BuyLuckCardActivity extends BaseActivity {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_luck_card);
        ButterKnife.bind(this);

        btn_top_bar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_top_bar_title.setText(R.string.buy_luck_card);
        tv_top_bar_right.setVisibility(View.GONE);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container,
                new BuyLuckCardFragment(), "BuyLuckCardFragment").commit();
    }

    public static class BuyLuckCardFragment extends RecyclerFragment<LuckCardCombo> {

        private Button btn_decrease;
        private TextView tv_buy_luck_card_count;
        private Button btn_increase;
        private TextView tv_total_price;
        private Button btn_buy_now;

        private LuckCardComboAdapter mAdapter;

        private int mBuyCount = 1;

        private DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

        private Subscription mSubscription;

        private ProgressDialog mProgressDialog;

        private RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (mAdapter.mChecked != null) {
                    tv_total_price.setText(String.format(Locale.getDefault(), "%s%s",
                            mDecimalFormat.format(mBuyCount * mAdapter.mChecked.cardPrice), getString(R.string.yuan)));
                }
            }
        };

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setCancelable(false);
        }

        @Override
        public RecyclerView.Adapter onCreateAdapter(List<LuckCardCombo> data) {
            mAdapter = new LuckCardComboAdapter(data);
            mAdapter.registerAdapterDataObserver(mObserver);
            return mAdapter;
        }

        @Override
        public void onLoadMoreAdapterCreated(LoadMoreAdapter loadMoreAdapter) {
            View footer = LayoutInflater.from(getContext()).inflate(R.layout.buy_luck_card_footer, recycler_view, false);
            btn_decrease = ButterKnife.findById(footer, R.id.btn_decrease);
            tv_buy_luck_card_count = ButterKnife.findById(footer, R.id.tv_buy_luck_card_count);
            btn_increase = ButterKnife.findById(footer, R.id.btn_increase);
            tv_total_price = ButterKnife.findById(footer, R.id.tv_total_price);
            btn_buy_now = ButterKnife.findById(footer, R.id.btn_buy_now);

            tv_buy_luck_card_count.setText(String.valueOf(mBuyCount));
            btn_decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBuyCount > 1) {
                        mBuyCount--;
                    }
                    tv_buy_luck_card_count.setText(String.valueOf(mBuyCount));
                    if (mAdapter.mChecked != null) {
                        tv_total_price.setText(String.format(Locale.getDefault(), "%s%s",
                                mDecimalFormat.format(mBuyCount * mAdapter.mChecked.cardPrice), getString(R.string.yuan)));
                    }
                }
            });
            btn_increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBuyCount++;
                    tv_buy_luck_card_count.setText(String.valueOf(mBuyCount));
                    if (mAdapter.mChecked != null) {
                        tv_total_price.setText(String.format(Locale.getDefault(), "%s%s",
                                mDecimalFormat.format(mBuyCount * mAdapter.mChecked.cardPrice), getString(R.string.yuan)));
                    }
                }
            });
            btn_buy_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAdapter.mChecked != null) {
                        mProgressDialog.show();
                        if (mSubscription != null) {
                            mSubscription.unsubscribe();
                        }
                        mSubscription = mRestAPI.redEnvelopeService()
                                .createBuyingLuckCardOrder(mUserInfoSp.getLong("user_id", 0),
                                        mAdapter.mChecked.cardId, mBuyCount)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<ResponseData<WeChatPayOrder>>() {
                                    @Override
                                    public void call(ResponseData<WeChatPayOrder> orderResponseData) {
                                        mProgressDialog.dismiss();
                                        if (orderResponseData.code == 0) {
                                            WeChatPay.pay(getContext(), orderResponseData.data.appid,
                                                    orderResponseData.data.partnerid,
                                                    orderResponseData.data.prepayid,
                                                    orderResponseData.data.noncestr,
                                                    orderResponseData.data.timestamp,
                                                    orderResponseData.data.packages,
                                                    orderResponseData.data.sign);
                                        } else {
                                            Toast.makeText(App.getInstance(), orderResponseData.msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            });

            tv_buy_luck_card_count.setText(String.valueOf(mBuyCount));
            loadMoreAdapter.addFooter(footer);
        }

        @Override
        public Observable<PageResult<LuckCardCombo>> onCreatePageObservable(int currentPage) {
            return mRestAPI.redEnvelopeService()
                    .getLuckCardCombo()
                    .map(new Func1<ResponseData<List<LuckCardCombo>>, PageResult<LuckCardCombo>>() {
                        @Override
                        public PageResult<LuckCardCombo> call(ResponseData<List<LuckCardCombo>> listResponseData) {
                            PageResult<LuckCardCombo> result = new PageResult<>();
                            result.elements = listResponseData.data;
                            result.total_pages = 1;
                            return result;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        @Override
        public void notifyDataChanged(List<LuckCardCombo> data) {
            mAdapter.replaceAll(data);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            swipe_refresh_layout.setColorSchemeResources(R.color.colorLightRed);
            swipe_refresh_layout.setEnabled(false);

            recycler_view.setHasFixedSize(true);
            recycler_view.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recycler_view.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int offsets = (int) DimenUtils.dipToPixels(4, parent.getContext());
                    outRect.set(offsets, offsets, offsets, offsets);
                }
            });

            super.onViewCreated(view, savedInstanceState);
        }

        @Override
        public void onDestroyView() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
            mAdapter.unregisterAdapterDataObserver(mObserver);
            super.onDestroyView();
        }
    }

    static class LuckCardComboAdapter extends RecyclerView.Adapter<LuckCardComboAdapter.LuckCardComboViewHolder> {

        private List<LuckCardCombo> mData;

        private LuckCardCombo mChecked;

        LuckCardComboAdapter(List<LuckCardCombo> data) {
            this.mData = data;
        }

        void replaceAll(final List<LuckCardCombo> data) {
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
        public LuckCardComboViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LuckCardComboViewHolder(parent, this);
        }

        @Override
        public void onBindViewHolder(LuckCardComboViewHolder holder, int position) {
            holder.bindTo(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        static class LuckCardComboViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.chb_luck_card_combo)
            CheckBox chb_luck_card_combo;
            @BindView(R.id.tv_luck_card_combo_time)
            TextView tv_luck_card_combo_time;
            @BindView(R.id.tv_luck_card_combo_price)
            TextView tv_luck_card_combo_price;

            private DecimalFormat mDecimalFormat;

            private LuckCardComboAdapter mAdapter;

            private LuckCardCombo mLuckCardCombo;

            LuckCardComboViewHolder(ViewGroup parent, LuckCardComboAdapter adapter) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.luck_card_combo_item, parent, false));
                ButterKnife.bind(this, this.itemView);
                this.mDecimalFormat = new DecimalFormat("0.00");
                this.mAdapter = adapter;

                chb_luck_card_combo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mLuckCardCombo != mAdapter.mChecked) {
                            mAdapter.mChecked = mLuckCardCombo;
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            void bindTo(LuckCardCombo luckCardCombo) {
                mLuckCardCombo = luckCardCombo;
                tv_luck_card_combo_time.setText(itemView.getContext().getString(R.string.minutes, luckCardCombo.cardTime / 60));
                tv_luck_card_combo_price.setText(String.format(Locale.getDefault(), "%s%s",
                        "￥", mDecimalFormat.format(luckCardCombo.cardPrice)));
                chb_luck_card_combo.setChecked(luckCardCombo == mAdapter.mChecked);
            }
        }
    }
}
