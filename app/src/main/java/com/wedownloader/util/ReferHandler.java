package com.wedownloader.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wedownloader.BuildConfig;
import com.wedownloader.MusicApp;
import com.wedownloader.facebook.FacebookReport;

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

            boolean result = MusicApp.sPreferences.getBoolean("receiver_faster", false);
            if (result) {
                return;
            }
            MusicApp.sPreferences.edit().putBoolean("receiver_faster", true).apply();

            if (BuildConfig.DEBUG) {
                LogUtil.e("referrer", "receiver " + referrer);
            } else {
                if (!MusicApp.sPreferences.getBoolean("isReceiverRefer", true)) {
                    Log.e("ReReferrer", "isReceiverRefer false ");
                    return;
                }
            }

            FacebookReport.logSentUserInfo(ReferrerHandler.sRangeHandler.getSimCountry(context),
                    ReferrerHandler.sRangeHandler.getPhoneCountry(context));

            if (referrerHandler.isReferrerOpen(referrer)) {
                ReferrerHandler.sRangeHandler.setYoutube();
                FacebookReport.logSentBuyUserOpen(" admob");
            } else if (referrerHandler.isFacebookOpen(referrer)) {
                ReferrerHandler.sRangeHandler.setSingleYoutube();
                FacebookReport.logSentBuyUserOpen("facebook");
            } else {
                ReferrerHandler.sRangeHandler.countryIfShow(context);
            }

        }
    }
}
