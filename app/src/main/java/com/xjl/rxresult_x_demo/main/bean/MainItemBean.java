package com.xjl.rxresult_x_demo.main.bean;

import androidx.annotation.DrawableRes;

public class MainItemBean {

    public String title;
    @DrawableRes
    public int imageRes;
    public int favCount;
    public int commentCount;
    public boolean fav;

    public MainItemBean(String title, int imageRes, int favCount, int commentCount, boolean fav) {
        this.title = title;
        this.imageRes = imageRes;
        this.favCount = favCount;
        this.commentCount = commentCount;
        this.fav = fav;
    }



}
