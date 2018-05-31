package com.downloadermp3.data.youtube;

import com.downloadermp3.bean.YTbeModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liyanju on 2018/5/18.
 */

public interface YouTubeService {

    @GET("videos?part=snippet&videoCategoryId=10&chart=mostPopular&maxResults=45")
    Call<YTbeModel> getYoutubeMusic(@Query("pageToken") String pageToken, @Query("key") String key);

    @GET("search?part=snippet&type=video&maxResults=45")
    Call<YTbeModel> searchYoutubeMusic(@Query("q") String search, @Query("pageToken") String pageToken, @Query("key") String key);
}
