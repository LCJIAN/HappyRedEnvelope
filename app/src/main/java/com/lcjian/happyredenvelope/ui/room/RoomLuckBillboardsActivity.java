package com.lcjian.happyredenvelope.ui.room;

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

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.RoomBillboard;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RoomLuckBillboardsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_luck_billboards);
        ButterKnife.bind(this);
        btn_top_bar_left.setOnClickListener(this);
        tv_top_bar_title.setText(R.string.room_luck_billboard);
        tv_top_bar_right.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_room_luck_billboards_container,
                RoomLuckBillboardsFragment.newInstance(getIntent().getLongExtra("room_id", 0)), "RoomLuckBillboardsFragment").commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_top_bar_left: {
                onBackPressed();
            }
            break;
            default:
                break;
        }
    }

    public static class RoomLuckBillboardsFragment extends RecyclerFragment<RoomBillboard> {

        private long mRoomId;

        private RoomLuckBillboardAdapter mAdapter;

        public static RoomLuckBillboardsFragment newInstance(long roomId) {
            RoomLuckBillboardsFragment fragment = new RoomLuckBillboardsFragment();
            Bundle args = new Bundle();
            args.putLong("room_id", roomId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mRoomId = getArguments().getLong("room_id");
            }
        }

        @Override
        public RecyclerView.Adapter onCreateAdapter(List<RoomBillboard> data) {
            mAdapter = new RoomLuckBillboardAdapter(data);
            return mAdapter;
        }

        @Override
        public Observable<PageResult<RoomBillboard>> onCreatePageObservable(int currentPage) {
            return mRestAPI.redEnvelopeService()
                    .getRoomBillboards(mRoomId)
                    .map(new Func1<ResponseData<List<RoomBillboard>>, PageResult<RoomBillboard>>() {
                        @Override
                        public PageResult<RoomBillboard> call(ResponseData<List<RoomBillboard>> pageResultResponseData) {
                            PageResult<RoomBillboard> result = new PageResult<>();
                            result.total_pages = 1;
                            result.elements = pageResultResponseData.data;
                            return result;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        @Override
        public void notifyDataChanged(List<RoomBillboard> data) {
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

    static class RoomLuckBillboardAdapter extends RecyclerView.Adapter<RoomLuckBillboardAdapter.RoomLuckBillboardViewHolder> {

        private List<RoomBillboard> mData;

        RoomLuckBillboardAdapter(List<RoomBillboard> data) {
            this.mData = data;
        }

        void replaceAll(final List<RoomBillboard> data) {
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
        public RoomLuckBillboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RoomLuckBillboardViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(RoomLuckBillboardViewHolder holder, int position) {
            holder.bindTo(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        static class RoomLuckBillboardViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.iv_user_avatar)
            ImageView iv_user_avatar;
            @BindView(R.id.tv_user_name)
            TextView tv_user_name;
            @BindView(R.id.tv_luckiest_time)
            TextView tv_luckiest_time;
            @BindView(R.id.iv_red_envelop_count)
            TextView iv_red_envelop_count;

            RoomLuckBillboardViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_lucky_billboard_item, parent, false));
                ButterKnife.bind(this, this.itemView);
            }

            void bindTo(RoomBillboard roomBillboard) {
                Context context = itemView.getContext();
                Glide.with(context)
                        .load(roomBillboard.headImg)
                        .apply(Global.userAvatar)
                        .transition(Global.crossFade)
                        .into(iv_user_avatar);

                tv_user_name.setText(roomBillboard.nickname);
                tv_luckiest_time.setText(context.getString(R.string.luckiest_times, roomBillboard.luckiestCount));
                iv_red_envelop_count.setText(String.valueOf(roomBillboard.HongbaoCount));
            }
        }
    }
}
