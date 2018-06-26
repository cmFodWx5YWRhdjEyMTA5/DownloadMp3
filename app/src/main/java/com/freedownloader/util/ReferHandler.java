package com.freedownloader.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.freedownloader.BuildConfig;
import com.freedownloader.Mp3App;
import com.freedownloader.facebook.FacebookReport;

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
                FacebookReport.logSentOpenSuper("open admob");
            } else if (referrerHandler.isFacebookOpen(referrer)) {
                FacebookReport.logSentOpenSuper("open facebook");
            } else {
                ReferrerHandler.sRangeHandler.countryIfShow(context);
            }

        }
    }
}
