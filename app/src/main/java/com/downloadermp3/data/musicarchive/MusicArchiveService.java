package com.downloadermp3.data.musicarchive;

import com.downloadermp3.bean.MusicArchiveModel;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by liyanju on 2018/6/20.
 */

public interface MusicArchiveService {

    @GET("featured.json")
    Call<MusicArchiveModel> getMusicArchiveFeatred();

    @GET("recent.json")
    Call<MusicArchiveModel> getMusicArchiveRecent();
}
