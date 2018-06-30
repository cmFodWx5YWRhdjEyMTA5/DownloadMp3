package com.freedownloader.data.musicarchive;

import com.freedownloader.bean.MusicArchiveBean;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by liyanju on 2018/6/20.
 */

public interface MusicArchiveService {

    @GET("featured.json")
    Call<MusicArchiveBean> getMusicArchiveFeatred();

    @GET("recent.json")
    Call<MusicArchiveBean> getMusicArchiveRecent();
}
