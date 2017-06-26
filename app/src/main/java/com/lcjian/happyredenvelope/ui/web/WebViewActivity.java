package com.lcjian.happyredenvelope.ui.web;

import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends BaseActivity implements
        ChromeClientCallbackManager.ReceivedTitleCallback {

    @BindView(R.id.web_view_container)
    LinearLayout web_view_container;

    private AgentWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        mAgentWeb = AgentWeb.with(this) // 传入Activity
                .setAgentWebParent(web_view_container,
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT)) // 传入AgentWeb的父控件
                .useDefaultIndicator() // 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setReceivedTitleCallback(this) // 设置Web页面的title回调
                .createAgentWeb()
                .ready()
                .go(getIntent().getStringExtra("url"));
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {

    }
}
