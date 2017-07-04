package com.lcjian.happyredenvelope.data.entity;

public class LuckCardSummary {

    public long lefttime;
    public Summary sell;
    public Summary send;

    public static class Summary {
        public int totalCount;
        public long totalTime;
    }
}
