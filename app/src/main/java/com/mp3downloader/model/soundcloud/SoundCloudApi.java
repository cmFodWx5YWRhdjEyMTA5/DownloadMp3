package com.mp3downloader.model.soundcloud;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mp3downloader.model.BaseModel;
import com.mp3downloader.model.IMusicApi;

import java.io.File;
import java.util.List;
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

public class SoundCloudApi implements IMusicApi{

    private static final String BASE_SOUNDCLOUD_URL = "http://api.soundcloud.com/";

    private static SoundCloudService sSoundCloudService;

    @Override
    public void resetPaging() {

    }

    private static Cache createDefaultCache(Context context) {
        File cacheDir = new File(context.getCacheDir().getAbsolutePath(), "/okhttp/");
        if (cacheDir.mkdirs() || cacheDir.isDirectory()) {
            return new Cache(cacheDir, 1024 * 1024 * 10);
        }
        return null;
    }

    private static SoundCloudService getSoundCloudService(Context context) {
        if (sSoundCloudService == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(SoundCloudModel.class, new SoundCloudDesrializer());
            Gson gson = gsonBuilder.create();
            GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(createDefaultCache(context))
                    .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                    .readTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_SOUNDCLOUD_URL)
                    .client(client)
                    .addConverterFactory(gsonConverterFactory)
                    .build();
            sSoundCloudService = retrofit.create(SoundCloudService.class);
        }
        return sSoundCloudService;
    }

    @Override
    public boolean onShowNextPage() {
        return false;
    }

    @Override
    public List<BaseModel> getRecommondMusic(Context context) {
        return null;
    }

    @Override
    public List<BaseModel> searchMusic(Context context, String query) {
        try {
            SoundCloudService soundCloudService = getSoundCloudService(context);
            Call<SoundCloudModel> call = soundCloudService.getSearchMusicList(query);
            Response<SoundCloudModel> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().arrayList;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
