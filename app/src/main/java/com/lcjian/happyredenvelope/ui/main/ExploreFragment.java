package com.lcjian.happyredenvelope.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseFragment;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.Banner;
import com.lcjian.happyredenvelope.data.entity.Explore;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.ui.web.WebViewActivity;
import com.lcjian.lib.viewpager.AutoViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ExploreFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.vp_banner)
    AutoViewPager vp_banner;
    @BindView(R.id.tv_explore_one)
    TextView tv_explore_one;
    @BindView(R.id.tv_explore_two)
    TextView tv_explore_two;
    @BindView(R.id.tv_explore_three)
    TextView tv_explore_three;
    @BindView(R.id.tv_explore_four)
    TextView tv_explore_four;
    Unbinder unbinder;

    private CompositeSubscription mSubscriptions;

    private Explore mExplore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        tv_explore_one.setOnClickListener(this);
        tv_explore_two.setOnClickListener(this);
        tv_explore_three.setOnClickListener(this);
        tv_explore_four.setOnClickListener(this);

        mSubscriptions = new CompositeSubscription();
        mSubscriptions.add(mRestAPI.redEnvelopeService().getBanners()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<List<Banner>>>() {
                    @Override
                    public void call(ResponseData<List<Banner>> listResponseData) {
                        vp_banner.setAdapter(new BannerAdapter(listResponseData.data));
                        vp_banner.setOffscreenPageLimit(2);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
        mSubscriptions.add(mRestAPI.redEnvelopeService().getExplore()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<Explore>>() {
                    @Override
                    public void call(ResponseData<Explore> exploreResponseData) {
                        mExplore = exploreResponseData.data;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (mSubscriptions != null) {
            mSubscriptions.unsubscribe();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_explore_one: {
                if (mExplore != null) {
                    startActivity(new Intent(getContext(), WebViewActivity.class).putExtra("url", mExplore.temai));
                }
            }
            break;
            case R.id.tv_explore_two: {
                if (mExplore != null) {
                    startActivity(new Intent(getContext(), WebViewActivity.class).putExtra("url", mExplore.ticket));
                }
            }
            break;
            case R.id.tv_explore_three: {
                if (mExplore != null) {
                    startActivity(new Intent(getContext(), WebViewActivity.class).putExtra("url", mExplore.news));
                }
            }
            break;
            case R.id.tv_explore_four: {
                if (mExplore != null) {
                    startActivity(new Intent(getContext(), WebViewActivity.class).putExtra("url", mExplore.apps));
                }
            }
            break;
            default:
                break;
        }
    }

    private static class BannerAdapter extends PagerAdapter {

        private List<Banner> mData;

        private List<View> mRecycledViews;

        private BannerAdapter(List<Banner> data) {
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
                view = new ImageView(container.getContext());
                view.setLayoutParams(new ViewPager.LayoutParams());
            } else {
                view = mRecycledViews.get(0);
                mRecycledViews.remove(0);
            }
            final Banner banner = mData.get(position);

            Glide.with(container.getContext())
                    .load(banner.img)
                    .apply(RequestOptions.centerCropTransform())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into((ImageView) view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(v.getContext(), WebViewActivity.class).putExtra("url", banner.url));
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
