package com.wedownloader.data.musicarchive;

import com.wedownloader.bean.MusicArchiveBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liyanju on 2018/6/20.
 */

public interface MusicArchiveService {

    @GET("featured.json")
    Call<MusicArchiveBean> getMusicArchiveFeatred();

    @GET("recent.json")
    Call<MusicArchiveBean> getMusicArchiveRecent();

    @GET("api/get/tracks.json?limit=" + MusicArchiveBean.PAGE_LIMIT
            + "&commercial=true&added_month=true&api_key=1X7D906QL9JUH3E8")
    Call<MusicArchiveBean> getMusicArchiveNew(@Query("page") int page);

    @GET("interesting.json")
    Call<MusicArchiveBean> getMusicArchiveTops();

    @GET("api/get/tracks.json?limit="+ MusicArchiveBean.PAGE_LIMIT +"&commercial=true&sort_by=track_favorites&api_key=1X7D906QL9JUH3E8")
    Call<MusicArchiveBean> getMusicArchiveFavorites(@Query("page") int page);

}
