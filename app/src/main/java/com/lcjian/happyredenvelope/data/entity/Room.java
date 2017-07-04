package com.lcjian.happyredenvelope.data.entity;

import java.util.List;

public class Room {

    public long createTime;
    public String desc;
    public int hongBaoCount;
    public long id;
    public int maxNumber;
    public String name;
    public int nowNumber;
    public String pic;
    public List<User> rankInfo;
    public boolean show;
    public boolean vip;
    public int state;   // 1:人数没到抢的状态;2:人数已到可以抢的状态;3:人数已满
}
