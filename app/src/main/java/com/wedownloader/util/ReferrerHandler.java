package com.wedownloader.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.net.URLDecoder;

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

    public static void setSingytb() {
        sRangeHandler.setSingleYoutube();
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

    public boolean isFacebookOpen(String referrer) {
        try {
            String decodeReferrer = URLDecoder.decode(referrer, "utf-8");
            String utmSource = getUtmSource(decodeReferrer);
            if (!TextUtils.isEmpty(utmSource) && utmSource.contains("not set")) {
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String getUtmSource(String str) {
        if (!TextUtils.isEmpty(str)) {
            String[] split = str.split("&");
            if (split != null && split.length >= 0) {
                for (String str2 : split) {
                    if (str2 != null && str2.contains("utm_source")) {
                        String[] split2 = str2.split("=");
                        if (split2 != null && split2.length > 1) {
                            return split2[1];
                        }
                    }
                }
            }
        }
        return null;
    }
}
