package com.lcjian.happyredenvelope.ui.search;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.GroupHeaderViewHolder;
import com.lcjian.happyredenvelope.common.RoomViewHolder;
import com.lcjian.happyredenvelope.data.entity.Goods;
import com.lcjian.happyredenvelope.data.entity.GroupHeader;
import com.lcjian.happyredenvelope.data.entity.Room;
import com.lcjian.happyredenvelope.data.entity.SearchHistory;
import com.lcjian.happyredenvelope.data.entity.SearchHistoryHeader;
import com.lcjian.lib.entity.Displayable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ROOM = 0;
    private static final int TYPE_GOODS = 1;
    private static final int TYPE_SEARCH_HISTORY = 2;
    private static final int TYPE_SEARCH_HISTORY_HEADER = 3;
    private static final int TYPE_GROUP_HEADER = 4;

    private List<Displayable> mData;

    SearchAdapter(List<Displayable> data) {
        this.mData = data;
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
        return viewType == TYPE_ROOM ? new RoomViewHolder(parent)
                : (viewType == TYPE_GOODS ? new GoodsViewHolder(parent)
                : (viewType == TYPE_SEARCH_HISTORY ? new SearchHistoryViewHolder(parent)
                : (viewType == TYPE_SEARCH_HISTORY_HEADER ? new SearchHistoryHeaderViewHolder(parent)
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

    static class GoodsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_goods_avatar)
        ImageView iv_goods_avatar;
        @BindView(R.id.tv_goods_name)
        TextView tv_goods_name;
        @BindView(R.id.tv_goods_price)
        TextView tv_goods_price;
        @BindView(R.id.tv_goods_sale_count)
        TextView tv_goods_sale_count;

        Goods goods;

        private DecimalFormat mDecimalFormat;

        GoodsViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_item, parent, false));
            ButterKnife.bind(this, this.itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            this.mDecimalFormat = new DecimalFormat("0.00");
        }

        void bindTo(Goods goods) {
            this.goods = goods;
            Context context = itemView.getContext();
            Glide.with(context)
                    .load(goods.pic)
                    .apply(RequestOptions.placeholderOf(R.drawable.shape_room_no_avatar_bg))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(iv_goods_avatar);
            tv_goods_name.setText(goods.title);
            tv_goods_price.setText(String.format(Locale.getDefault(), "%s%s", "￥", mDecimalFormat.format(goods.price)));
            tv_goods_sale_count.setText(context.getString(R.string.sale_count, goods.saleCount));
        }
    }

    static class SearchHistoryHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_search_history_title)
        TextView tv_search_history_title;
        @BindView(R.id.btn_delete_histories)
        ImageButton btn_delete_histories;
        @BindView(R.id.tv_no_data)
        TextView tv_no_data;

        SearchHistoryHeaderViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_header_item, parent, false));
            ButterKnife.bind(this, this.itemView);
            btn_delete_histories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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

        SearchHistoryViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_item, parent, false));
            ButterKnife.bind(this, this.itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        void bindTo(SearchHistory searchHistory) {
            this.searchHistory = searchHistory;
            tv_room_id.setText(searchHistory.text);
        }
    }
}
