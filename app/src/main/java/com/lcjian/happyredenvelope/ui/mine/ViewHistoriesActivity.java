package com.lcjian.happyredenvelope.ui.mine;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.entity.GoodsHistory;
import com.lcjian.happyredenvelope.data.entity.GroupHeader;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.RoomHistory;
import com.lcjian.happyredenvelope.data.entity.VideoHistory;
import com.lcjian.lib.entity.Displayable;
import com.lcjian.lib.recyclerview.LoadMoreAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

public class ViewHistoriesActivity extends BaseActivity {

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
        setContentView(R.layout.activity_view_histories);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        btn_top_bar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_top_bar_title.setText(R.string.view_history);
        tv_top_bar_right.setText(R.string.clear);
        tv_top_bar_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                if (mSubscription != null) {
                    mSubscription.unsubscribe();
                }
                mSubscription = mRestAPI.redEnvelopeService()
                        .cleanHistories(mUserInfoSp.getLong("user_id", 0))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseData<String>>() {
                            @Override
                            public void call(ResponseData<String> stringResponseData) {
                                mProgressDialog.dismiss();
                                if (stringResponseData.code == 0) {
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_view_histories_container,
                                            new ViewHistoriesFragment(), "ViewHistoriesFragment").commit();
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
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_view_histories_container,
                new ViewHistoriesFragment(), "ViewHistoriesFragment").commit();
    }

    @Override
    protected void onDestroy() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    public static class ViewHistoriesFragment extends RecyclerFragment<Displayable> {

        private ViewHistoryAdapter mAdapter;

        @Override
        public RecyclerView.Adapter onCreateAdapter(List<Displayable> data) {
            mAdapter = new ViewHistoryAdapter(data);
            return mAdapter;
        }

        @Override
        public void onLoadMoreAdapterCreated(LoadMoreAdapter loadMoreAdapter) {
            loadMoreAdapter.setEnabled(false);
        }

        @Override
        public Observable<PageResult<Displayable>> onCreatePageObservable(int currentPage) {
            return Observable.zip(
                    mRestAPI.redEnvelopeService().getRoomHistories(mUserInfoSp.getLong("user_id", 0), 2),
                    mRestAPI.redEnvelopeService().getGoodsHistories(mUserInfoSp.getLong("user_id", 0), 10),
                    mRestAPI.redEnvelopeService().getVideoHistories(mUserInfoSp.getLong("user_id", 0), 1, 10),
                    new Func3<ResponseData<List<RoomHistory>>, ResponseData<List<GoodsHistory>>, ResponseData<PageResult<VideoHistory>>, PageResult<Displayable>>() {
                        @Override
                        public PageResult<Displayable> call(ResponseData<List<RoomHistory>> listResponseData,
                                                            ResponseData<List<GoodsHistory>> listResponseData2,
                                                            ResponseData<PageResult<VideoHistory>> listResponseData3) {
                            PageResult<Displayable> result = new PageResult<>();
                            result.total_pages = 1;
                            result.elements = new ArrayList<>();
                            if (listResponseData.data != null && !listResponseData.data.isEmpty()) {
                                result.elements.add(new GroupHeader(getString(R.string.history_room)));
                                result.elements.addAll(listResponseData.data);
                            }
                            if (listResponseData2.data != null && !listResponseData2.data.isEmpty()) {
                                result.elements.add(new GroupHeader(getString(R.string.history_goods)));
                                result.elements.addAll(listResponseData2.data);
                            }
                            if (listResponseData3.data.elements != null && !listResponseData3.data.elements.isEmpty()) {
                                result.elements.add(new GroupHeader(getString(R.string.history_video)));
                                result.elements.addAll(listResponseData3.data.elements);
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

            recycler_view.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (recycler_view.getAdapter().getItemViewType(position) == 3
                            || recycler_view.getAdapter().getItemViewType(position) == 2) {
                        return 2;
                    } else {
                        return 1;
                    }
                }
            });
            recycler_view.setLayoutManager(gridLayoutManager);

            super.onViewCreated(view, savedInstanceState);
        }
    }

}
