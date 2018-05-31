package com.downloadermp3.provider;

import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

/**
 * Created by liyanju on 2018/5/21.
 */

public class Mp3DownloadProvider extends BaseContentProvider {

    public static final String AUTHORITIES = "com.downloadermp3.provider.MyDownloadProvider";

    public static final int DOWNLOADEDKEY = 102;

    @Override
    public void onAddTableInfo(SparseArray<TableInfo> tableInfoArray) {
        tableInfoArray.put(DOWNLOADEDKEY, new DownloadTable());
    }

    @Override
    public String onDataBaseName() {
        return "Mp3Download";
    }

    @Override
    public int onDataBaseVersion() {
        return 1;
    }

    @Override
    public void onDBUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
