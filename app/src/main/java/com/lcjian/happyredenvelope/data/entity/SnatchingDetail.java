package com.lcjian.happyredenvelope.data.entity;

import com.lcjian.lib.entity.Displayable;

import java.util.List;

public class SnatchingDetail {

    public float money;
    public List<Rank> rankList;
    public int position;
    public int totalCount;
    public int alreadyReceiveCount;
    public Brand supprotInfo;

    public static class Rank implements Displayable {

        public int totalLuck;
        public int totalCount;
        public User hblUser;
        public int luck;
        public long time;
        public int money;
    }
}
