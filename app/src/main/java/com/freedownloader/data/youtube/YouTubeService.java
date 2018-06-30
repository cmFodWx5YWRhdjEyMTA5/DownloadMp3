package com.freedownloader.data.youtube;

import com.freedownloader.bean.YTbeBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liyanju on 2018/5/18.
 */

public interface YouTubeService {

    @GET("videos?part=snippet&videoCategoryId=10&chart=mostPopular&maxResults=45")
    Call<YTbeBean> getYoutubeMusic(@Query("pageToken") String pageToken, @Query("key") String key);

    @GET("search?part=snippet&type=video&maxResults=45")
    Call<YTbeBean> searchYoutubeMusic(@Query("q") String search, @Query("pageToken") String pageToken, @Query("key") String key);
}
