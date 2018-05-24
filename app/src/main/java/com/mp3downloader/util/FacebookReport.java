package com.mp3downloader.util;

import android.os.Bundle;

import com.facebook.appevents.AppEventsLogger;
import com.mp3downloader.App;

/**
 * Created by liyanju on 2018/5/18.
 */

public class FacebookReport {


    public static void logSentRating(String str) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("rating", str);
        logger.logEvent("Rating", bundle);
    }

    public static void logSentMainPageShow() {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("soundcloud", App.isSoundCloud() ? "true" : "false");
        bundle.putString("youtube", App.isYoutube() ? "true" : "false");
        logger.logEvent("MainPageShow", bundle);
    }

    public static void logSentSearchPageShow() {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("soundcloud", App.isSoundCloud() ? "true" : "false");
        bundle.putString("youtube", App.isYoutube() ? "true" : "false");
        logger.logEvent("SearchPageShow", bundle);
    }

    public static void logSentSearchPage(String search) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("search", search);
        bundle.putString("soundcloud", App.isSoundCloud() ? "true" : "false");
        bundle.putString("youtube", App.isYoutube() ? "true" : "false");
        logger.logEvent("SearchPageShow", bundle);
    }

    public static void logSentDownloadPageShow() {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        logger.logEvent("DownloadPageShow");
    }

    public static void logSentStartDownload(String title) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        logger.logEvent("StartDownload", bundle);
    }

    public static void logSentDownloadFinish(String title) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("soundcloud", App.isSoundCloud() ? "true" : "false");
        bundle.putString("youtube", App.isYoutube() ? "true" : "false");
        logger.logEvent("DownloadFinish", bundle);
    }

    public static void logSentPlayMusic() {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("soundcloud", App.isSoundCloud() ? "true" : "false");
        bundle.putString("youtube", App.isYoutube() ? "true" : "false");
        logger.logEvent("PlayMusic", bundle);
    }


    public static void logSentUserInfo(String simCode, String phoneCode) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("sim_country", simCode);
        bundle.putString("phone_country", phoneCode);
        bundle.putString("phone_type", android.os.Build.MODEL);
        logger.logEvent("sentUserInfo",bundle);
    }

    public static void logSentFBDeepLink(String deepLink) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("deepLink", deepLink);
        logger.logEvent("SentFBDeepLink",bundle);
    }

    public static void logSentReferrer(String Referrer) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("referrer", Referrer);
        logger.logEvent("SentReferrer",bundle);
    }

    public static void logSentOpenSuper(String source) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("from_source", source);
        logger.logEvent("SentOpenSuper",bundle);
    }
}
