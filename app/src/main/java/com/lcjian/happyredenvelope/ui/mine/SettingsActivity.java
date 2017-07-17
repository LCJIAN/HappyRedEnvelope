package com.lcjian.happyredenvelope.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.ui.main.MainActivity;
import com.lcjian.lib.util.common.FileUtils;
import com.lcjian.lib.util.common.StorageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SettingsActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;
    @BindView(R.id.rl_clean_cache)
    RelativeLayout rl_clean_cache;
    @BindView(R.id.btn_sign_out)
    Button btn_sign_out;

    @BindView(R.id.switch_turn_off_msg)
    SwitchCompat switch_turn_off_msg;
    @BindView(R.id.switch_turn_on_zip)
    SwitchCompat switch_turn_on_zip;
    @BindView(R.id.switch_msg_notification)
    SwitchCompat switch_msg_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        tv_top_bar_title.setText(R.string.settings);
        tv_top_bar_right.setVisibility(View.GONE);
        btn_top_bar_left.setOnClickListener(this);
        rl_clean_cache.setOnClickListener(this);
        btn_sign_out.setOnClickListener(this);

        switch_turn_off_msg.setOnCheckedChangeListener(this);
        switch_turn_on_zip.setOnCheckedChangeListener(this);
        switch_msg_notification.setOnCheckedChangeListener(this);
        switch_turn_off_msg.setChecked(mSettingSp.getBoolean("turn_off_msg", false));
        switch_turn_on_zip.setChecked(mSettingSp.getBoolean("turn_on_zip", false));
        switch_msg_notification.setChecked(mSettingSp.getBoolean("msg_notification", false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_top_bar_left:
                onBackPressed();
                break;
            case R.id.btn_sign_out:
                mUserInfoSp.edit().clear().apply();
                startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case R.id.rl_clean_cache:
                Observable.just(true)
                        .map(new Func1<Boolean, Boolean>() {
                            @Override
                            public Boolean call(Boolean aBoolean) {
                                FileUtils.deleteFile(StorageUtils.getCacheDirectory(SettingsActivity.this, false).getAbsolutePath());
                                FileUtils.deleteFile(StorageUtils.getCacheDirectory(SettingsActivity.this, true).getAbsolutePath());
                                return aBoolean;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                Toast.makeText(App.getInstance(), R.string.cache_cleared, Toast.LENGTH_SHORT).show();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_turn_off_msg:
                mSettingSp.edit().putBoolean("turn_off_msg", isChecked).apply();
                break;
            case R.id.switch_turn_on_zip:
                mSettingSp.edit().putBoolean("turn_on_zip", isChecked).apply();
                break;
            case R.id.switch_msg_notification:
                mSettingSp.edit().putBoolean("msg_notification", isChecked).apply();
                break;
            default:
                break;
        }
    }
}
