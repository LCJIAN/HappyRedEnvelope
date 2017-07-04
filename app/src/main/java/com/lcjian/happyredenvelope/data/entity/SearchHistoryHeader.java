package com.lcjian.happyredenvelope.data.entity;

import com.lcjian.lib.entity.Displayable;

public class SearchHistoryHeader implements Displayable {

    public String title;

    public String noDataText;

    public boolean noData;

    public SearchHistoryHeader(String title, String noDataText, boolean noData) {
        this.title = title;
        this.noData = noData;
        this.noDataText = noDataText;
    }
}
