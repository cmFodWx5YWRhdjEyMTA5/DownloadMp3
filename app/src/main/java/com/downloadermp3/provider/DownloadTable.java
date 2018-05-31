package com.downloadermp3.provider;

import android.net.Uri;

import java.util.Map;

/**
 * Created by liyanju on 2018/5/21.
 */

public class DownloadTable extends TableInfo{

    public static final String TABLE_NAME = "DownloadedMp3";

    public static final Uri URI = Uri.parse("content://" + Mp3DownloadProvider.AUTHORITIES + "/" + TABLE_NAME);

    public static final String PATH = "mp3_path";
    public static final String NAME = "mp3_name";
    public static final String IMAGE = "mp3_image";
    public static final String DURATION = "mp3_duration";
    public static final String NEWDOWLOAD = "mp3_newdownload";
    public static final String ID = "mp3_id";
    public static final String ARTIST = "mp3_artist";

    @Override
    public String onTableName() {
        return TABLE_NAME;
    }

    @Override
    public Uri onContentUri() {
        return URI;
    }

    @Override
    public void onInitColumnsMap(Map<String, String> columnsMap) {
        columnsMap.put(ID, "int");
        columnsMap.put(PATH, "text");
        columnsMap.put(NAME, "text");
        columnsMap.put(IMAGE, "text");
        columnsMap.put(DURATION, "int");
        columnsMap.put(NEWDOWLOAD, "int");
        columnsMap.put(ARTIST, "text");
    }
}
