package com.lcjian.happyredenvelope.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.widget.RadioGroup;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.AppLinks;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.lib.util.FragmentSwitchHelper;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.socialize.UMShareAPI;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.rg_bottom_navigation)
    RadioGroup rg_bottom_navigation;

    private FragmentSwitchHelper mFragmentSwitchHelper;

    private Subscription mSubscription;

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

        mSubscription = mRestAPI.redEnvelopeService()
                .getAppLinks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<AppLinks>>() {
                    @Override
                    public void call(ResponseData<AppLinks> appLinksResponseData) {
                        if (appLinksResponseData.code == 0) {
                            AppLinks appLinks = appLinksResponseData.data;
                            mUserInfoSp.edit()
                                    .putString("luck_card_tutorial_link", appLinks.fukaManualLink)
                                    .putString("helping_center_link", appLinks.helpCenterLink)
                                    .putString("red_envelop_sponsor_link", appLinks.hongbaoCooperatorLink)
                                    .putString("hot_app_link", appLinks.hotAppLink)
                                    .putString("tao_bao_ticket_Link", appLinks.taobaoTicketLink)
                                    .putString("withdraw_tutorial_link", appLinks.withdrawLink)
                                    .apply();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        if (mUserInfoSp.getLong("user_id", 0) != 0) {
            PushAgent.getInstance(App.getInstance())
                    .addAlias(
                            String.valueOf(mUserInfoSp.getLong("user_id", 0)),
                            "we_chat",
                            new UTrack.ICallBack() {
                                @Override
                                public void onMessage(boolean isSuccess, String message) {
                                }
                            });
        }
    }

    @Override
    protected void onDestroy() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.onDestroy();
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
