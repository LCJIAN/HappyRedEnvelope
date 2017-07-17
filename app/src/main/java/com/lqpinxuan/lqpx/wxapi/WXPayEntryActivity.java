package com.lqpinxuan.lqpx.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.Constants;
import com.lcjian.happyredenvelope.R;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @BindView(R.id.tv_pay_result)
    TextView tv_pay_result;

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        ButterKnife.bind(this);

        btn_top_bar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_top_bar_title.setText(R.string.pay_result);
        tv_top_bar_right.setVisibility(View.GONE);

        api = WXAPIFactory.createWXAPI(this, Constants.WE_CHAT_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                tv_pay_result.setText(R.string.pay_success);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                tv_pay_result.setText(R.string.user_canceled);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                tv_pay_result.setText(R.string.auth_denied);
                break;
            default:
                tv_pay_result.setText(R.string.pay_failed);
                break;
        }
    }
}