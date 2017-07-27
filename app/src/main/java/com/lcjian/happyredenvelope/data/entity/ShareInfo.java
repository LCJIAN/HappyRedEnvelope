package com.lcjian.happyredenvelope.data.entity;

public class ShareInfo {

    public String inviteQrcode;
    public String inviteCode;
    public Content shareInfo;

    public static class Content {
        public String title;
        public String link;
        public String content;
    }
}
