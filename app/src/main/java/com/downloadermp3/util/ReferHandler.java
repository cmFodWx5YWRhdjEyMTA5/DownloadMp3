package com.downloadermp3.util;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.downloadermp3.BuildConfig;
import com.downloadermp3.Mp3App;
import com.downloadermp3.facebook.FacebookReport;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by liyanju on 2018/4/3.
 */

public class ReferHandler {

    public static ReferrerReceiverHandler createInstallReferrerReceiverHandler() {
        return new ReferrerReceiverHandler();
    }

    public static class ReferrerReceiverHandler {

        public void onHandleIntent(Context context, Intent intent, ReferrerHandler referrerHandler) {

            String referrer = intent.getStringExtra("referrer");
            if (referrer == null) {
                return;
            }

            FacebookReport.logSentReferrer(referrer);

            boolean result = Mp3App.sPreferences.getBoolean("receiver_referrer", false);
            if (result) {
                return;
            }
            Mp3App.sPreferences.edit().putBoolean("receiver_referrer", true).apply();

            if (BuildConfig.DEBUG) {
                LogUtil.e("referrer", "receiver_referrer " + referrer);
            } else {
                if (!Mp3App.sPreferences.getBoolean("isReceiverRefer", true)) {
                    Log.e("MReReferrer", "isReceiverRefer false ");
                    return;
                }
            }

            FacebookReport.logSentUserInfo(ReferrerHandler.sRangeHandler.getSimCountry(context),
                    ReferrerHandler.sRangeHandler.getPhoneCountry(context));

            if (referrerHandler.isReferrerOpen(referrer)) {
                ReferrerHandler.sRangeHandler.setYoutube();
                FacebookReport.logSentOpenSuper("admob for open");
            } else if (RangeHandler.isFacebookOpen(referrer)) {
                ReferrerHandler.sRangeHandler.setSoundCloud();
                FacebookReport.logSentOpenSuper("admob for facebook");
            } else {
                ReferrerHandler.sRangeHandler.countryIfShow(context);
            }

        }
    }
}
