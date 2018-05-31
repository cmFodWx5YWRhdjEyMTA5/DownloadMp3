package com.downloadermp3.util;

import android.content.Context;
import android.content.Intent;

/**
 * Created by liyanju on 2018/5/31.
 */

public class ReferrerHandler {

    private ReferHandler.ReferrerReceiverHandler sHandler = ReferHandler
            .createInstallReferrerReceiverHandler();

    public static RangeHandler sRangeHandler = new RangeHandler();

    public static void setSCloud() {
        sRangeHandler.setSoundCloud();
    }

    public static void setYTB() {
        sRangeHandler.setYoutube();
    }

    public static void initReferrer() {
        sRangeHandler.initSpecial();
    }

    private static volatile ReferrerHandler sReferrerHandler;

    public static ReferrerHandler getInstances() {
        if (sReferrerHandler == null) {
            synchronized (ReferrerHandler.class) {
                if (sReferrerHandler == null) {
                    sReferrerHandler = new ReferrerHandler();
                }
            }
        }
        return sReferrerHandler;
    }

    public void handleIntent(Context context, Intent intent) {
        sHandler.onHandleIntent(context, intent, this);
    }

    public boolean isReferrerOpen(String referrer) {
        if (referrer.startsWith("campaigntype=")
                && referrer.contains("campaignid=")) {
            return true;
        } else {
            return false;
        }
    }
}
