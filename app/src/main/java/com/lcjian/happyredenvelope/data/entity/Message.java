package com.lcjian.happyredenvelope.data.entity;

import java.io.Serializable;

public class Message implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public long id;
    public String desc;
    public int type;//消息类型    1-通知消息 2-广告消息 3-真红包消息 4-空红包消息 5-假红包信息
    public User hblUser;
    public long createTime;

    public Brand supportInfo;

}
