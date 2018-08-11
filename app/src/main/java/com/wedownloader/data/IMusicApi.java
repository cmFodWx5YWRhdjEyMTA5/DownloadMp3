package com.wedownloader.data;

import android.content.Context;

import java.util.List;

/**
 * Created by liyanju on 2018/5/18.
 */

public interface IMusicApi {

    List<Song> getRecommondMusic(Context context);

    List<Song> searchMusic(Context context, String query);

    void resetPaging();

    boolean onShowNextPage();
}
