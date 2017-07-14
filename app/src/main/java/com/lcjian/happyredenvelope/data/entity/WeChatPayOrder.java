package com.lcjian.happyredenvelope.data.entity;

import com.google.gson.annotations.SerializedName;

public class WeChatPayOrder {
    public String appid;
    public String noncestr;
    @SerializedName("package")
    public String packages;
    public String partnerid;
    public String prepayid;
    public String timestamp;
    public String sign;
}
