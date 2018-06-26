package com.freedownloader.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

/**
 * Created by liyanju on 2018/5/21.
 */

public class Mp3DownloadProvider extends BaseContentProvider {

    public static final String AUTHORITIES = "com.freedownload.db.music.downloadprovider";

    public static final int DOWNLOADEDKEY = 102;

    @Override
    public void onAddTableInfo(SparseArray<TableInfo> tableInfoArray) {
        tableInfoArray.put(DOWNLOADEDKEY, new DownloadTable());
    }

    @Override
    public String onDataBaseName() {
        return "FreeDownload";
    }

    @Override
    public int onDataBaseVersion() {
        return 1;
    }

    @Override
    public void onDBUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
