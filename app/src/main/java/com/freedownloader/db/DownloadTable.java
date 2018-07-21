package com.freedownloader.db;

import android.net.Uri;

import java.util.Map;

/**
 * Created by liyanju on 2018/5/21.
 */

public class DownloadTable extends TableInfo{

    public static final String TABLE_NAME = "wedownloaded";

    public static final Uri URI = Uri.parse("content://" + Mp3DownloadProvider.AUTHORITIES + "/" + TABLE_NAME);

    public static final String PATH = "path";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String DURATION = "duration";
    public static final String NEWDOWLOAD = "isnewdownload";
    public static final String ID = "id";
    public static final String ARTIST = "artist";

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
        columnsMap.put(NEWDOWLOAD, "int");
        columnsMap.put(ARTIST, "text");
        columnsMap.put(IMAGE, "text");
        columnsMap.put(DURATION, "int");
    }
}
