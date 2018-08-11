package com.wedownloader.bean;

import java.io.Serializable;

/**
 * Created by liyanju on 2018/6/20.
 */

public class TitleBean implements Serializable {

    public String title;

    public static final int RECOMMEND_TYPE = 1;
    public static final int INSTRUMENT_TYPE = 2;
    public static final int GENRES_TYPE = 3;
    public static final int TOP_LISTENED_TYPE = 4;
    public static final int TOP_DOWNLOAD_TYPE = 5;

    public int type;


    public TitleBean(String title, int type) {
        this.title = title;
        this.type = type;
    }
}
