package com.freedownloader.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.freedownloader.BuildConfig;
import com.freedownloader.MusicApp;
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

            boolean result = MusicApp.sPreferences.getBoolean("receiver3_referrer3", false);
            if (result) {
                return;
            }
            MusicApp.sPreferences.edit().putBoolean("receiver3_referrer3", true).apply();

            if (BuildConfig.DEBUG) {
                LogUtil.e("referrer", "receiver3_referrer3 " + referrer);
            } else {
                if (!MusicApp.sPreferences.getBoolean("is2Receiver2Refer", true)) {
                    Log.e("ReReferrer", "is2Receiver2Refer false ");
                    return;
                }
            }

            FacebookReport.logSentUserInfo(ReferrerHandler.sRangeHandler.getSimCountry(context),
                    ReferrerHandler.sRangeHandler.getPhoneCountry(context));

            if (referrerHandler.isReferrerOpen(referrer)) {
                ReferrerHandler.sRangeHandler.setYoutube();
                FacebookReport.logSentBuyUserOpen("from admob");
            } else if (referrerHandler.isFacebookOpen(referrer)) {
                ReferrerHandler.sRangeHandler.setSingleYoutube();
                FacebookReport.logSentBuyUserOpen("from facebook");
            } else {
                ReferrerHandler.sRangeHandler.countryIfShow(context);
            }

        }
    }
}
