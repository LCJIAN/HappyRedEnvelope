package com.lcjian.happyredenvelope.ui.room;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.RoomAndCreator;
import com.lcjian.happyredenvelope.data.entity.User;
import com.lcjian.happyredenvelope.data.entity.Users;
import com.lcjian.happyredenvelope.data.entity.VipInfo;
import com.lcjian.happyredenvelope.ui.mine.BuyVipActivity;
import com.lcjian.lib.recyclerview.LoadMoreAdapter;
import com.lcjian.lib.util.common.DimenUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RoomInfoActivity extends BaseActivity {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;

    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);
        ButterKnife.bind(this);

        btn_top_bar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_top_bar_right.setVisibility(View.GONE);

        mSubscription = mRestAPI.redEnvelopeService()
                .getRoomDetailAndCreator(getIntent().getLongExtra("room_id", 0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<RoomAndCreator>>() {
                    @Override
                    public void call(ResponseData<RoomAndCreator> roomResponseData) {
                        if (roomResponseData.code == 0) {
                            tv_top_bar_title.setText(getString(R.string.room_title,
                                    roomResponseData.data.hongBaoRoom.name, roomResponseData.data.hongBaoRoom.nowNumber));
                            boolean isManager = roomResponseData.data.hblUser != null
                                    && mUserInfoSp.getLong("user_id", 0) == roomResponseData.data.hblUser.userId;
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fl_fragment_container,
                                            RoomInfoFragment.newInstance(getIntent().getLongExtra("room_id", 0), isManager),
                                            "RoomInfoFragment").commit();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    public static class RoomInfoFragment extends RecyclerFragment<User> implements View.OnClickListener {

        private TextView tv_view_more_member;
        private TextView tv_room_luck_billboard;
        private TextView tv_invite_friend;
        private View v_be_a_vip_divider;
        private TextView tv_be_a_vip;
        private Button btn_exit_room;

        private long mRoomId;

        private boolean mIsManager;

        private RoomMemberGridAdapter mAdapter;

        private List<User> mMembers;

        private Subscription mVipSubscription;

        public static RoomInfoFragment newInstance(long roomId, boolean isManager) {
            RoomInfoFragment fragment = new RoomInfoFragment();
            Bundle args = new Bundle();
            args.putLong("room_id", roomId);
            args.putBoolean("is_manager", isManager);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mRoomId = getArguments().getLong("room_id");
                mIsManager = getArguments().getBoolean("is_manager");
            }
        }

        @Override
        public RecyclerView.Adapter onCreateAdapter(List<User> data) {
            mAdapter = new RoomMemberGridAdapter(data);
            return mAdapter;
        }

        @Override
        public void onLoadMoreAdapterCreated(LoadMoreAdapter loadMoreAdapter) {
            View footer = LayoutInflater.from(getContext()).inflate(R.layout.room_info_footer, recycler_view, false);
            tv_view_more_member = ButterKnife.findById(footer, R.id.tv_view_more_member);
            tv_room_luck_billboard = ButterKnife.findById(footer, R.id.tv_room_luck_billboard);
            tv_invite_friend = ButterKnife.findById(footer, R.id.tv_invite_friend);
            v_be_a_vip_divider = ButterKnife.findById(footer, R.id.v_be_a_vip_divider);
            tv_be_a_vip = ButterKnife.findById(footer, R.id.tv_be_a_vip);
            btn_exit_room = ButterKnife.findById(footer, R.id.btn_exit_room);

            tv_view_more_member.setOnClickListener(this);
            tv_room_luck_billboard.setOnClickListener(this);
            tv_invite_friend.setOnClickListener(this);
            tv_be_a_vip.setOnClickListener(this);
            btn_exit_room.setOnClickListener(this);
            loadMoreAdapter.addFooter(footer);
        }

        @Override
        public Observable<PageResult<User>> onCreatePageObservable(int currentPage) {
            return mRestAPI.redEnvelopeService()
                    .getRoomMembers(mUserInfoSp.getLong("user_id", 0), mRoomId, 1, 100)
                    .map(new Func1<ResponseData<Users>, PageResult<User>>() {
                        @Override
                        public PageResult<User> call(ResponseData<Users> listResponseData) {
                            PageResult<User> result = new PageResult<>();
                            result.total_pages = 1;
                            mMembers = listResponseData.data.data;
                            if (mMembers != null && mMembers.size() > 20) {
                                result.elements = mMembers.subList(0, 20);
                            } else {
                                result.elements = listResponseData.data.data;
                            }
                            return result;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        @Override
        public void notifyDataChanged(List<User> data) {
            mAdapter.replaceAll(data);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            swipe_refresh_layout.setColorSchemeResources(R.color.colorLightRed);

            recycler_view.setHasFixedSize(true);
            recycler_view.setLayoutManager(new GridLayoutManager(getContext(), 5));
            recycler_view.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int pixel1 = (int) DimenUtils.dipToPixels(4, parent.getContext());
                    outRect.set(pixel1, pixel1, pixel1, pixel1);
                }
            });
            if (mIsManager) {
                recycler_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(v.getContext(), RoomMemberManageActivity.class).putExtra("room_id", mRoomId));
                    }
                });
            }

            super.onViewCreated(view, savedInstanceState);

            mVipSubscription = mRestAPI.redEnvelopeService()
                    .isVip(mUserInfoSp.getLong("user_id", 0))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ResponseData<VipInfo>>() {
                        @Override
                        public void call(ResponseData<VipInfo> vipInfoResponseData) {
                            if (vipInfoResponseData.code == 0) {
                                if (!vipInfoResponseData.data.isvip) {
                                    v_be_a_vip_divider.setVisibility(View.VISIBLE);
                                    tv_be_a_vip.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        @Override
        public void onDestroyView() {
            if (mVipSubscription != null) {
                mVipSubscription.unsubscribe();
            }
            super.onDestroyView();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_view_more_member:
                    tv_view_more_member.setVisibility(View.GONE);
                    notifyDataChanged(mMembers);
                    break;
                case R.id.tv_room_luck_billboard:
                    startActivity(new Intent(v.getContext(), RoomLuckBillboardsActivity.class));
                    break;
                case R.id.tv_invite_friend:
                    break;
                case R.id.tv_be_a_vip:
                    startActivity(new Intent(v.getContext(), BuyVipActivity.class));
                    break;
                case R.id.btn_exit_room:
                    break;
                default:
                    break;
            }
        }
    }

    static class RoomMemberGridAdapter extends RecyclerView.Adapter<RoomMemberGridAdapter.MemberViewHolder> {

        private List<User> mData;

        RoomMemberGridAdapter(List<User> data) {
            this.mData = data;
        }

        void replaceAll(final List<User> data) {
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
                    return mData.get(oldItemPosition).userId == data.get(newItemPosition).userId;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return TextUtils.equals(mData.get(oldItemPosition).userHeadimg, data.get(newItemPosition).userHeadimg)
                            && TextUtils.equals(mData.get(oldItemPosition).userNickname, data.get(newItemPosition).userNickname);
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
        public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MemberViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(MemberViewHolder holder, int position) {
            holder.bindTo(mData.get(position));
        }

        static class MemberViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.iv_user_avatar)
            ImageView iv_user_avatar;
            @BindView(R.id.tv_user_name)
            TextView tv_user_name;

            MemberViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_member_grid_item, parent, false));
                ButterKnife.bind(this, this.itemView);
            }

            public void bindTo(User user) {
                Glide.with(itemView.getContext())
                        .load(user.userHeadimg)
                        .apply(Global.userAvatar)
                        .transition(Global.crossFade)
                        .into(iv_user_avatar);
                tv_user_name.setText(user.userNickname);
            }
        }
    }
}
