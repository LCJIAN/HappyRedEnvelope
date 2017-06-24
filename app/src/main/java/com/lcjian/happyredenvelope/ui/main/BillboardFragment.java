package com.lcjian.happyredenvelope.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lcjian.happyredenvelope.BaseFragment;
import com.lcjian.happyredenvelope.R;
import com.lcjian.lib.content.SimpleFragmentPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BillboardFragment extends BaseFragment {

    @BindView(R.id.tab_billboard)
    TabLayout tab_billboard;
    @BindView(R.id.vp_billboard)
    ViewPager vp_billboard;

    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_billboard, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        SimpleFragmentPagerAdapter pagerAdapter = new SimpleFragmentPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(BillboardsFragment.newInstance(1), getString(R.string.day_billboard));
        pagerAdapter.addFragment(BillboardsFragment.newInstance(2), getString(R.string.week_billboard));
        pagerAdapter.addFragment(BillboardsFragment.newInstance(3), getString(R.string.month_billboard));
        vp_billboard.setAdapter(pagerAdapter);
        tab_billboard.setupWithViewPager(vp_billboard);
        tab_billboard.setSelectedTabIndicatorColor(Color.WHITE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
