package com.lcjian.happyredenvelope.ui.search;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.RxBus;
import com.lcjian.happyredenvelope.common.GoodsViewHolder;
import com.lcjian.happyredenvelope.common.GroupHeaderViewHolder;
import com.lcjian.happyredenvelope.common.RoomViewHolder;
import com.lcjian.happyredenvelope.data.db.table.SearchHistoryTable;
import com.lcjian.happyredenvelope.data.entity.Goods;
import com.lcjian.happyredenvelope.data.entity.GroupHeader;
import com.lcjian.happyredenvelope.data.entity.Room;
import com.lcjian.happyredenvelope.data.entity.SearchHistory;
import com.lcjian.happyredenvelope.data.entity.SearchHistoryHeader;
import com.lcjian.lib.entity.Displayable;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ROOM = 0;
    private static final int TYPE_GOODS = 1;
    private static final int TYPE_SEARCH_HISTORY = 2;
    private static final int TYPE_SEARCH_HISTORY_HEADER = 3;
    private static final int TYPE_GROUP_HEADER = 4;

    private List<Displayable> mData;

    private SharedPreferences mUserInfoSp;
    private FragmentManager mFragmentManager;
    private StorIOSQLite mStorIOSQLite;
    private RxBus mRxBus;

    SearchAdapter(List<Displayable> data, RxBus rxBus,
                  StorIOSQLite storIOSQLite, SharedPreferences userInfoSp, FragmentManager fragmentManager) {
        this.mData = data;
        this.mRxBus = rxBus;
        this.mStorIOSQLite = storIOSQLite;
        this.mUserInfoSp = userInfoSp;
        this.mFragmentManager = fragmentManager;
    }

    void replaceAll(final List<Displayable> data) {
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
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) instanceof Room ? TYPE_ROOM
                : (mData.get(position) instanceof Goods ? TYPE_GOODS
                : (mData.get(position) instanceof SearchHistory ? TYPE_SEARCH_HISTORY
                : (mData.get(position) instanceof SearchHistoryHeader ? TYPE_SEARCH_HISTORY_HEADER : TYPE_GROUP_HEADER)));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == TYPE_ROOM ? new RoomViewHolder(parent, mUserInfoSp, mFragmentManager)
                : (viewType == TYPE_GOODS ? new GoodsViewHolder(parent)
                : (viewType == TYPE_SEARCH_HISTORY ? new SearchHistoryViewHolder(parent, mRxBus)
                : (viewType == TYPE_SEARCH_HISTORY_HEADER ? new SearchHistoryHeaderViewHolder(parent, mRxBus, mStorIOSQLite)
                : new GroupHeaderViewHolder(parent))));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RoomViewHolder) {
            ((RoomViewHolder) holder).bindTo((Room) mData.get(position));
        } else if (holder instanceof GoodsViewHolder) {
            ((GoodsViewHolder) holder).bindTo((Goods) mData.get(position));
        } else if (holder instanceof SearchHistoryViewHolder) {
            ((SearchHistoryViewHolder) holder).bindTo((SearchHistory) mData.get(position));
        } else if (holder instanceof SearchHistoryHeaderViewHolder) {
            ((SearchHistoryHeaderViewHolder) holder).bindTo((SearchHistoryHeader) mData.get(position));
        } else if (holder instanceof GroupHeaderViewHolder) {
            ((GroupHeaderViewHolder) holder).bindTo((GroupHeader) mData.get(position));
        }
    }

    static class SearchHistoryHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_search_history_title)
        TextView tv_search_history_title;
        @BindView(R.id.btn_delete_histories)
        ImageButton btn_delete_histories;
        @BindView(R.id.tv_no_data)
        TextView tv_no_data;

        RxBus mRxBus;
        StorIOSQLite mStorIOSQLite;

        SearchHistoryHeaderViewHolder(ViewGroup parent, RxBus rxBus, StorIOSQLite storIOSQLite) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_header_item, parent, false));
            ButterKnife.bind(this, this.itemView);
            this.mRxBus = rxBus;
            this.mStorIOSQLite = storIOSQLite;
            btn_delete_histories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStorIOSQLite.delete()
                            .byQuery(DeleteQuery.builder().table(SearchHistoryTable.TABLE_NAME).build())
                            .prepare()
                            .asRxObservable()
                            .subscribe(new Action1<DeleteResult>() {
                                @Override
                                public void call(DeleteResult deleteResult) {
                                    mRxBus.send("refresh");
                                }
                            });
                }
            });
        }

        void bindTo(SearchHistoryHeader searchHistoryHeader) {
            tv_search_history_title.setText(searchHistoryHeader.title);
            tv_no_data.setText(searchHistoryHeader.noDataText);
            tv_no_data.setVisibility(searchHistoryHeader.noData ? View.VISIBLE : View.GONE);
        }
    }

    static class SearchHistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_room_id)
        TextView tv_room_id;

        SearchHistory searchHistory;

        RxBus mRxBus;

        SearchHistoryViewHolder(ViewGroup parent, RxBus rxBus) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_item, parent, false));
            ButterKnife.bind(this, this.itemView);
            this.mRxBus = rxBus;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRxBus.send(new SearchActivity.SearchAction(searchHistory.text));
                }
            });
        }

        void bindTo(SearchHistory searchHistory) {
            this.searchHistory = searchHistory;
            tv_room_id.setText(searchHistory.text);
        }
    }
}
