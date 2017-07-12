package com.lcjian.happyredenvelope.ui.withdrawal;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.Withdrawal;
import com.lcjian.lib.util.common.DateUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.text.DecimalFormat;
import java.util.Date;
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

public class WithdrawalHistoriesActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;

    private Subscription mSubscription;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal_histories);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        tv_top_bar_title.setText(R.string.withdrawal_history);
        tv_top_bar_right.setText(R.string.clear);
        tv_top_bar_right.setOnClickListener(this);
        btn_top_bar_left.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_withdrawal_histories_container,
                new WithdrawalHistoriesFragment(), "WithdrawalHistoriesFragment").commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_top_bar_left: {
                onBackPressed();
            }
            break;
            case R.id.tv_top_bar_right: {
                mProgressDialog.show();
                mSubscription = mRestAPI.redEnvelopeService()
                        .cleanWithdrawalHistories(mUserInfoSp.getLong("user_id", 0))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseData<String>>() {
                            @Override
                            public void call(ResponseData<String> stringResponseData) {
                                mProgressDialog.dismiss();
                                if (stringResponseData.code == 0) {
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_withdrawal_histories_container,
                                            new WithdrawalHistoriesFragment(), "WithdrawalHistoriesFragment").commit();
                                } else {
                                    Toast.makeText(App.getInstance(), stringResponseData.msg, Toast.LENGTH_SHORT).show();
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

    public static class WithdrawalHistoriesFragment extends RecyclerFragment<Withdrawal> {

        private WithdrawalHistoryAdapter mAdapter;

        @Override
        public RecyclerView.Adapter onCreateAdapter(List<Withdrawal> data) {
            mAdapter = new WithdrawalHistoryAdapter(data);
            return mAdapter;
        }

        @Override
        public Observable<PageResult<Withdrawal>> onCreatePageObservable(int currentPage) {
            return mRestAPI.redEnvelopeService()
                    .getWithdrawalHistories(mUserInfoSp.getLong("user_id", 0), currentPage, 10)
                    .map(new Func1<ResponseData<PageResult<Withdrawal>>, PageResult<Withdrawal>>() {
                        @Override
                        public PageResult<Withdrawal> call(ResponseData<PageResult<Withdrawal>> pageResultResponseData) {
                            return pageResultResponseData.data;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        @Override
        public void notifyDataChanged(List<Withdrawal> data) {
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

    static class WithdrawalHistoryAdapter extends RecyclerView.Adapter<WithdrawalHistoryAdapter.WithdrawalViewHolder> {

        private List<Withdrawal> mWithdrawalHistories;

        WithdrawalHistoryAdapter(List<Withdrawal> withdrawalHistories) {
            this.mWithdrawalHistories = withdrawalHistories;
        }

        void replaceAll(final List<Withdrawal> data) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {

                @Override
                public int getOldListSize() {
                    return mWithdrawalHistories == null ? 0 : mWithdrawalHistories.size();
                }

                @Override
                public int getNewListSize() {
                    return data == null ? 0 : data.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mWithdrawalHistories.get(oldItemPosition) == data.get(newItemPosition);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return mWithdrawalHistories.get(oldItemPosition) == data.get(newItemPosition);
                }
            }, true);
            this.mWithdrawalHistories = data;
            diffResult.dispatchUpdatesTo(this);
        }

        @Override
        public WithdrawalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WithdrawalViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(WithdrawalViewHolder holder, int position) {
            holder.bindTo(mWithdrawalHistories.get(position));
        }

        @Override
        public int getItemCount() {
            return mWithdrawalHistories == null ? 0 : mWithdrawalHistories.size();
        }

        static class WithdrawalViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.iv_withdrawal_status)
            ImageView iv_withdrawal_status;
            @BindView(R.id.tv_withdrawal_status)
            TextView tv_withdrawal_status;
            @BindView(R.id.tv_withdrawal_amount)
            TextView tv_withdrawal_amount;
            @BindView(R.id.tv_withdrawal_time)
            TextView tv_withdrawal_time;
            @BindView(R.id.tv_withdrawal_no)
            TextView tv_withdrawal_no;

            private DecimalFormat mDecimalFormat;

            WithdrawalViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.withdrawal_history_item, parent, false));
                ButterKnife.bind(this, this.itemView);
                this.mDecimalFormat = new DecimalFormat("0.00");
            }

            void bindTo(Withdrawal withdrawal) {
                Context context = itemView.getContext();
                if (withdrawal.token.tokenIsexpired) {
                    iv_withdrawal_status.setImageResource(R.drawable.ic_withdrawal_expired);
                    tv_withdrawal_status.setText(R.string.withdrawal_expired);
                    tv_withdrawal_amount.setTextColor(ContextCompat.getColor(context, R.color.colorTextBlack));
                } else {
                    iv_withdrawal_status.setImageResource(R.drawable.ic_withdrawal_success);
                    tv_withdrawal_status.setText(R.string.withdrawal_success);
                    tv_withdrawal_amount.setTextColor(ContextCompat.getColor(context, R.color.colorDarkRed));
                }
                tv_withdrawal_amount.setText(String.format(Locale.getDefault(), "%s%s", "-￥", mDecimalFormat.format(withdrawal.withdrawAmount)));
                tv_withdrawal_time.setText(DateUtils.convertDateToStr(new Date(withdrawal.withdrawCreatetime), "yyyy-MM-dd HH:mm"));
                tv_withdrawal_no.setText(withdrawal.token.tokenContent);
            }
        }
    }
}
