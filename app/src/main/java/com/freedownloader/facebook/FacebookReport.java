package com.freedownloader.facebook;

import android.os.Bundle;

import com.freedownloader.Mp3App;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by liyanju on 2018/5/18.
 */

public class FacebookReport {

    public static void logSentUSOpen(boolean isWeek, String time){
        AppEventsLogger logger = AppEventsLogger.newLogger(Mp3App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("week", isWeek ? "true" : "false");
        bundle.putString("day", !isWeek ? "true" : "false");
        bundle.putString("time", time);
        logger.logEvent("logSentUSOpen", bundle);
    }

    public static void logSentMainPageShow() {
        AppEventsLogger logger = AppEventsLogger.newLogger(Mp3App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("scloud", Mp3App.isSCloud() ? "true" : "false");
        bundle.putString("ytb", Mp3App.isYTB() ? "true" : "false");
        logger.logEvent("logMainPageShow", bundle);
    }

    public static void logSentSearchPageShow() {
        AppEventsLogger logger = AppEventsLogger.newLogger(Mp3App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("scloud", Mp3App.isSCloud() ? "true" : "false");
        bundle.putString("ytb", Mp3App.isYTB() ? "true" : "false");
        logger.logEvent("logSearchPageShow", bundle);
    }

    public static void logSentSearchPage(String search) {
        AppEventsLogger logger = AppEventsLogger.newLogger(Mp3App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("search", search);
        bundle.putString("scloud", Mp3App.isSCloud() ? "true" : "false");
        bundle.putString("ytb", Mp3App.isYTB() ? "true" : "false");
        logger.logEvent("SearchPageShow", bundle);
    }

    public static void logSentDownloadPageShow() {
        AppEventsLogger logger = AppEventsLogger.newLogger(Mp3App.sContext);
        logger.logEvent("logDownloadPageShow");
    }

    public static void logSentDownloadFinish(String title) {
        AppEventsLogger logger = AppEventsLogger.newLogger(Mp3App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("scloud", Mp3App.isSCloud() ? "true" : "false");
        bundle.putString("ytb", Mp3App.isYTB() ? "true" : "false");
        logger.logEvent("logDownloadFinish", bundle);
    }

    public static void logSentPlayMusic() {
        AppEventsLogger logger = AppEventsLogger.newLogger(Mp3App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("scloud", Mp3App.isSCloud() ? "true" : "false");
        bundle.putString("ytb", Mp3App.isYTB() ? "true" : "false");
        logger.logEvent("PlayMp3", bundle);
    }


    public static void logSentReferrer(String Referrer) {
        AppEventsLogger logger = AppEventsLogger.newLogger(Mp3App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("referrer", Referrer);
        logger.logEvent("logsentReferrer",bundle);
    }

    public static void logSentOpenSuper(String source) {
        AppEventsLogger logger = AppEventsLogger.newLogger(Mp3App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("from", source);
        logger.logEvent("logsentOpenSuper",bundle);
    }

    public static void logSentUserInfo(String simCode, String phoneCode) {
        AppEventsLogger logger = AppEventsLogger.newLogger(Mp3App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("sim_ct", simCode);
        bundle.putString("phone_ct", phoneCode);
        bundle.putString("phone", android.os.Build.MODEL);
        logger.logEvent("logsentUserInfo",bundle);
    }

    public static void logSentStartDownload(String title) {
        AppEventsLogger logger = AppEventsLogger.newLogger(Mp3App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        logger.logEvent("logStartDownload", bundle);
    }

    public static void logSentRating(String str) {
        AppEventsLogger logger = AppEventsLogger.newLogger(Mp3App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("rating", str);
        logger.logEvent("logRating", bundle);
    }
}
