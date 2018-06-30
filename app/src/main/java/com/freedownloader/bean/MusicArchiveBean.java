package com.freedownloader.bean;

import android.os.Parcel;
import android.text.TextUtils;

import com.freedownloader.data.Song;
import com.freedownloader.util.LogUtil;
import com.freedownloader.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by liyanju on 2018/6/20.
 */

public class MusicArchiveBean implements Serializable{

    public static final int PAGE_LIMIT = 25;

    public static final int FEATURED_TYPE = 0;
    public static final int RECENT_TYPE = 1;

    public int type;

    public int total_pages;

    public ArrayList<Song> contentList = new ArrayList<>();

    public static class Content extends Song {

        public String track_title;

        public String track_duration; //"05:47"

        public String track_date_created;

        public String license_title;

        public String license_url;

        public String track_file_url;

        public String track_listen_url;

        public String track_url;

        public String album_title;

        public String artist_name;

        public String track_image_file;

        public int position;

        public String track_file;

        @Override
        public String getDownloadUrl() {
            return getPlayUrl();
        }

        @Override
        public String getPlayUrl() {
            if (!TextUtils.isEmpty(track_file) && !track_file.contains("https://")) {
                track_file = "https://freemusicarchive.org/file/" + track_file;
            }
            return track_file;
        }

        @Override
        public int getType() {
            return MUSIC_ARICH;
        }

        @Override
        public long getDuration() {
            return Utils.durationChange(track_duration) * 1000;
        }

        @Override
        public String getName() {
            return track_title;
        }

        @Override
        public String getImageUrl() {
            if (!TextUtils.isEmpty(track_image_file) && !track_image_file.contains("https://")) {
                track_image_file = "https://freemusicarchive.org/file/" + track_image_file;
            }
            LogUtil.v("img", " track_image_file: " + track_image_file);
            return track_image_file;
        }

        @Override
        public String getArtistName() {
            return artist_name;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.track_title);
            dest.writeString(this.track_duration);
            dest.writeString(this.track_date_created);
            dest.writeString(this.license_title);
            dest.writeString(this.license_url);
            dest.writeString(this.track_file_url);
            dest.writeString(this.track_listen_url);
            dest.writeString(this.track_url);
            dest.writeString(this.album_title);
            dest.writeString(this.artist_name);
            dest.writeString(this.track_image_file);
            dest.writeInt(this.position);
            dest.writeString(this.track_file);
        }

        public Content() {
        }

        protected Content(Parcel in) {
            this.track_title = in.readString();
            this.track_duration = in.readString();
            this.track_date_created = in.readString();
            this.license_title = in.readString();
            this.license_url = in.readString();
            this.track_file_url = in.readString();
            this.track_listen_url = in.readString();
            this.track_url = in.readString();
            this.album_title = in.readString();
            this.artist_name = in.readString();
            this.track_image_file = in.readString();
            this.position = in.readInt();
            this.track_file = in.readString();
        }

        public static final Creator<Content> CREATOR = new Creator<Content>() {
            @Override
            public Content createFromParcel(Parcel source) {
                return new Content(source);
            }

            @Override
            public Content[] newArray(int size) {
                return new Content[size];
            }
        };
    }
}
