package com.wedownloader.bean;

import android.os.Parcel;
import android.text.TextUtils;

import com.wedownloader.data.Song;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by liyanju on 2018/5/11.
 */

public class JamendoBean implements Serializable{

    public ArrayList<Song> arrayList = new ArrayList<>();


    public int type;

    public String name;

    public int imageRes;

    public String tags;


    public JamendoBean(String name, String tags, int imageRes, int type) {
        this.name = name;
        this.tags = tags;
        this.imageRes = imageRes;
        this.type = type;
    }

    public JamendoBean(){}


    public static class JamendoResult extends Song {


        public String name;
        public String artist_id;
        public String artist_name;
        public String artist_idstr;
        public String album_name;
        public String album_id;
        public String license_ccurl;
        public int position;
        public String releasedate;
        public String album_image;
        public String audio;
        public String audiodownload;
        public String shorturl;
        public String shareurl;
        public String image;
        public String lyrics;
        public long duration;

        @Override
        public long getDuration() {
            return duration;
        }

        @Override
        public String getArtistName() {
            return artist_name;
        }

        @Override
        public String getDownloadUrl() {
            return audiodownload;
        }

        @Override
        public String getPlayUrl() {
            if (TextUtils.isEmpty(audio)) {
                return audiodownload;
            }
            return audio;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getImageUrl() {
            return image;
        }

        @Override
        public int getType() {
            return Song.JAMENDO_TYPE;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeString(this.artist_id);
            dest.writeString(this.artist_name);
            dest.writeString(this.artist_idstr);
            dest.writeString(this.album_name);
            dest.writeString(this.album_id);
            dest.writeString(this.license_ccurl);
            dest.writeInt(this.position);
            dest.writeString(this.releasedate);
            dest.writeString(this.album_image);
            dest.writeString(this.audio);
            dest.writeString(this.audiodownload);
            dest.writeString(this.shorturl);
            dest.writeString(this.shareurl);
            dest.writeString(this.image);
            dest.writeString(this.lyrics);
        }

        public JamendoResult() {
        }

        protected JamendoResult(Parcel in) {
            this.name = in.readString();
            this.artist_id = in.readString();
            this.artist_name = in.readString();
            this.artist_idstr = in.readString();
            this.album_name = in.readString();
            this.album_id = in.readString();
            this.license_ccurl = in.readString();
            this.position = in.readInt();
            this.releasedate = in.readString();
            this.album_image = in.readString();
            this.audio = in.readString();
            this.audiodownload = in.readString();
            this.shorturl = in.readString();
            this.shareurl = in.readString();
            this.image = in.readString();
            this.lyrics = in.readString();
        }

        public static final Creator<JamendoResult> CREATOR = new Creator<JamendoResult>() {
            @Override
            public JamendoResult createFromParcel(Parcel source) {
                return new JamendoResult(source);
            }

            @Override
            public JamendoResult[] newArray(int size) {
                return new JamendoResult[size];
            }
        };
    }

}
