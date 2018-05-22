package com.mp3downloader.util;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.mp3downloader.App;

import java.util.Locale;

/**
 * Created by liyanju on 2018/4/3.
 */

public class ReferVersions {


    public static void setSoundCloud() {
        SuperVersionHandler.setSoundCloud();
    }

    public static void setYouTube() {
        SuperVersionHandler.setYoutube();
    }

    public static void initSuper() {
        SuperVersionHandler.initSpecial();
    }


    public static MultipleReferrerReceiverHandler createInstallReferrerReceiverHandler() {
        return new MultipleReferrerReceiverHandler();
    }

    public static class SuperVersionHandler {

        private static volatile boolean isSoundClound = false;

        private static volatile boolean isYoutube = false;

        public static void setSoundCloud() {
            isSoundClound = true;
            App.sPreferences.edit().putBoolean(Constants.KEY_SOUNDCLOUD, true).apply();
        }

        public static void setYoutube() {
            isYoutube = true;
            App.sPreferences.edit().putBoolean(Constants.KEY_YOUTUBE, true).apply();
        }

        public static String getPhoneCountry(Context context) {
            String country = "";
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager.getPhoneType()
                        != TelephonyManager.PHONE_TYPE_CDMA) {
                    country = telephonyManager.getNetworkCountryIso();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return country;
        }

        public static String getCountry2(Context context) {
            String country = "";
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String simCountry = telephonyManager.getSimCountryIso();
                if (simCountry != null && simCountry.length() == 2) {
                    country = simCountry.toUpperCase(Locale.ENGLISH);
                } else if (telephonyManager.getPhoneType()
                        != TelephonyManager.PHONE_TYPE_CDMA) {
                    country = telephonyManager.getNetworkCountryIso();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return country;
        }

        public static String getSimCountry(Context context) {
            String country = "";
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String simCountry = telephonyManager.getSimCountryIso();
                if (simCountry != null && simCountry.length() == 2) {
                    country = simCountry.toUpperCase(Locale.ENGLISH);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return country;
        }

        public static void initSpecial() {
            isSoundClound = App.sPreferences.getBoolean(Constants.KEY_SOUNDCLOUD, false);
            isYoutube = App.sPreferences.getBoolean(Constants.KEY_YOUTUBE, false);
        }

        public static boolean isSoundClound() {
            return isSoundClound;
        }

        public static boolean isYoutube() {
            return isYoutube;
        }

        public static boolean isReferrerOpen(String referrer) {
            if (referrer.startsWith("campaigntype=")
                    && referrer.contains("campaignid=")) {
                return true;
            } else {
                return false;
            }
        }

        private static boolean countryIfShow2(String country) {
            if ("ph".equals(country.toLowerCase())) {
                return true;
            }

            if ("it".equals(country.toLowerCase())) {
                return true;
            }

            if ("de".equals(country.toLowerCase())) {
                return true;
            }

            if ("mx".equals(country.toLowerCase())) {
                return true;
            }

            if ("id".equals(country.toLowerCase())) {
                return true;
            }

            if ("gb".equals(country.toLowerCase())) {
                return true;
            }

            if ("fr".equals(country.toLowerCase())) {
                return true;
            }

            if ("au".equals(country.toLowerCase())) {
                return true;
            }

            return false;
        }

        private static boolean countryIfShow(String country) {
            if ("id".equals(country.toLowerCase())) {
                return true;
            }

            if ("br".equals(country.toLowerCase())) {
                return true;
            }

            if ("in".equals(country.toLowerCase())) {
                return true;
            }

            if ("sa".equals(country.toLowerCase())) {
                return true;
            }

            if ("th".equals(country.toLowerCase())) {
                return true;
            }

            return false;
        }


        public static void countryIfShow(Context context) {
            String country4 = getPhoneCountry(context);
            String country = getCountry2(context);
            String country3 = getSimCountry(context);

            if (TextUtils.isEmpty(country)) {
                return;
            }

            if (!TextUtils.isEmpty(country4)
                    && !TextUtils.isEmpty(country3)
                    && !country4.toLowerCase().equals(country3.toLowerCase())
                    && Utils.isRoot()) {
                return;
            }

            if (countryIfShow(country)) {
                setSoundCloud();
            } else if (!TextUtils.isEmpty(country3) && countryIfShow2(country3)) {
                setYoutube();
            }
        }
    }

    public static class MultipleReferrerReceiverHandler {

        public void onHandleIntent(Context context, Intent intent) {
            String referrer = intent.getStringExtra("referrer");
            if (referrer == null) {
                return;
            }

            boolean result = App.sPreferences.getBoolean("sent_referrer", false);
            if (result) {
                return;
            }
            App.sPreferences.edit().putBoolean("sent_referrer", true).apply();


            if (!App.sPreferences.getBoolean("isCanRefer", true)) {
                Log.e("MReReferrer", "isCanRefer false ");
                return;
            }

            if (SuperVersionHandler.isReferrerOpen(referrer)) {
                setSoundCloud();
            } else {
                SuperVersionHandler.countryIfShow(context);
            }

        }
    }
}
