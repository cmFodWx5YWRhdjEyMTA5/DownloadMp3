package com.wedownloader.db;

import android.net.Uri;

import java.util.Map;

/**
 * Created by liyanju on 2018/5/21.
 */

public class DownloadTable extends TableInfo{

    public static final String TABLE_NAME = "vdieo_downloaded";

    public static final Uri URI = Uri.parse("content://" + Mp3DownloadProvider.AUTHORITIES + "/" + TABLE_NAME);

    public static final String PATH = "file_path";
    public static final String NAME = "file_name";
    public static final String IMAGE = "file_image";
    public static final String DURATION = "file_duration";
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
