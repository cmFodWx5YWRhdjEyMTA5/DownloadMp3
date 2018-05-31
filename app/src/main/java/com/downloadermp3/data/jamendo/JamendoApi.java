package com.downloadermp3.data.jamendo;

import android.content.Context;

import com.downloadermp3.bean.JamendoModel;
import com.downloadermp3.data.IMusicApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.downloadermp3.data.BaseModel;

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
 * Created by liyanju on 2018/5/18.
 */

public class JamendoApi implements IMusicApi {

    private static final String BASE_JAMENDO_URL = "https://api.jamendo.com/v3.0/";

    private static JamendoService sJamendoService;

    private boolean isShowNextPage;

    @Override
    public void resetPaging() {
        offset = 0;
    }

    @Override
    public boolean onShowNextPage() {
        return isShowNextPage;
    }

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

    private int offset;

    @Override
    public ArrayList<BaseModel> getRecommondMusic(Context context) {
        try {
            JamendoService jamendoService = getJamendoService(context);
            Call<JamendoModel> call = jamendoService
                    .getJamendoDataByOrder(JamendoService.DOWNLOADS_TOTAL_ORDER, offset);
            Response<JamendoModel> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                if (response.body().arrayList.size() > 0) {
                    offset = offset + JamendoService.PAGE_LIMIT;
                }
                if (response.body().arrayList.size() < JamendoService.PAGE_LIMIT) {
                    isShowNextPage = false;
                } else {
                    isShowNextPage = true;
                }
                return response.body().arrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<BaseModel> searchMusic(Context context, String query) {
        try {
            JamendoService jamendoService = getJamendoService(context);
            Call<JamendoModel> call = jamendoService.searchJamendoData(query, offset);
            Response<JamendoModel> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                if (response.body().arrayList.size() > 0) {
                    offset = offset + JamendoService.PAGE_LIMIT;
                }
                if (response.body().arrayList.size() < JamendoService.PAGE_LIMIT) {
                    isShowNextPage = false;
                } else {
                    isShowNextPage = true;
                }
                return response.body().arrayList;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
