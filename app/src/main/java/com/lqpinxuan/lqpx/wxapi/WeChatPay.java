package com.lqpinxuan.lqpx.wxapi;

public class WeChatPay {

    private IWXAPI api;
    PayReq req = new PayReq();
    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
    req.appId			= json.getString("appid");
    req.partnerId		= json.getString("partnerid");
    req.prepayId		= json.getString("prepayid");
    req.nonceStr		= json.getString("noncestr");
    req.timeStamp		= json.getString("timestamp");
    req.packageValue	= json.getString("package");
    req.sign			= json.getString("sign");
    req.extData			= "app data"; // optional
    api.sendReq(req);
}
