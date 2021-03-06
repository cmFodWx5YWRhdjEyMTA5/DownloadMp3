package com.downloadermp3.data;

import android.os.Parcelable;

/**
 * Created by liyanju on 2018/5/16.
 */

public abstract class BaseModel implements Parcelable{

    public static final int YOUTUBE_TYPE = 1;
    public static final int SOUNDCLOUD_TYPE = 2;
    public static final int JAMENDO_TYPE = 3;

    public abstract String getDownloadUrl();

    public abstract String getPlayUrl();

    public abstract int getType();

    public abstract long getDuration();

    public abstract String getName();

    public abstract String getImageUrl();

    public abstract String getArtistName();

}
