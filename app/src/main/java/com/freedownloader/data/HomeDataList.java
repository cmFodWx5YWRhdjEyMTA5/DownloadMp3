package com.freedownloader.data;

import android.content.Context;

import com.freedownloader.Mp3App;
import com.freedownloader.R;
import com.freedownloader.bean.JamendoBean;
import com.freedownloader.bean.MusicArchiveBean;
import com.freedownloader.bean.TitleBean;
import com.freedownloader.data.jamendo.JamendoApi;
import com.freedownloader.data.jamendo.JamendoService;
import com.freedownloader.data.musicarchive.MusicArchiveClient;
import com.freedownloader.util.ACache;
import com.freedownloader.util.LogUtil;

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
        TitleBean titleModel;
        boolean dataFull = true;

        //add remcom list style
        MusicArchiveBean musicArchiveFeatured = requestMusicArchive(MusicArchiveBean.FEATURED_TYPE);
        if (musicArchiveFeatured != null && musicArchiveFeatured.contentList.size() > 0) {
            musicArchiveFeatured.type = MusicArchiveBean.FEATURED_TYPE;
            MusicArchiveBean musicArchiveRecent = requestMusicArchive(MusicArchiveBean.RECENT_TYPE);
            if (musicArchiveRecent != null && musicArchiveRecent.contentList.size() > 0) {
                musicArchiveRecent.type = MusicArchiveBean.RECENT_TYPE;
                titleModel = new TitleBean(context.getString(R.string.recommend_text),
                        TitleBean.RECOMMEND_TYPE);

                dataList.add(titleModel);

                HashMap<Integer, MusicArchiveBean> sparseArray = new HashMap<>();
                Collections.shuffle(musicArchiveFeatured.contentList);
                Collections.shuffle(musicArchiveRecent.contentList);
                sparseArray.put(MusicArchiveBean.FEATURED_TYPE, musicArchiveFeatured);
                sparseArray.put(MusicArchiveBean.RECENT_TYPE, musicArchiveRecent);

                dataList.add(sparseArray);
            } else {
                dataFull = false;
            }
        } else {
            dataFull = false;
        }

        //add top lisented grid style
        JamendoBean jamendoModel2 = requestJamendoData(JamendoService.LISTEN_TOTAL_ORDER);
        if (jamendoModel2 != null && jamendoModel2.arrayList.size() > 0) {
            jamendoModel2.type = TitleBean.TOP_LISTENED_TYPE;
            titleModel = new TitleBean(context.getString(R.string.top_listened_text),
                    TitleBean.TOP_LISTENED_TYPE);
            dataList.add(titleModel);
            Collections.shuffle(jamendoModel2.arrayList);
            dataList.add(jamendoModel2.arrayList);
        } else {
            dataFull = false;
        }

        //add Gener  grid style
        dataList.add(new TitleBean(context.getString(R.string.genre_text),
                TitleBean.GENRES_TYPE));
        dataList.add(getJamendoGeners());

        //add top download grid style
        JamendoBean jamendoModel1 = requestJamendoData(JamendoService.DOWNLOADS_TOTAL_ORDER);
        if (jamendoModel1 != null && jamendoModel1.arrayList.size() > 0) {
            jamendoModel1.type = TitleBean.TOP_DOWNLOAD_TYPE;
            titleModel = new TitleBean(context.getString(R.string.top_download_text),
                    TitleBean.TOP_DOWNLOAD_TYPE);
            dataList.add(titleModel);
            Collections.shuffle(jamendoModel1.arrayList);
            dataList.add(jamendoModel1.arrayList);
        } else {
            dataFull = false;
        }

        //add Instrument  grid style
        dataList.add(new TitleBean(context.getString(R.string.instrument_text),
                TitleBean.INSTRUMENT_TYPE));
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


    private static MusicArchiveBean requestMusicArchive(int type) {
        try {
            if (type == MusicArchiveBean.FEATURED_TYPE) {
                Response<MusicArchiveBean> response = MusicArchiveClient.getMusicArchiveRetrofit(Mp3App.sContext)
                        .getMusicArchiveFeatred().execute();
                if (response != null && response.body() != null) {
                    return response.body();
                }
            } else {
                Response<MusicArchiveBean> response = MusicArchiveClient.getMusicArchiveRetrofit(Mp3App.sContext)
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

    private static JamendoBean requestJamendoData(String order) {
        try {
            Response<JamendoBean> response = JamendoApi.getJamendoService(Mp3App.sContext)
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

    private static ArrayList<JamendoBean> getJamendoInstrument() {
        ArrayList<JamendoBean> list = new ArrayList<>();
        list.add(new JamendoBean(INSTRUMENT[10], INSTRUMENT[10].toLowerCase(), R.drawable.inst_organ, TitleBean.INSTRUMENT_TYPE));
        list.add(new JamendoBean(INSTRUMENT[6], INSTRUMENT[6].toLowerCase(), R.drawable.inst_keyboard, TitleBean.INSTRUMENT_TYPE));
        list.add(new JamendoBean(INSTRUMENT[5], INSTRUMENT[5].toLowerCase(), R.drawable.inst_accousticguitar, TitleBean.INSTRUMENT_TYPE));
        list.add(new JamendoBean(INSTRUMENT[9], INSTRUMENT[9].toLowerCase(), R.drawable.inst_flute, TitleBean.INSTRUMENT_TYPE));
        list.add(new JamendoBean(INSTRUMENT[1], INSTRUMENT[1].toLowerCase(), R.drawable.inst_bass, TitleBean.INSTRUMENT_TYPE));
        list.add(new JamendoBean(INSTRUMENT[13], INSTRUMENT[13].toLowerCase(), R.drawable.inst_cello, TitleBean.INSTRUMENT_TYPE));
        list.add(new JamendoBean(INSTRUMENT[0], INSTRUMENT[0].toLowerCase(), R.drawable.inst_piano, TitleBean.INSTRUMENT_TYPE));
        list.add(new JamendoBean(INSTRUMENT[12], INSTRUMENT[12].toLowerCase(), R.drawable.inst_violin, TitleBean.INSTRUMENT_TYPE));
        list.add(new JamendoBean(INSTRUMENT[2], INSTRUMENT[2].toLowerCase(), R.drawable.inst_drum, TitleBean.INSTRUMENT_TYPE));
        list.add(new JamendoBean(INSTRUMENT[3], INSTRUMENT[3].toLowerCase(), R.drawable.inst_electroguitar, TitleBean.INSTRUMENT_TYPE));
        list.add(new JamendoBean(INSTRUMENT[4], INSTRUMENT[4].toLowerCase(), R.drawable.inst_computer, TitleBean.INSTRUMENT_TYPE));
        list.add(new JamendoBean(INSTRUMENT[7], INSTRUMENT[7].toLowerCase(), R.drawable.inst_synthesizer, TitleBean.INSTRUMENT_TYPE));
        list.add(new JamendoBean(INSTRUMENT[8], INSTRUMENT[8].toLowerCase(), R.drawable.inst_violin, TitleBean.INSTRUMENT_TYPE));
        list.add(new JamendoBean(INSTRUMENT[14], INSTRUMENT[14].toLowerCase(), R.drawable.inst_saxophone, TitleBean.INSTRUMENT_TYPE));
        return list;
    }

    private static ArrayList<JamendoBean> getJamendoGeners() {
        ArrayList<JamendoBean> list = new ArrayList<>();
        list.add(new JamendoBean(GENERSARRAY[1], GENERSARRAY[1].toLowerCase(), R.drawable.electro, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[0], GENERSARRAY[0].toLowerCase(), R.drawable.pop, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[7], GENERSARRAY[7].toLowerCase(), R.drawable.jazz, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[5], GENERSARRAY[5].toLowerCase(), R.drawable.hip_hop, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[2], GENERSARRAY[2].toLowerCase(), R.drawable.rock, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[12], GENERSARRAY[12].toLowerCase(), R.drawable.dance, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[3], GENERSARRAY[3].toLowerCase(), R.drawable.ambient, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[4], GENERSARRAY[4].toLowerCase(), R.drawable.instrumental, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[6], GENERSARRAY[6].toLowerCase(), R.drawable.indiepop, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[8], GENERSARRAY[8].toLowerCase(), R.drawable.world, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[9], GENERSARRAY[9].toLowerCase(), R.drawable.metal, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[10], GENERSARRAY[10].toLowerCase(), R.drawable.experimental, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[11], GENERSARRAY[11].toLowerCase(), R.drawable.chillout, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[13], GENERSARRAY[13].toLowerCase(), R.drawable.country, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[14], GENERSARRAY[14].toLowerCase(), R.drawable.classic, TitleBean.GENRES_TYPE));

        list.add(new JamendoBean(GENERSARRAY[15], GENERSARRAY[15].toLowerCase(), R.drawable.reggae, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[16], GENERSARRAY[16].toLowerCase(), R.drawable.lounge, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[17], GENERSARRAY[17].toLowerCase(), R.drawable.techno, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[18], GENERSARRAY[18].toLowerCase(), R.drawable.trance, TitleBean.GENRES_TYPE));
        list.add(new JamendoBean(GENERSARRAY[19], GENERSARRAY[19].toLowerCase(), R.drawable.punk, TitleBean.GENRES_TYPE));
        return list;
    }
}
