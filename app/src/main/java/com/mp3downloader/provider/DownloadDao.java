package com.mp3downloader.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.mp3downloader.model.DownloadTask;

import java.util.ArrayList;

/**
 * Created by liyanju on 2018/5/21.
 */

public class DownloadDao {

    public static DownloadTask getDownloadTaskById(Context context, int id) {
        String selection = DownloadTable.ID
                + " = ? ";
        String selectionArgs[] = new String[]{String.valueOf(id)};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(DownloadTable.URI,
                            null, selection, selectionArgs, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                return DownloadTask.cursorToDownloadSong(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static void removeDownloaded(Context context, int id) {
        String where = DownloadTable.ID + " = " + id;
        context.getContentResolver().delete(DownloadTable.URI, where, null);
    }

    public static int getNewDownloadCount(Context context) {
        Cursor cursor = null;
        try {
            String selecton = DownloadTable.NEWDOWLOAD + " = 1";
            cursor = context.getContentResolver().query(DownloadTable.URI,
                    null, selecton, null, null);
            if (cursor != null) {
                return cursor.getCount();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public static void updateDownloadNew(Context context) {
        try {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(DownloadTable.NEWDOWLOAD, 0);
            String selecton = DownloadTable.NEWDOWLOAD + " = 1";
            context.getContentResolver().update(DownloadTable.URI, contentValues,
                    selecton, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<DownloadTask> getAllDownloaded(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(DownloadTable.URI,
                    null, null, null, null);
            ArrayList<DownloadTask> list = new ArrayList<>();
            while (cursor != null && cursor.moveToNext()) {
                DownloadTask downloadSong = DownloadTask.cursorToDownloadSong(cursor);
                list.add(downloadSong);
            }
            return list;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void addDownloadTask(Context context, DownloadTask downloadSong) {
        context.getContentResolver().insert(DownloadTable.URI,
                DownloadTask.createContentValues(downloadSong));
    }
}
