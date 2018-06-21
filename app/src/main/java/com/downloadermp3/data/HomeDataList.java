package com.downloadermp3.data;

import android.content.Context;

import com.downloadermp3.Mp3App;
import com.downloadermp3.R;
import com.downloadermp3.bean.JamendoModel;
import com.downloadermp3.bean.MusicArchiveModel;
import com.downloadermp3.bean.TitleModel;
import com.downloadermp3.data.jamendo.JamendoApi;
import com.downloadermp3.data.jamendo.JamendoService;
import com.downloadermp3.data.musicarchive.MusicArchiveClient;
import com.downloadermp3.util.ACache;
import com.downloadermp3.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import retrofit2.Response;

/**
 * Created by liyanju on 2018/6/20.
 */

public class HomeDataList {

    public static final String TAG = "HomeDataList";

    public static Context context = Mp3App.sContext;

    public static ArrayList<Object> getHomeDataList(Context context, boolean isNeedCache) {
        if (isNeedCache) {
            Object object = null;
            try {
                object = ACache.get(Mp3App.sContext).getAsObject("homeData");
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (object != null && object instanceof ArrayList) {
                LogUtil.v(TAG, "getHomeDataList load cache....");
                return (ArrayList<Object>) object;
            }
        }
        LogUtil.v(TAG, "getHomeDataList request data....");

        ArrayList<Object> dataList = new ArrayList<>();
        TitleModel titleModel;
        boolean dataFull = true;

        //add remcom list style
        MusicArchiveModel musicArchiveFeatured = requestMusicArchive(MusicArchiveModel.FEATURED_TYPE);
        if (musicArchiveFeatured != null && musicArchiveFeatured.contentList.size() > 0) {
            musicArchiveFeatured.type = MusicArchiveModel.FEATURED_TYPE;
            MusicArchiveModel musicArchiveRecent = requestMusicArchive(MusicArchiveModel.RECENT_TYPE);
            if (musicArchiveRecent != null && musicArchiveRecent.contentList.size() > 0) {
                musicArchiveRecent.type = MusicArchiveModel.RECENT_TYPE;
                titleModel = new TitleModel(context.getString(R.string.recommend_text),
                        TitleModel.RECOMMEND_TYPE);

                dataList.add(titleModel);

                HashMap<Integer, MusicArchiveModel> sparseArray = new HashMap<>();
                Collections.shuffle(musicArchiveFeatured.contentList);
                Collections.shuffle(musicArchiveRecent.contentList);
                sparseArray.put(MusicArchiveModel.FEATURED_TYPE, musicArchiveFeatured);
                sparseArray.put(MusicArchiveModel.RECENT_TYPE, musicArchiveRecent);

                dataList.add(sparseArray);
            } else {
                dataFull = false;
            }
        } else {
            dataFull = false;
        }

        //add top download grid style
        JamendoModel jamendoModel1 = requestJamendoData(JamendoService.DOWNLOADS_TOTAL_ORDER);
        if (jamendoModel1 != null && jamendoModel1.arrayList.size() > 0) {
            jamendoModel1.type = TitleModel.TOP_DOWNLOAD_TYPE;
            titleModel = new TitleModel(context.getString(R.string.top_download_text),
                    TitleModel.TOP_DOWNLOAD_TYPE);
            dataList.add(titleModel);
            Collections.shuffle(jamendoModel1.arrayList);
            dataList.add(jamendoModel1.arrayList);
        } else {
            dataFull = false;
        }

        //add Gener  grid style
        dataList.add(new TitleModel(context.getString(R.string.genre_text),
                TitleModel.GENRES_TYPE));
        dataList.add(getJamendoGeners());

        //add top lisented grid style
        JamendoModel jamendoModel2 = requestJamendoData(JamendoService.LISTEN_TOTAL_ORDER);
        if (jamendoModel2 != null && jamendoModel2.arrayList.size() > 0) {
            jamendoModel2.type = TitleModel.TOP_LISTENED_TYPE;
            titleModel = new TitleModel(context.getString(R.string.top_listened_text),
                    TitleModel.TOP_LISTENED_TYPE);
            dataList.add(titleModel);
            Collections.shuffle(jamendoModel2.arrayList);
            dataList.add(jamendoModel2.arrayList);
        } else {
            dataFull = false;
        }

        //add Instrument  grid style
        dataList.add(new TitleModel(context.getString(R.string.instrument_text),
                TitleModel.INSTRUMENT_TYPE));
        dataList.add(getJamendoInstrument());

        LogUtil.v(TAG, " getHomeDataList dataFull " + dataFull);
        if (dataFull) {
            try {
                ACache.get(Mp3App.sContext).put("homeData", dataList, 60 * 60 * 24 * 1);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        return dataList;
    }


    private static MusicArchiveModel requestMusicArchive(int type) {
        try {
            if (type == MusicArchiveModel.FEATURED_TYPE) {
                Response<MusicArchiveModel> response = MusicArchiveClient.getMusicArchiveRetrofit(Mp3App.sContext)
                        .getMusicArchiveFeatred().execute();
                if (response != null && response.body() != null) {
                    return response.body();
                }
            } else {
                Response<MusicArchiveModel> response = MusicArchiveClient.getMusicArchiveRetrofit(Mp3App.sContext)
                        .getMusicArchiveRecent().execute();
                if (response != null && response.body() != null) {
                    return response.body();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static JamendoModel requestJamendoData(String order) {
        try {
            Response<JamendoModel> response = JamendoApi.getJamendoService(Mp3App.sContext)
                    .getJamendoDataByOrder(order, 0)
                    .execute();
            if (response != null && response.body() != null) {
                return response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String GENERSARRAY[] = new String[]{
            context.getString(R.string.pop_text),
            context.getString(R.string.Electronic_text),
            context.getString(R.string.rock_text),
            context.getString(R.string.ambient_text),
            context.getString(R.string.intrumental_text),
            context.getString(R.string.hiphop_text),
            context.getString(R.string.indie_text),
            context.getString(R.string.jazz_text),
            context.getString(R.string.world_text),
            context.getString(R.string.metal_text),
            context.getString(R.string.experimental_text),
            context.getString(R.string.Chillout_text),
            context.getString(R.string.Dance_text),
            context.getString(R.string.Country_text),
            context.getString(R.string.Classical_text),
            context.getString(R.string.Reggae_text),
            context.getString(R.string.Lounge_text),
            context.getString(R.string.Techno_text),
            context.getString(R.string.Trance_text),
            context.getString(R.string.Punk_text),
            context.getString(R.string.Folk_text),
            context.getString(R.string.Rap_text),
            context.getString(R.string.Triphop_text),
            context.getString(R.string.newage_text),
            context.getString(R.string.Dubstep_text),
            context.getString(R.string.Acoustic_text),
            context.getString(R.string.Blues_text),
            context.getString(R.string.Funk_text),
            context.getString(R.string.Disco_text)
    };

    private static final String INSTRUMENT[] = new String[]{
            context.getString(R.string.Piano),
            context.getString(R.string.Bass),
            context.getString(R.string.Drum),
            context.getString(R.string.Electricguitar),
            context.getString(R.string.Computer),
            context.getString(R.string.Acousticguitar),
            context.getString(R.string.Keyboard),
            context.getString(R.string.Synthesizer),
            context.getString(R.string.Violin),
            context.getString(R.string.Flute),
            context.getString(R.string.Organ),
            context.getString(R.string.Viola),
            context.getString(R.string.Cello),
            context.getString(R.string.Saxophone),
            context.getString(R.string.Classicalguitar),
            context.getString(R.string.Accordion),
    };

    private static ArrayList<JamendoModel> getJamendoInstrument() {
        ArrayList<JamendoModel> list = new ArrayList<>();
        list.add(new JamendoModel(INSTRUMENT[6], INSTRUMENT[6].toLowerCase(), R.drawable.inst_keyboard, TitleModel.INSTRUMENT_TYPE));
        list.add(new JamendoModel(INSTRUMENT[10], INSTRUMENT[10].toLowerCase(), R.drawable.inst_organ, TitleModel.INSTRUMENT_TYPE));
        list.add(new JamendoModel(INSTRUMENT[1], INSTRUMENT[1].toLowerCase(), R.drawable.inst_bass, TitleModel.INSTRUMENT_TYPE));
        list.add(new JamendoModel(INSTRUMENT[9], INSTRUMENT[9].toLowerCase(), R.drawable.inst_flute, TitleModel.INSTRUMENT_TYPE));
        list.add(new JamendoModel(INSTRUMENT[13], INSTRUMENT[13].toLowerCase(), R.drawable.inst_cello, TitleModel.INSTRUMENT_TYPE));
        list.add(new JamendoModel(INSTRUMENT[0], INSTRUMENT[0].toLowerCase(), R.drawable.inst_piano, TitleModel.INSTRUMENT_TYPE));
        list.add(new JamendoModel(INSTRUMENT[12], INSTRUMENT[12].toLowerCase(), R.drawable.inst_violin, TitleModel.INSTRUMENT_TYPE));
        list.add(new JamendoModel(INSTRUMENT[2], INSTRUMENT[2].toLowerCase(), R.drawable.inst_drum, TitleModel.INSTRUMENT_TYPE));
        list.add(new JamendoModel(INSTRUMENT[3], INSTRUMENT[3].toLowerCase(), R.drawable.inst_electroguitar, TitleModel.INSTRUMENT_TYPE));
        list.add(new JamendoModel(INSTRUMENT[4], INSTRUMENT[4].toLowerCase(), R.drawable.inst_computer, TitleModel.INSTRUMENT_TYPE));
        list.add(new JamendoModel(INSTRUMENT[5], INSTRUMENT[5].toLowerCase(), R.drawable.inst_accousticguitar, TitleModel.INSTRUMENT_TYPE));
        list.add(new JamendoModel(INSTRUMENT[7], INSTRUMENT[7].toLowerCase(), R.drawable.inst_synthesizer, TitleModel.INSTRUMENT_TYPE));
        list.add(new JamendoModel(INSTRUMENT[8], INSTRUMENT[8].toLowerCase(), R.drawable.inst_violin, TitleModel.INSTRUMENT_TYPE));
        list.add(new JamendoModel(INSTRUMENT[14], INSTRUMENT[14].toLowerCase(), R.drawable.inst_saxophone, TitleModel.INSTRUMENT_TYPE));
        return list;
    }

    private static ArrayList<JamendoModel> getJamendoGeners() {
        ArrayList<JamendoModel> list = new ArrayList<>();
        list.add(new JamendoModel(GENERSARRAY[5], GENERSARRAY[5].toLowerCase(), R.drawable.hip_hop, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[0], GENERSARRAY[0].toLowerCase(), R.drawable.pop, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[2], GENERSARRAY[2].toLowerCase(), R.drawable.rock, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[7], GENERSARRAY[7].toLowerCase(), R.drawable.jazz, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[1], GENERSARRAY[1].toLowerCase(), R.drawable.electro, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[12], GENERSARRAY[12].toLowerCase(), R.drawable.dance, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[3], GENERSARRAY[3].toLowerCase(), R.drawable.ambient, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[4], GENERSARRAY[4].toLowerCase(), R.drawable.instrumental, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[6], GENERSARRAY[6].toLowerCase(), R.drawable.indiepop, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[8], GENERSARRAY[8].toLowerCase(), R.drawable.world, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[9], GENERSARRAY[9].toLowerCase(), R.drawable.metal, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[10], GENERSARRAY[10].toLowerCase(), R.drawable.experimental, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[11], GENERSARRAY[11].toLowerCase(), R.drawable.chillout, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[13], GENERSARRAY[13].toLowerCase(), R.drawable.country, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[14], GENERSARRAY[14].toLowerCase(), R.drawable.classic, TitleModel.GENRES_TYPE));

        list.add(new JamendoModel(GENERSARRAY[15], GENERSARRAY[15].toLowerCase(), R.drawable.reggae, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[16], GENERSARRAY[16].toLowerCase(), R.drawable.lounge, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[17], GENERSARRAY[17].toLowerCase(), R.drawable.techno, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[18], GENERSARRAY[18].toLowerCase(), R.drawable.trance, TitleModel.GENRES_TYPE));
        list.add(new JamendoModel(GENERSARRAY[19], GENERSARRAY[19].toLowerCase(), R.drawable.punk, TitleModel.GENRES_TYPE));
        return list;
    }
}
