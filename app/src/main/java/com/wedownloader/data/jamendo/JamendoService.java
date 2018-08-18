package com.wedownloader.data.jamendo;

import com.wedownloader.bean.JamendoBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by liyanju on 2018/5/16.
 */

public interface JamendoService {

    String ClIENT_ID = "1c5d732e"; //cf4a7613 //

   String DOWNLOADS_TOTAL_ORDER = "downloads_total";
   String POPULARITY_TOTAL_ORDER = "popularity_total";
   String LISTEN_TOTAL_ORDER = "listens_total";

    int PAGE_LIMIT = 25;

    @GET("tracks?client_id="+ClIENT_ID+"&format=json&include=lyrics&limit=25&audiodlformat=mp32&order=relevance&type=single+albumtrack")
    Call<JamendoBean> searchJamendoData(@Query("search") String namesearch, @Query("offset") int offset);

    @GET("tracks?client_id="+ClIENT_ID+"&format=json&limit="+PAGE_LIMIT+"&type=single+albumtrack")
    Call<JamendoBean> getJamendoDataByOrder(@Query("order") String order, @Query("offset") int offset);

    @GET("tracks?client_id="+ClIENT_ID+"&format=json&include=lyrics&limit=25&type=single+albumtrack")
    Call<JamendoBean> getJamendoDataByTags(@Query("tags") String tags, @Query("offset") int offset);
}