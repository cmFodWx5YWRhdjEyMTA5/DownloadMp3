package com.freedownloader.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

import com.freedownloader.db.DownloadTable;

/**
 * Created by liyanju on 2018/5/21.
 */

public class DownloadTask extends Song {

    private String title;
    private String path;
    private String image;
    private long duration;
    public int id;
    public boolean isNewDowload = true;
    private String artist;

    public DownloadTask(String title, String path, String image, long duration, int id, String artist) {
        this.title = title;
        this.path = path;
        this.image = image;
        this.duration = duration;
        this.id = id;
        this.artist = artist;
    }

    public DownloadTask(){}

    public static DownloadTask cursorToDownloadSong(Cursor cursor) {
        DownloadTask downloadSong = new DownloadTask();
        downloadSong.title = cursor.getString(cursor.getColumnIndexOrThrow(DownloadTable.NAME));
        downloadSong.path = cursor.getString(cursor.getColumnIndexOrThrow(DownloadTable.PATH));
        downloadSong.image = cursor.getString(cursor.getColumnIndexOrThrow(DownloadTable.IMAGE));
        downloadSong.duration = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadTable.DURATION));
        downloadSong.id = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadTable.ID));
        downloadSong.isNewDowload = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadTable.NEWDOWLOAD)) == 1;
        downloadSong.artist = cursor.getString(cursor.getColumnIndexOrThrow(DownloadTable.ARTIST));
        return downloadSong;
    }

    public static ContentValues createContentValues(DownloadTask downloadSong) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DownloadTable.NAME, downloadSong.title);
        contentValues.put(DownloadTable.PATH, downloadSong.path);
        contentValues.put(DownloadTable.IMAGE, downloadSong.image);
        contentValues.put(DownloadTable.NEWDOWLOAD, downloadSong.isNewDowload ? 1 : 0);
        contentValues.put(DownloadTable.ID, downloadSong.id);
        contentValues.put(DownloadTable.DURATION, downloadSong.duration);
        contentValues.put(DownloadTable.ARTIST, downloadSong.artist);
        return contentValues;
    }

    @Override
    public String getDownloadUrl() {
        return "";
    }

    @Override
    public String getPlayUrl() {
        return path;
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public String getImageUrl() {
        return image;
    }

    @Override
    public String getArtistName() {
        return null;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.path);
        dest.writeString(this.image);
        dest.writeLong(this.duration);
        dest.writeInt(this.id);
        dest.writeByte(this.isNewDowload ? (byte) 1 : (byte) 0);
    }

    protected DownloadTask(Parcel in) {
        this.title = in.readString();
        this.path = in.readString();
        this.image = in.readString();
        this.duration = in.readLong();
        this.id = in.readInt();
        this.isNewDowload = in.readByte() != 0;
    }

    public static final Creator<DownloadTask> CREATOR = new Creator<DownloadTask>() {
        @Override
        public DownloadTask createFromParcel(Parcel source) {
            return new DownloadTask(source);
        }

        @Override
        public DownloadTask[] newArray(int size) {
            return new DownloadTask[size];
        }
    };
}
