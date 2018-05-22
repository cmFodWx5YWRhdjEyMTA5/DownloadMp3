package com.mp3downloader.model;

import android.content.Context;

import java.util.List;

/**
 * Created by liyanju on 2018/5/18.
 */

public interface IMusicApi {

    List<BaseModel> getRecommondMusic(Context context);

    List<BaseModel> searchMusic(Context context, String query);

    void resetPaging();

    boolean onShowNextPage();
}
