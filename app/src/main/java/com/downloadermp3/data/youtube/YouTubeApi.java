package com.downloadermp3.data.youtube;

import android.content.Context;
import android.text.TextUtils;

import com.downloadermp3.bean.YTbeModel;
import com.downloadermp3.data.BaseModel;
import com.downloadermp3.data.IMusicApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liyanju on 2018/5/18.
 */

public class YouTubeApi implements IMusicApi {

    public static final String YOUTUBE_URL = "https://www.googleapis.com/youtube/v3/";

    private static YouTubeService sYouTubeService;

    private String nextPage = "";

    private static final Random SRANDOM = new Random();

    private static final String KEYS[] = new String[]{
            "AIzaSyBGMm_BbAX_fo2VWO-5oDdPCwthdCEZjQ4",
            "AIzaSyCeAdoPkQwNeJgHzKUN3uF-hKmy1GII-Vg",
            "AIzaSyAyOFXjB7LQUnBs88s-Cx8vIY9SednWEn4"
    };

    private static Cache createDefaultCache(Context context) {
        File cacheDir = new File(context.getCacheDir().getAbsolutePath(), "/okhttp/");
        if (cacheDir.mkdirs() || cacheDir.isDirectory()) {
            return new Cache(cacheDir, 1024 * 1024 * 10);
        }
        return null;
    }

    private static YouTubeService getYouTubeService(Context context) {
        if (sYouTubeService == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(YTbeModel.class, new YouTubeDesrializer());
            Gson gson = gsonBuilder.create();
            GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(createDefaultCache(context))
                    .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                    .readTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(YOUTUBE_URL)
                    .client(client)
                    .addConverterFactory(gsonConverterFactory)
                    .build();
            sYouTubeService = retrofit.create(YouTubeService.class);
        }
        return sYouTubeService;
    }

    private String getDevelopKey() {
        return KEYS[SRANDOM.nextInt(KEYS.length)];
    }

    @Override
    public List<BaseModel> getRecommondMusic(Context context) {
        try {
            YouTubeService youTubeService = getYouTubeService(context);
            Call<YTbeModel> call = youTubeService.getYoutubeMusic(nextPage, getDevelopKey());
            Response<YTbeModel> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                if (response.body().list.size() > 0) {
                    nextPage = response.body().nextPageToken;
                }
                return response.body().list;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<BaseModel> searchMusic(Context context, String query) {
        try {
            YouTubeService youTubeService = getYouTubeService(context);
            Call<YTbeModel> call = youTubeService.searchYoutubeMusic(query, nextPage, getDevelopKey());
            Response<YTbeModel> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                if (response.body().list.size() > 0) {
                    nextPage = response.body().nextPageToken;
                }
                return response.body().list;
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void resetPaging() {
        nextPage = "";
    }

    @Override
    public boolean onShowNextPage() {
        return !TextUtils.isEmpty(nextPage);
    }
}
