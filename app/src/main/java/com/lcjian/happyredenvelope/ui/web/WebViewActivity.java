package com.lcjian.happyredenvelope.ui.web;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.lib.util.common.DimenUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends BaseActivity implements
        ChromeClientCallbackManager.ReceivedTitleCallback, View.OnClickListener {

    @BindView(R.id.web_view_container)
    LinearLayout web_view_container;
    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;

    private AgentWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        tv_top_bar_right.setVisibility(View.GONE);
        tv_top_bar_title.setGravity(Gravity.CENTER);
        tv_top_bar_title.setMaxWidth((int) DimenUtils.dipToPixels(200, this));
        tv_top_bar_title.setSingleLine();
        tv_top_bar_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);

        btn_top_bar_left.setOnClickListener(this);

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
        tv_top_bar_title.setText(title);
        tv_top_bar_title.requestFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_top_bar_left:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!mAgentWeb.back()) {
            super.onBackPressed();
        }
    }
}
