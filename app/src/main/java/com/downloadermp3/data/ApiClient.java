package com.downloadermp3.data;

import android.content.Context;

import com.downloadermp3.data.jamendo.JamendoDeserializer;
import com.downloadermp3.bean.JamendoModel;
import com.downloadermp3.data.jamendo.JamendoService;
import com.downloadermp3.data.soundcloud.SoundCloudService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liyanju on 2018/5/17.
 */

public class ApiClient {

    private static final String BASE_SOUNDCLOUD_URL = "http://api.soundcloud.com/";
    private static final String BASE_JAMENDO_URL = "https://api.jamendo.com/v3.0/";

    private static JamendoService sJamendoService;

    private static Cache createDefaultCache(Context context) {
        File cacheDir = new File(context.getCacheDir().getAbsolutePath(), "/okhttp/");
        if (cacheDir.mkdirs() || cacheDir.isDirectory()) {
            return new Cache(cacheDir, 1024 * 1024 * 10);
        }
        return null;
    }

    private static JamendoService getJamendoService(Context context) {
        if (sJamendoService == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(JamendoModel.class, new JamendoDeserializer());
            Gson gson = gsonBuilder.create();
            GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(createDefaultCache(context))
                    .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                    .readTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_JAMENDO_URL)
                    .client(client)
                    .addConverterFactory(gsonConverterFactory)
                    .build();
            sJamendoService = retrofit.create(JamendoService.class);
        }
        return sJamendoService;
    }

    private static SoundCloudService sSoundCloudService;

    private static SoundCloudService getSoundCloudService(Context context) {
        if (sSoundCloudService == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
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

    public static ArrayList<BaseModel> getRecommondMusic(Context context, int offset) {
        try {
            JamendoService jamendoService = getJamendoService(context);
            Call<JamendoModel> call = jamendoService
                    .getJamendoDataByOrder(JamendoService.DOWNLOADS_TOTAL_ORDER, offset);
            Response<JamendoModel> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().arrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<BaseModel> searchMusic(Context context, String search, int offset) {
        try {
            JamendoService jamendoService = getJamendoService(context);
            Call<JamendoModel> call = jamendoService.searchJamendoData(search, offset);
            Response<JamendoModel> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().arrayList;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
