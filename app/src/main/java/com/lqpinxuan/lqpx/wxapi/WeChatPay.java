package com.lqpinxuan.lqpx.wxapi;

import android.content.Context;

import com.lcjian.happyredenvelope.Constants;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WeChatPay {

    public static void pay(Context context,
                           String appId,
                           String partnerId,
                           String prepayId,
                           String nonceStr,
                           String timeStamp,
                           String packageValue,
                           String sign) {
        PayReq req = new PayReq();
        req.appId = appId;
        req.partnerId = partnerId;
        req.prepayId = prepayId;
        req.nonceStr = nonceStr;
        req.timeStamp = timeStamp;
        req.packageValue = packageValue;
        req.sign = sign;
        WXAPIFactory.createWXAPI(context, Constants.WE_CHAT_ID).sendReq(req);
    }

}
