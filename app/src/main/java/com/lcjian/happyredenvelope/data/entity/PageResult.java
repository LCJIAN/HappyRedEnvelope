package com.lcjian.happyredenvelope.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageResult<T> {

    public Integer page_number;
    public Integer page_size;
    @SerializedName("totalPages")
    public Integer total_pages;
    public Integer total_elements;
    @SerializedName("records")
    public List<T> elements;
}
