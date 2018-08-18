package com.wedownloader.facebook;

import android.os.Bundle;

import com.wedownloader.MusicApp;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by liyanju on 2018/5/18.
 */

public class FacebookReport {

    public static void logSentUSOpen(boolean isWeek, String time){
        AppEventsLogger logger = AppEventsLogger.newLogger(MusicApp.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("week", isWeek ? "true" : "false");
        bundle.putString("day", !isWeek ? "true" : "false");
        bundle.putString("time", time);
        logger.logEvent("SentUSOpenFaster", bundle);
    }

    public static void logSentMainPageShow() {
        AppEventsLogger logger = AppEventsLogger.newLogger(MusicApp.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("scloud_from", MusicApp.isSCloud() ? "true" : "false");
        bundle.putString("ytb_from", MusicApp.isYTB() ? "true" : "false");
        bundle.putString("single_from", MusicApp.isSingYTB() ? "true" : "false");
        logger.logEvent("MainPageShow", bundle);
    }

    public static void logSentSearchPageShow() {
        AppEventsLogger logger = AppEventsLogger.newLogger(MusicApp.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("scloud", MusicApp.isSCloud() ? "true" : "false");
        bundle.putString("ytb", MusicApp.isYTB() ? "true" : "false");
        bundle.putString("single", MusicApp.isSingYTB() ? "true" : "false");
        logger.logEvent("SearchPageShow", bundle);
    }

    public static void logSentSearchPage(String search) {
        AppEventsLogger logger = AppEventsLogger.newLogger(MusicApp.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("search", search);
        bundle.putString("scloud", MusicApp.isSCloud() ? "true" : "false");
        bundle.putString("ytb", MusicApp.isYTB() ? "true" : "false");
        bundle.putString("single", MusicApp.isSingYTB() ? "true" : "false");
        logger.logEvent("SearchPageShow", bundle);
    }

    public static void logSentDownloadFinish(String title) {
        AppEventsLogger logger = AppEventsLogger.newLogger(MusicApp.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("scloud", MusicApp.isSCloud() ? "true" : "false");
        bundle.putString("ytb", MusicApp.isYTB() ? "true" : "false");
        logger.logEvent("DownloadFinish", bundle);
    }

    public static void logSentPlayMusic() {
        AppEventsLogger logger = AppEventsLogger.newLogger(MusicApp.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("scloud", MusicApp.isSCloud() ? "true" : "false");
        bundle.putString("ytb", MusicApp.isYTB() ? "true" : "false");
        bundle.putString("single", MusicApp.isSingYTB() ? "true" : "false");
        logger.logEvent("PlayMp3", bundle);
    }


    public static void logSentReferrer(String Referrer) {
        AppEventsLogger logger = AppEventsLogger.newLogger(MusicApp.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("referrer", Referrer);
        logger.logEvent("sentReferrer",bundle);
    }

    public static void logSentOpenSuper(String source) {
        AppEventsLogger logger = AppEventsLogger.newLogger(MusicApp.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("from", source);
        logger.logEvent("sentOpenFaster",bundle);
    }

    public static void logSentBuyUserOpen(String source) {
        AppEventsLogger logger = AppEventsLogger.newLogger(MusicApp.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("from", source);
        logger.logEvent("sentBuyUsers",bundle);
    }

    public static void logSentUserInfo(String simCode, String phoneCode) {
        AppEventsLogger logger = AppEventsLogger.newLogger(MusicApp.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("sim_code", simCode);
        bundle.putString("phone_code", phoneCode);
        bundle.putString("phone", android.os.Build.MODEL);
        logger.logEvent("sentUserInfo",bundle);
    }

    public static void logSentStartDownload(String title) {
        AppEventsLogger logger = AppEventsLogger.newLogger(MusicApp.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        logger.logEvent("StartDownload", bundle);
    }

    public static void logSentRating(String str) {
        AppEventsLogger logger = AppEventsLogger.newLogger(MusicApp.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("rating_str", str);
        logger.logEvent("Rating", bundle);
    }
}