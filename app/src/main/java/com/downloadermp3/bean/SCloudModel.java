package com.downloadermp3.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.downloadermp3.data.BaseModel;
import com.downloadermp3.data.soundcloud.SoundCloudService;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by liyanju on 2018/5/16.
 */

public class SCloudModel {

    public ArrayList<BaseModel> arrayList = new ArrayList<>();

    public static class SCloudResult extends BaseModel {


        @SerializedName("stream_url")
        private String mStreamURL;

        @SerializedName("artwork_url")
        private String mArtworkURL;

        private String created_at;

        public String title;

        public User user;

        public long duration;

        @Override
        public long getDuration() {
            return duration;
        }

        @Override
        public String getDownloadUrl() {
            return mStreamURL + "?client_id=" + SoundCloudService.CLIENT_ID;
        }

        @Override
        public String getPlayUrl() {
            return mStreamURL + "?client_id=" + SoundCloudService.CLIENT_ID;
        }

        public static class User implements Parcelable{

            public String username;

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.username);
            }

            public User() {
            }

            protected User(Parcel in) {
                this.username = in.readString();
            }

            public static final Creator<User> CREATOR = new Creator<User>() {
                @Override
                public User createFromParcel(Parcel source) {
                    return new User(source);
                }

                @Override
                public User[] newArray(int size) {
                    return new User[size];
                }
            };
        }

        @Override
        public String getName() {
            return title;
        }

        @Override
        public String getImageUrl() {
            return mArtworkURL;
        }

        @Override
        public String getArtistName() {
            return user != null ? user.username : "";
        }


        @Override
        public int getType() {
            return BaseModel.SOUNDCLOUD_TYPE;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mStreamURL);
            dest.writeString(this.mArtworkURL);
            dest.writeString(this.created_at);
            dest.writeString(this.title);
            dest.writeParcelable(this.user, flags);
            dest.writeLong(this.duration);
        }

        public SCloudResult() {
        }

        protected SCloudResult(Parcel in) {
            this.mStreamURL = in.readString();
            this.mArtworkURL = in.readString();
            this.created_at = in.readString();
            this.title = in.readString();
            this.user = in.readParcelable(User.class.getClassLoader());
            this.duration = in.readLong();
        }

        public static final Creator<SCloudResult> CREATOR = new Creator<SCloudResult>() {
            @Override
            public SCloudResult createFromParcel(Parcel source) {
                return new SCloudResult(source);
            }

            @Override
            public SCloudResult[] newArray(int size) {
                return new SCloudResult[size];
            }
        };
    }
}
