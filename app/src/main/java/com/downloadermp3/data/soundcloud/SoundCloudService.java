package com.downloadermp3.data.soundcloud;

import com.downloadermp3.bean.SCloudModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liyanju on 2018/5/16.
 */

public interface SoundCloudService {

    String CLIENT_ID = "a3e059563d7fd3372b49b37f00a00bcf";

    @GET("tracks?limit=100&client_id=" + CLIENT_ID)
    Call<SCloudModel> getSearchMusicList(@Query("q") String query);
}
