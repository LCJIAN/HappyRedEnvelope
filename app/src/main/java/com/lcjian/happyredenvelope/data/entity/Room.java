package com.lcjian.happyredenvelope.data.entity;

import java.util.List;

/**
 * Created by root on 17-6-20.
 */

public class Room {

    public long createTime;
    public String desc;
    public int hongBaoCount;
    public long id;
    public int maxNumber;
    public String name;
    public int nowNumber;
    public String pic;
    public List<RankInfo> rankInfo;
    public         boolean  show;
    public         boolean  vip;

    public static class RankInfo {
        public String userHeadimg;
        public long userId;
        public String userNickname;
    }
}
