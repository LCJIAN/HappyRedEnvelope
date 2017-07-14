package com.lcjian.happyredenvelope.data.entity;

import java.io.Serializable;

public class User implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public long userCreatetime;
    public String userHeadimg;
    public long userId;
    public long userLastlogintime;
    public String userNickname;
    public String userOpenid;
    public String userCountry;
    public int userSex;
    public float userBalance;
}
