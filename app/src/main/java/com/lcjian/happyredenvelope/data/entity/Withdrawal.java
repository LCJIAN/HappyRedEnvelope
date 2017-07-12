package com.lcjian.happyredenvelope.data.entity;

public class Withdrawal {

    public float withdrawAmount;
    public long withdrawCreatetime;
    public boolean withdrawIsdeleted;
    public boolean withdrawIssucceed;
    public Token token;

    public static class Token {

        public long tokenCreatetime;
        public String tokenContent;
        public boolean tokenIsexpired;
        public int tokenTime;

    }
}
