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

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.constants.AlibcConstants;
import com.alibaba.baichuan.android.trade.model.TradeResult;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcDetailPage;
import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.lib.util.common.DimenUtils;

import java.util.HashMap;
import java.util.Map;

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

        Map<String, String> exParams = new HashMap<>();
        exParams.put(AlibcConstants.TAOKE_PID, "mm_106929643_20324269_108658120");

        //商品详情page
        AlibcBasePage detailPage = new AlibcDetailPage("ss");
        AlibcTrade.show(this, mAgentWeb.getWebCreator().get(), null, null, detailPage, null, null, exParams ,
                new AlibcTradeCallback() {
                    @Override
                    public void onTradeSuccess(TradeResult tradeResult) {
                        //打开电商组件，用户操作中成功信息回调。tradeResult：成功信息（结果类型：加购，支付；支付结果）
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        //打开电商组件，用户操作中错误信息回调。code：错误码；msg：错误信息
                    }
                });
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
