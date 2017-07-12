package com.lcjian.happyredenvelope.ui.room;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.lcjian.happyredenvelope.data.entity.User;
import com.lcjian.happyredenvelope.data.entity.Users;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RoomMemberManageActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;
    @BindView(R.id.chb_check_all)
    CheckBox chb_check_all;
    @BindView(R.id.btn_check_none)
    Button btn_check_none;
    @BindView(R.id.btn_delete)
    Button btn_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_member_manage);
        ButterKnife.bind(this);

        tv_top_bar_title.setText(R.string.delete_room_member);
        tv_top_bar_right.setVisibility(View.GONE);

        btn_top_bar_left.setOnClickListener(this);
        chb_check_all.setOnClickListener(this);
        btn_check_none.setOnClickListener(this);
        btn_delete.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container,
                RoomMemberManageFragment.newInstance(getIntent().getLongExtra("room_id", 0)), "RoomMemberManageFragment").commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_top_bar_left:
                onBackPressed();
                break;
            case R.id.chb_check_all: {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("RoomMemberManageFragment");
                if (fragment != null) {
                    ((RoomMemberManageFragment) fragment).selectAll();
                }
            }
            break;
            case R.id.btn_check_none: {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("RoomMemberManageFragment");
                if (fragment != null) {
                    ((RoomMemberManageFragment) fragment).selectNone();
                }
            }
            break;
            case R.id.btn_delete:
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("RoomMemberManageFragment");
                if (fragment != null) {
                    ((RoomMemberManageFragment) fragment).delete();
                }
                break;
            default:
                break;
        }
    }

    public static class RoomMemberManageFragment extends RecyclerFragment<User> {

        private long mRoomId;

        private RoomMemberManageAdapter mAdapter;

        public static RoomMemberManageFragment newInstance(long roomId) {
            RoomMemberManageFragment fragment = new RoomMemberManageFragment();
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
        public RecyclerView.Adapter onCreateAdapter(List<User> data) {
            mAdapter = new RoomMemberManageAdapter(data);
            return mAdapter;
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
                            result.elements = listResponseData.data.data;
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

            recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
            recycler_view.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                    .color(ContextCompat.getColor(getContext(), R.color.colorDivider))
                    .size(1)
                    .build());

            super.onViewCreated(view, savedInstanceState);
        }

        public void selectAll() {
            mAdapter.selectAll();
        }

        public void selectNone() {
            mAdapter.selectNone();
        }

        public void delete() {
            if (mAdapter.mChecked != null && !mAdapter.mChecked.isEmpty()) {
                List<String> userIds = new ArrayList<>();
                for (User user : mAdapter.mChecked) {
                    userIds.add(String.valueOf(user.userId));
                }
                mRestAPI.redEnvelopeService().deleteRoomMembers(mUserInfoSp.getLong("user_id", 0), mRoomId, TextUtils.join(",", userIds))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseData<String>>() {
                            @Override
                            public void call(ResponseData<String> stringResponseData) {
                                if (stringResponseData.code == 0) {
                                    mAdapter.mData.removeAll(mAdapter.mChecked);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
            }
        }
    }

    static class RoomMemberManageAdapter extends RecyclerView.Adapter<RoomMemberManageAdapter.MemberManageViewHolder> {

        private List<User> mData;

        private List<User> mChecked;

        RoomMemberManageAdapter(List<User> data) {
            this.mData = data;
            this.mChecked = new ArrayList<>();
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
        public MemberManageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MemberManageViewHolder(parent, this);
        }

        @Override
        public void onBindViewHolder(MemberManageViewHolder holder, int position) {
            holder.bindTo(mData.get(position));
        }

        private void selectAll() {
            mChecked.addAll(mData);
            notifyDataSetChanged();
        }

        private void selectNone() {
            mChecked.removeAll(mData);
            notifyDataSetChanged();
        }

        static class MemberManageViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.chb_check)
            CheckBox chb_check;
            @BindView(R.id.iv_user_avatar)
            ImageView iv_user_avatar;
            @BindView(R.id.tv_user_name)
            TextView tv_user_name;

            private User user;

            private RoomMemberManageAdapter mRoomMemberManageAdapter;

            MemberManageViewHolder(ViewGroup parent, RoomMemberManageAdapter roomMemberManageAdapter) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_member_manage_item, parent, false));
                ButterKnife.bind(this, this.itemView);
                this.mRoomMemberManageAdapter = roomMemberManageAdapter;
                chb_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mRoomMemberManageAdapter.mChecked.contains(user)) {
                            mRoomMemberManageAdapter.mChecked.remove(user);
                        } else {
                            mRoomMemberManageAdapter.mChecked.add(user);
                        }
                        mRoomMemberManageAdapter.notifyDataSetChanged();
                    }
                });
            }

            public void bindTo(User user) {
                this.user = user;
                if (mRoomMemberManageAdapter.mChecked.contains(user)) {
                    chb_check.setChecked(true);
                } else {
                    chb_check.setChecked(false);
                }
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
