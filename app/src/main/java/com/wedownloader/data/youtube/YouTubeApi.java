package com.wedownloader.data.youtube;

import android.content.Context;
import android.text.TextUtils;

import com.wedownloader.bean.YTbeBean;
import com.wedownloader.data.Song;
import com.wedownloader.data.IMusicApi;
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
            "AIzaSyAnoUWLG9EfcDcnXzZ5uvLSldng7MlWeGM",
            "AIzaSyCVPWH192CubnRU_g8-ax-HM4CwMdUeWi0",
            "AIzaSyB9cH-GkN6dov0GiqWVVrFDYBV7XPWbLDw",
            "AIzaSyCxl6bHj0_DS_OGUrBhqiL_4oIqV2aYdnM",

            "AIzaSyCOqIz0_kaHdVWyHrR1h-6p4dtEB92fPxc",
            "AIzaSyBzGn19so24rA7_4-ngMk-kMlVTLImQlmM",
            "AIzaSyAHUpSbVPm0lrNzkr69OK75P6j9bCk7BjU",
            "AIzaSyAHUpSbVPm0lrNzkr69OK75P6j9bCk7BjU",
            "AIzaSyCV9C9ebeRjGFhm6PROf3vb9Qt1PhK4tyA",
            "AIzaSyDfXw1yg4horp0V3G4PPUfZlJDMKm8VenI",
            "AIzaSyBzKfKOLEtxYzlStFvgVl2LV78PNbL83ZE",
            "AIzaSyCJ9wEzN4KcLoKTmEQvclIeNVvm-5kWsSk",
            "AIzaSyCaUVhLD77Eqo0UpNbJ4rkHjX4THuq1kio",
            "AIzaSyCBv-aS9zStaCV2NmrL1a3c_WvfatLD4RE",
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
            gsonBuilder.registerTypeAdapter(YTbeBean.class, new YouTubeDesrializer());
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
    public List<Song> getRecommondMusic(Context context) {
        try {
            YouTubeService youTubeService = getYouTubeService(context);
            Call<YTbeBean> call = youTubeService.getYoutubeMusic(nextPage, getDevelopKey());
            Response<YTbeBean> response = call.execute();
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
    public List<Song> searchMusic(Context context, String query) {
        try {
            YouTubeService youTubeService = getYouTubeService(context);
            Call<YTbeBean> call = youTubeService.searchYoutubeMusic(query, nextPage, getDevelopKey());
            Response<YTbeBean> response = call.execute();
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
