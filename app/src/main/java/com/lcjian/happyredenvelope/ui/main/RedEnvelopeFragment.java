package com.lcjian.happyredenvelope.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.PopupWindowCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseFragment;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.RedEnvHot;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.ui.mine.UserLuckCardActivity;
import com.lcjian.happyredenvelope.ui.room.CreateRoomActivity;
import com.lcjian.happyredenvelope.ui.search.SearchActivity;
import com.lcjian.happyredenvelope.ui.web.WebViewActivity;
import com.lcjian.lib.content.SimpleFragmentPagerAdapter;
import com.lcjian.lib.viewpager.AutoViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RedEnvelopeFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.tab_red_envelope)
    TabLayout tab_red_envelope;
    @BindView(R.id.vp_red_envelope)
    ViewPager vp_red_envelope;
    @BindView(R.id.btn_go_search)
    ImageButton btn_go_search;
    @BindView(R.id.btn_go_add_room)
    ImageButton btn_go_add_room;
    @BindView(R.id.vp_red_env_hot)
    AutoViewPager vp_red_env_hot;
    @BindView(R.id.tv_view_red_env_hot)
    TextView tv_view_red_env_hot;
    Unbinder unbinder;

    TextView tv_create_room;
    TextView tv_buy_luck_card;

    private List<RedEnvHot> mRedEnvHots;

    private Subscription mSubscription;

    private PopupWindow mPopupWindow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_red_envelope, container, false);
        unbinder = ButterKnife.bind(this, view);

        View viewPopupWindow = LayoutInflater.from(getContext()).inflate(R.layout.popup_window_create_room,
                (ViewGroup) getActivity().getWindow().getDecorView(), false);
        tv_create_room = (TextView) viewPopupWindow.findViewById(R.id.tv_create_room);
        tv_buy_luck_card = (TextView) viewPopupWindow.findViewById(R.id.tv_buy_luck_card);
        mPopupWindow = new PopupWindow(
                viewPopupWindow,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                btn_go_add_room.setImageResource(R.drawable.ic_add);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        btn_go_search.setOnClickListener(this);
        btn_go_add_room.setOnClickListener(this);
        tv_create_room.setOnClickListener(this);
        tv_buy_luck_card.setOnClickListener(this);
        tv_view_red_env_hot.setOnClickListener(this);


        SimpleFragmentPagerAdapter pagerAdapter = new SimpleFragmentPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(RoomsFragment.newInstance(false), getString(R.string.normal_room));
        pagerAdapter.addFragment(RoomsFragment.newInstance(true), getString(R.string.vip_room));
        vp_red_envelope.setAdapter(pagerAdapter);
        tab_red_envelope.setupWithViewPager(vp_red_envelope);
        tab_red_envelope.setSelectedTabIndicatorColor(Color.WHITE);

        mSubscription = mRestAPI.redEnvelopeService().getRedEnvHots()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<List<RedEnvHot>>>() {
                    @Override
                    public void call(ResponseData<List<RedEnvHot>> listResponseData) {
                        if (listResponseData.code == 0) {
                            mRedEnvHots = listResponseData.data;
                            vp_red_env_hot.setAdapter(new RedEnvHotAdapter(mRedEnvHots));
                            vp_red_env_hot.setOffscreenPageLimit(2);
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
        super.onDestroyView();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_go_add_room: {
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                } else {
                    btn_go_add_room.setImageResource(R.drawable.ic_close_2);
                    PopupWindowCompat.showAsDropDown(mPopupWindow, btn_go_add_room, 0, 0, Gravity.BOTTOM);
                }
            }
            break;
            case R.id.tv_create_room: {
                mPopupWindow.dismiss();
                startActivity(new Intent(v.getContext(), CreateRoomActivity.class));
            }
            break;
            case R.id.btn_go_search: {
                startActivity(new Intent(v.getContext(), SearchActivity.class));
            }
            break;
            case R.id.tv_buy_luck_card: {
                mPopupWindow.dismiss();
                startActivity(new Intent(v.getContext(), UserLuckCardActivity.class));
            }
            break;
            case R.id.tv_view_red_env_hot: {
                if (mRedEnvHots != null && !mRedEnvHots.isEmpty()) {
                    startActivity(new Intent(v.getContext(), WebViewActivity.class)
                            .putExtra("url", mRedEnvHots.get(vp_red_env_hot.getCurrentItem()).link));
                }
            }
            break;
            default:
                break;
        }
    }

    private static class RedEnvHotAdapter extends PagerAdapter {

        private List<RedEnvHot> mData;

        private List<View> mRecycledViews;

        private RedEnvHotAdapter(List<RedEnvHot> data) {
            this.mData = data;
            this.mRecycledViews = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view;
            if (mRecycledViews.isEmpty()) {
                view = LayoutInflater.from(container.getContext()).inflate(R.layout.red_envelope_hot_item, container, false);
            } else {
                view = mRecycledViews.get(0);
                mRecycledViews.remove(0);
            }
            final RedEnvHot redEnvHot = mData.get(position);
            TextView tv_hot_title = (TextView) view.findViewById(R.id.tv_hot_title);
            tv_hot_title.setText(redEnvHot.name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(v.getContext(), WebViewActivity.class).putExtra("url", redEnvHot.link));
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            mRecycledViews.add((View) object);
        }
    }
}
