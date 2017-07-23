package com.lcjian.happyredenvelope.ui.room;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.LeftTimeInfo;
import com.lcjian.happyredenvelope.data.entity.Message;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.Room;
import com.lcjian.happyredenvelope.data.entity.User;
import com.lcjian.happyredenvelope.data.entity.Users;
import com.lcjian.happyredenvelope.ui.mine.RedEnvelopeHistoriesActivity;
import com.lcjian.lib.recyclerview.RecyclerViewPositionHelper;
import com.lcjian.lib.util.common.DimenUtils;
import com.lcjian.lib.util.common.StringUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func4;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RoomActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;
    @BindView(R.id.rv_room_members)
    RecyclerView rv_room_members;
    @BindView(R.id.rv_chat_messages)
    RecyclerView rv_chat_messages;

    @BindView(R.id.tv_room_member_count)
    TextView tv_room_member_count;
    @BindView(R.id.btn_invite_friend)
    Button btn_invite_friend;
    @BindView(R.id.ll_room_state_waiting)
    LinearLayout ll_room_state_waiting;
    @BindView(R.id.tv_time_left)
    TextView tv_time_left;
    @BindView(R.id.btn_go_red_envelope_histories)
    Button btn_go_red_envelope_histories;
    @BindView(R.id.ll_room_state_snatching)
    LinearLayout ll_room_state_snatching;

    private RoomMemberAdapter mRoomMemberAdapter;
    private RoomMessageAdapter mRoomMessageAdapter;

    private List<User> mMembers;
    private List<Message> mMessages;

    private boolean mJoined;

    private long mUserId;
    private long mRoomId;
    private Subscription mSubscriptionJoinRoom;
    private Subscription mSubscriptionExitRoom;
    private Subscription mSubscriptionRoomData;
    private Subscription mSubscriptionCounting;
    private CompositeSubscription mSubscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        ButterKnife.bind(this);
        mUserId = mUserInfoSp.getLong("user_id", 0);
        mRoomId = getIntent().getLongExtra("room_id", 0);
        mMembers = new ArrayList<>();
        mMessages = new ArrayList<>();

        tv_top_bar_right.setBackgroundResource(R.drawable.ic_room_member);
        tv_top_bar_right.setOnClickListener(this);
        btn_top_bar_left.setOnClickListener(this);
        btn_invite_friend.setOnClickListener(this);
        btn_go_red_envelope_histories.setOnClickListener(this);

        rv_room_members.setHasFixedSize(true);
        rv_room_members.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_room_members.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pixel1 = (int) DimenUtils.dipToPixels(4, parent.getContext());
                int pixel2 = (int) DimenUtils.dipToPixels(8, parent.getContext());
                outRect.set(pixel1, pixel2, pixel1, pixel2);
            }
        });
        mRoomMemberAdapter = new RoomMemberAdapter(null);
        rv_room_members.setAdapter(mRoomMemberAdapter);

        rv_chat_messages.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rv_chat_messages.setLayoutManager(layoutManager);
        mRoomMessageAdapter = new RoomMessageAdapter(null, getSupportFragmentManager());
        rv_chat_messages.setAdapter(mRoomMessageAdapter);

        mSubscriptions = new CompositeSubscription();
        ConnectableObservable<Object> eventEmitter = mRxBus.toObserverable().publish();
        mSubscriptions.add(eventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (TextUtils.equals("close_room_activity", event.toString())) {
                    onBackPressed();
                }
            }
        }));
        mSubscriptions.add(eventEmitter.connect());
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadRoomData();
    }

    @Override
    protected void onStop() {
        if (mSubscriptionRoomData != null) {
            mSubscriptionRoomData.unsubscribe();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        exitRoom();
        if (mSubscriptionRoomData != null) {
            mSubscriptionRoomData.unsubscribe();
        }
        if (mSubscriptionJoinRoom != null) {
            mSubscriptionJoinRoom.unsubscribe();
        }
        if (mSubscriptionCounting != null) {
            mSubscriptionCounting.unsubscribe();
        }
        if (mSubscriptions != null) {
            mSubscriptions.unsubscribe();
        }
        mRoomMessageAdapter.destroy();
        UMShareAPI.get(this).release();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_top_bar_left:
                onBackPressed();
                break;
            case R.id.tv_top_bar_right:
                startActivity(new Intent(this, RoomInfoActivity.class).putExtra("room_id", mRoomId));
                break;
            case R.id.btn_go_red_envelope_histories:
                startActivity(new Intent(this, RedEnvelopeHistoriesActivity.class));
                break;
            case R.id.btn_invite_friend:
                new ShareAction(this)
                        .withMedia(new UMWeb(
                                "http://www.baidu.com",
                                "我是标题",
                                "我是内容，描述内容。",
                                new UMImage(this, R.mipmap.ic_launcher)))
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(new UMShareListener() {
                            @Override
                            public void onStart(SHARE_MEDIA share_media) {
                            }

                            @Override
                            public void onResult(SHARE_MEDIA share_media) {
                            }

                            @Override
                            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                Toast.makeText(App.getInstance(), R.string.share_failed, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancel(SHARE_MEDIA share_media) {
                            }
                        }).share();
                break;
            default:
                break;
        }
    }

    private void joinRoom() {
        if (mSubscriptionJoinRoom != null) {
            mSubscriptionJoinRoom.unsubscribe();
        }
        mSubscriptionJoinRoom = mRestAPI.redEnvelopeService()
                .joinRoom(mUserId, mRoomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<Integer>>() {
                    @Override
                    public void call(ResponseData<Integer> integerResponseData) {
                        if (integerResponseData.code == 0) {
                            mJoined = true;
                            Toast.makeText(App.getInstance(),
                                    getString(R.string.joined_msg, integerResponseData.data), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void exitRoom() {
        if (mSubscriptionExitRoom != null) {
            mSubscriptionExitRoom.unsubscribe();
        }
        mSubscriptionExitRoom = mRestAPI.redEnvelopeService()
                .exitRoom(mUserId, mRoomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<String>>() {
                    @Override
                    public void call(ResponseData<String> stringResponseData) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private void loadRoomData() {
        if (mSubscriptionRoomData != null) {
            mSubscriptionRoomData.unsubscribe();
        }
        mSubscriptionRoomData = Observable
                .interval(0, 5, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Long, Observable<RoomData>>() {
                    @Override
                    public Observable<RoomData> call(Long aLong) {
                        return Observable.zip(
                                mRestAPI.redEnvelopeService().getLuckCardTotalLeftTime(mUserId).cache(),
                                mRestAPI.redEnvelopeService().getRoomDetail(mRoomId),
                                mRestAPI.redEnvelopeService().getRoomMembers(mUserId, mRoomId, 1, 100),
                                mRestAPI.redEnvelopeService().getAddedMsg(mUserId, mRoomId),
                                new Func4<ResponseData<LeftTimeInfo>, ResponseData<Room>, ResponseData<Users>, ResponseData<List<Message>>, RoomData>() {
                                    @Override
                                    public RoomData call(ResponseData<LeftTimeInfo> leftTimeInfoResponseData,
                                                         ResponseData<Room> roomResponseData,
                                                         ResponseData<Users> usersResponseData,
                                                         ResponseData<List<Message>> listResponseData) {
                                        if (listResponseData.code != 0) {
                                            throw new RuntimeException("you are kicked");
                                        }
                                        return new RoomData(leftTimeInfoResponseData.data,
                                                roomResponseData.data,
                                                usersResponseData.data.data,
                                                listResponseData.data);
                                    }
                                }
                        );
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RoomData>() {
                    @Override
                    public void call(RoomData roomData) {
                        if (roomData.leftTimeInfo.totalLeftTime <= 0) {
                            if (getSupportFragmentManager().findFragmentByTag("LuckCardInsufficientFragment") == null) {
                                new LuckCardInsufficientFragment().show(getSupportFragmentManager(), "LuckCardInsufficientFragment");
                            }
                            if (mSubscriptionRoomData != null) {
                                mSubscriptionRoomData.unsubscribe();
                            }
                        } else {
                            if (getSupportFragmentManager().findFragmentByTag("LuckCardInsufficientFragment") != null) {
                                LuckCardInsufficientFragment fragment = (LuckCardInsufficientFragment)
                                        getSupportFragmentManager().findFragmentByTag("LuckCardInsufficientFragment");
                                fragment.dismiss();
                            }
                            if (!mJoined) {
                                joinRoom();
                            }
                        }

                        tv_top_bar_title.setText(getString(R.string.room_title, roomData.room.name, roomData.room.nowNumber));

                        if (roomData.room.state == 1) {
                            boolean change = ll_room_state_snatching.getVisibility() == View.VISIBLE;
                            ll_room_state_waiting.setVisibility(View.VISIBLE);
                            ll_room_state_snatching.setVisibility(View.GONE);

                            tv_room_member_count.setText(getString(R.string.room_now_member_count, roomData.room.nowNumber));

                            if (change) {
                                if (mSubscriptionCounting != null) {
                                    mSubscriptionCounting.unsubscribe();
                                }
                            }
                        } else if (roomData.room.state == 2) {
                            boolean change = ll_room_state_snatching.getVisibility() == View.GONE;
                            ll_room_state_waiting.setVisibility(View.GONE);
                            ll_room_state_snatching.setVisibility(View.VISIBLE);

                            if (change) {
                                counting(roomData.leftTimeInfo.totalLeftTime);
                            }
                        }

                        if (roomData.users != null && !roomData.users.isEmpty()) {
                            mMembers.clear();
                            mMembers.addAll(roomData.users);
                            mRoomMemberAdapter.replaceAll(new ArrayList<>(mMembers));
                        }
                        if (roomData.newMessages != null && !roomData.newMessages.isEmpty()) {
                            int preFirstPosition = RecyclerViewPositionHelper.createHelper(rv_chat_messages).findFirstVisibleItemPosition();
                            mMessages.addAll(0, roomData.newMessages);
                            mRoomMessageAdapter.replaceAll(new ArrayList<>(mMessages));
                            if (preFirstPosition < 2) {
                                rv_chat_messages.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        rv_chat_messages.smoothScrollToPosition(0);
                                    }
                                });
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if ("you are kicked".equals(throwable.getMessage())) {
                            if (mJoined) {
                                mJoined = false;
                                Toast.makeText(App.getInstance(), R.string.your_are_kicked, Toast.LENGTH_SHORT).show();
                                if (mSubscriptionRoomData != null) {
                                    mSubscriptionRoomData.unsubscribe();
                                }
                                onBackPressed();
                            }
                        }
                    }
                });
    }

    private void counting(long leftTime) {
        if (mSubscriptionCounting != null) {
            mSubscriptionCounting.unsubscribe();
        }
        mSubscriptionCounting = Observable.combineLatest(
                Observable.interval(1, TimeUnit.SECONDS),
                Observable.just(leftTime),
                new Func2<Long, Long, Long>() {
                    @Override
                    public Long call(Long aLong, Long aLong2) {
                        return (aLong2 - aLong) < 0 ? 0 : (aLong2 - aLong) * 1000;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        String str = StringUtils.stringForTime(aLong.intValue(), true, true);
                        SpannableString spannableString = new SpannableString(str);
                        String[] array = str.split(":");
                        int start;
                        int end = 0;
                        for (int i = 0; i < array.length; i++) {
                            if (i == 0) {
                                start = 0;
                            } else {
                                start = end + 1;
                            }
                            end = start + array[i].length();
                            spannableString.setSpan(new BackgroundColorSpan(ContextCompat.getColor(RoomActivity.this, R.color.color_two)),
                                    start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        tv_time_left.setText(spannableString);
                    }
                });
    }

    private static class RoomData {

        private LeftTimeInfo leftTimeInfo;
        private Room room;
        private List<User> users;
        private List<Message> newMessages;

        private RoomData(LeftTimeInfo leftTimeInfo, Room room, List<User> users, List<Message> newMessages) {
            this.leftTimeInfo = leftTimeInfo;
            this.room = room;
            this.users = users;
            this.newMessages = newMessages;
        }
    }

}
