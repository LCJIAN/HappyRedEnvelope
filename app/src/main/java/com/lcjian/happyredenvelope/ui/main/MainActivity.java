package com.lcjian.happyredenvelope.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.lcjian.happyredenvelope.R;
import com.lcjian.lib.util.FragmentSwitchHelper;
import com.umeng.socialize.UMShareAPI;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.rg_bottom_navigation)
    RadioGroup rg_bottom_navigation;

    private FragmentSwitchHelper mFragmentSwitchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        rg_bottom_navigation.setOnCheckedChangeListener(this);
        mFragmentSwitchHelper = FragmentSwitchHelper.create(
                R.id.fl_main_fragment_container,
                getSupportFragmentManager(),
                new RedEnvelopeFragment(),
                new BillboardFragment(),
                new ExploreFragment(),
                new MineFragment());
        mFragmentSwitchHelper.changeFragment(RedEnvelopeFragment.class);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_red_envelope: {
                mFragmentSwitchHelper.changeFragment(RedEnvelopeFragment.class);
            }
            break;
            case R.id.rb_billboard: {
                mFragmentSwitchHelper.changeFragment(BillboardFragment.class);
            }
            break;
            case R.id.rb_explore: {
                mFragmentSwitchHelper.changeFragment(ExploreFragment.class);
            }
            break;
            case R.id.rb_mine: {
                mFragmentSwitchHelper.changeFragment(MineFragment.class);
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
