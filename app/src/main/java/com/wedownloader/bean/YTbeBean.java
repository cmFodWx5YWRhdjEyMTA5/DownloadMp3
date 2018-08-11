package com.wedownloader.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.wedownloader.data.Song;

import java.util.ArrayList;

/**
 * Created by liyanju on 2018/5/18.
 */

public class YTbeBean {

    public String nextPageToken;

    public ArrayList<Song> list = new ArrayList<>();

    public static class YTBSnippet extends Song {



        public String publishedAt;
        public String channelId;
        public String title;
        public String description;
        public ThumbnailsBean thumbnails;
        public String channelTitle;
        public String categoryId;

        public String vid;

        public String downloadurl;

        public long duration;

//        public ContentDetails contentDetails;
//
//        public Statistics statistics;


        @Override
        public long getDuration() {
            return 1000*60*3+1500;
        }

        @Override
        public String getDownloadUrl() {
            return downloadurl;
        }

        @Override
        public String getPlayUrl() {
            return downloadurl;
        }

        @Override
        public String getName() {
            return title;
        }

        @Override
        public String getImageUrl() {
            if (thumbnails != null) {
                return thumbnails.getThumbnails();
            }
            return "";
        }

        @Override
        public String getArtistName() {
            return description;
        }

        @Override
        public int getType() {
            return YOUTUBE_TYPE;
        }

//        public static class Statistics {
//            private String viewCount;
//            private String likeCount;
//            private String dislikeCount;
//            private String favoriteCount;
//            private String commentCount;
//
//            public String getViewCount() {
//                return viewCount;
//            }
//
//            public void setViewCount(String viewCount) {
//                this.viewCount = viewCount;
//            }
//
//            public String getLikeCount() {
//                return likeCount;
//            }
//
//            public void setLikeCount(String likeCount) {
//                this.likeCount = likeCount;
//            }
//
//            public String getDislikeCount() {
//                return dislikeCount;
//            }
//
//            public void setDislikeCount(String dislikeCount) {
//                this.dislikeCount = dislikeCount;
//            }
//
//            public String getFavoriteCount() {
//                return favoriteCount;
//            }
//
//            public void setFavoriteCount(String favoriteCount) {
//                this.favoriteCount = favoriteCount;
//            }
//
//            public String getCommentCount() {
//                return commentCount;
//            }
//
//            public void setCommentCount(String commentCount) {
//                this.commentCount = commentCount;
//            }
//        }

//        public static class ContentDetails {
//            public String duration;
//            public String dimension;
//            public String definition;
//            public String caption;
//            public boolean licensedContent;
//            public String projection;
//        }


        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof YTBSnippet
                    && (((YTBSnippet) obj).title.equals(title) && (((YTBSnippet) obj).description
                    .equals(description)))) {
                return true;
            }
            return false;
        }

        public static class ThumbnailsBean implements Parcelable{

            @SerializedName("default")
            private DefaultBean defaultX;
            private MediumBean medium;
            private HighBean high;
            private StandardBean standard;
            private MaxresBean maxres;

            public String getThumbnails() {
                if (standard != null) {
                    return standard.getUrl();
                } else if (high != null) {
                    return high.getUrl();
                }
                return "";
            }

            public DefaultBean getDefaultX() {
                return defaultX;
            }

            public void setDefaultX(DefaultBean defaultX) {
                this.defaultX = defaultX;
            }

            public MediumBean getMedium() {
                return medium;
            }

            public void setMedium(MediumBean medium) {
                this.medium = medium;
            }

            public HighBean getHigh() {
                return high;
            }

            public void setHigh(HighBean high) {
                this.high = high;
            }

            public StandardBean getStandard() {
                return standard;
            }

            public void setStandard(StandardBean standard) {
                this.standard = standard;
            }

            public MaxresBean getMaxres() {
                return maxres;
            }

            public void setMaxres(MaxresBean maxres) {
                this.maxres = maxres;
            }

            public static class DefaultBean implements Parcelable{
                /**
                 * url : https://i.ytimg.com/vi/6ajP1v4Dgfs/default.jpg
                 * width : 120
                 * height : 90
                 */

                private String url;
                private int width;
                private int height;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }


                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(this.url);
                    dest.writeInt(this.width);
                    dest.writeInt(this.height);
                }

                public DefaultBean() {
                }

                protected DefaultBean(Parcel in) {
                    this.url = in.readString();
                    this.width = in.readInt();
                    this.height = in.readInt();
                }

                public static final Creator<DefaultBean> CREATOR = new Creator<DefaultBean>() {
                    @Override
                    public DefaultBean createFromParcel(Parcel source) {
                        return new DefaultBean(source);
                    }

                    @Override
                    public DefaultBean[] newArray(int size) {
                        return new DefaultBean[size];
                    }
                };
            }

            public static class MediumBean implements Parcelable{
                /**
                 * url : https://i.ytimg.com/vi/6ajP1v4Dgfs/mqdefault.jpg
                 * width : 320
                 * height : 180
                 */

                private String url;
                private int width;
                private int height;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }


                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(this.url);
                    dest.writeInt(this.width);
                    dest.writeInt(this.height);
                }

                public MediumBean() {
                }

                protected MediumBean(Parcel in) {
                    this.url = in.readString();
                    this.width = in.readInt();
                    this.height = in.readInt();
                }

                public static final Creator<MediumBean> CREATOR = new Creator<MediumBean>() {
                    @Override
                    public MediumBean createFromParcel(Parcel source) {
                        return new MediumBean(source);
                    }

                    @Override
                    public MediumBean[] newArray(int size) {
                        return new MediumBean[size];
                    }
                };
            }

            public static class HighBean implements Parcelable{
                /**
                 * url : https://i.ytimg.com/vi/6ajP1v4Dgfs/hqdefault.jpg
                 * width : 480
                 * height : 360
                 */

                private String url;
                private int width;
                private int height;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }


                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(this.url);
                    dest.writeInt(this.width);
                    dest.writeInt(this.height);
                }

                public HighBean() {
                }

                protected HighBean(Parcel in) {
                    this.url = in.readString();
                    this.width = in.readInt();
                    this.height = in.readInt();
                }

                public static final Creator<HighBean> CREATOR = new Creator<HighBean>() {
                    @Override
                    public HighBean createFromParcel(Parcel source) {
                        return new HighBean(source);
                    }

                    @Override
                    public HighBean[] newArray(int size) {
                        return new HighBean[size];
                    }
                };
            }

            public static class StandardBean implements Parcelable{
                /**
                 * url : https://i.ytimg.com/vi/6ajP1v4Dgfs/sddefault.jpg
                 * width : 640
                 * height : 480
                 */

                private String url;
                private int width;
                private int height;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(this.url);
                    dest.writeInt(this.width);
                    dest.writeInt(this.height);
                }

                public StandardBean() {
                }

                protected StandardBean(Parcel in) {
                    this.url = in.readString();
                    this.width = in.readInt();
                    this.height = in.readInt();
                }

                public static final Creator<StandardBean> CREATOR = new Creator<StandardBean>() {
                    @Override
                    public StandardBean createFromParcel(Parcel source) {
                        return new StandardBean(source);
                    }

                    @Override
                    public StandardBean[] newArray(int size) {
                        return new StandardBean[size];
                    }
                };
            }

            public static class MaxresBean implements Parcelable{
                /**
                 * url : https://i.ytimg.com/vi/6ajP1v4Dgfs/maxresdefault.jpg
                 * width : 1280
                 * height : 720
                 */

                private String url;
                private int width;
                private int height;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }


                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(this.url);
                    dest.writeInt(this.width);
                    dest.writeInt(this.height);
                }

                public MaxresBean() {
                }

                protected MaxresBean(Parcel in) {
                    this.url = in.readString();
                    this.width = in.readInt();
                    this.height = in.readInt();
                }

                public static final Creator<MaxresBean> CREATOR = new Creator<MaxresBean>() {
                    @Override
                    public MaxresBean createFromParcel(Parcel source) {
                        return new MaxresBean(source);
                    }

                    @Override
                    public MaxresBean[] newArray(int size) {
                        return new MaxresBean[size];
                    }
                };
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeParcelable(this.defaultX, flags);
                dest.writeParcelable(this.medium, flags);
                dest.writeParcelable(this.high, flags);
                dest.writeParcelable(this.standard, flags);
                dest.writeParcelable(this.maxres, flags);
            }

            public ThumbnailsBean() {
            }

            protected ThumbnailsBean(Parcel in) {
                this.defaultX = in.readParcelable(DefaultBean.class.getClassLoader());
                this.medium = in.readParcelable(MediumBean.class.getClassLoader());
                this.high = in.readParcelable(HighBean.class.getClassLoader());
                this.standard = in.readParcelable(StandardBean.class.getClassLoader());
                this.maxres = in.readParcelable(MaxresBean.class.getClassLoader());
            }

            public static final Creator<ThumbnailsBean> CREATOR = new Creator<ThumbnailsBean>() {
                @Override
                public ThumbnailsBean createFromParcel(Parcel source) {
                    return new ThumbnailsBean(source);
                }

                @Override
                public ThumbnailsBean[] newArray(int size) {
                    return new ThumbnailsBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.publishedAt);
            dest.writeString(this.channelId);
            dest.writeString(this.title);
            dest.writeString(this.description);
            dest.writeParcelable(this.thumbnails, flags);
            dest.writeString(this.channelTitle);
            dest.writeString(this.categoryId);
            dest.writeString(this.vid);
            dest.writeString(this.downloadurl);
        }

        public YTBSnippet() {
        }

        protected YTBSnippet(Parcel in) {
            this.publishedAt = in.readString();
            this.channelId = in.readString();
            this.title = in.readString();
            this.description = in.readString();
            this.thumbnails = in.readParcelable(ThumbnailsBean.class.getClassLoader());
            this.channelTitle = in.readString();
            this.categoryId = in.readString();
            this.vid = in.readString();
            this.downloadurl = in.readString();
        }

        public static final Creator<YTBSnippet> CREATOR = new Creator<YTBSnippet>() {
            @Override
            public YTBSnippet createFromParcel(Parcel source) {
                return new YTBSnippet(source);
            }

            @Override
            public YTBSnippet[] newArray(int size) {
                return new YTBSnippet[size];
            }
        };
    }
}
