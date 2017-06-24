package com.lcjian.happyredenvelope.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.lcjian.happyredenvelope.BaseFragment;
import com.lcjian.happyredenvelope.R;
import com.lcjian.lib.content.SimpleFragmentPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RedEnvelopeFragment extends BaseFragment {

    @BindView(R.id.tab_red_envelope)
    TabLayout tab_red_envelope;
    @BindView(R.id.vp_red_envelope)
    ViewPager vp_red_envelope;
    @BindView(R.id.btn_go_search)
    ImageButton btn_go_search;
    @BindView(R.id.btn_go_add_room)
    ImageButton btn_go_add_room;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_red_envelope, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        SimpleFragmentPagerAdapter pagerAdapter = new SimpleFragmentPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(RoomsFragment.newInstance(false), getString(R.string.normal_room));
        pagerAdapter.addFragment(RoomsFragment.newInstance(true), getString(R.string.vip_room));
        vp_red_envelope.setAdapter(pagerAdapter);
        tab_red_envelope.setupWithViewPager(vp_red_envelope);
        tab_red_envelope.setSelectedTabIndicatorColor(Color.WHITE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
