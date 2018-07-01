package com.downloadermp3.util;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.downloadermp3.Mp3App;
import com.downloadermp3.facebook.FacebookReport;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by liyanju on 2018/5/31.
 */

public class RangeHandler {

    private volatile boolean isSoundClound = false;

    private volatile boolean isYoutube = false;

    public void setSoundCloud() {
        isSoundClound = true;
        Mp3App.sPreferences.edit().putBoolean(Constants.KEY_SOUNDCLOUD, true).apply();
    }

    public void setYoutube() {
        isYoutube = true;
        Mp3App.sPreferences.edit().putBoolean(Constants.KEY_YOUTUBE, true).apply();
    }

    public String getPhoneCountry(Context context) {
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

    public String getCountry2(Context context) {
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

    public String getSimCountry(Context context) {
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

    public void initSpecial() {
        isSoundClound = Mp3App.sPreferences.getBoolean(Constants.KEY_SOUNDCLOUD, false);
        isYoutube = Mp3App.sPreferences.getBoolean(Constants.KEY_YOUTUBE, false);
    }

    public boolean isSClound() {
        return isSoundClound;
    }

    public boolean isYTB() {
        return isYoutube;
    }


    private boolean countryIfShow2(String country) {

        if ("it".equals(country.toLowerCase())) {
            checkusTime(Mp3App.sContext);
            return false;
        }

        if ("ph".equals(country.toLowerCase())) {
            return true;
        }

        if ("pe".equals(country.toLowerCase())) {
            return true;
        }

        if ("ar".equals(country.toLowerCase())) {
            return true;
        }

        if ("ca".equals(country.toLowerCase())) {
            checkusTime(Mp3App.sContext);
            return false;
        }

        if ("au".equals(country.toLowerCase())) {
            checkusTime(Mp3App.sContext);
            return false;
        }

        if ("gb".equals(country.toLowerCase())) {
            checkusTime(Mp3App.sContext);
            return false;
        }

        if ("mx".equals(country.toLowerCase())) {
            return true;
        }

        if ("id".equals(country.toLowerCase())) {
            return true;
        }

        if ("us".equals(country.toLowerCase())) {
            checkusTime(Mp3App.sContext);
            return false;
        }

        if ("jp".equals(country.toLowerCase())) {
            checkusTime(Mp3App.sContext);
            return false;
        }

        if ("ec".equals(country.toLowerCase())) {
            return true;
        }

        if ("nz".equals(country.toLowerCase())) {
            return true;
        }

        if ("co".equals(country.toLowerCase())) {
            return true;
        }

        if ("bg".equals(country.toLowerCase())) {
            return true;
        }

        if ("my".equals(country.toLowerCase())) {
            return true;
        }

        if ("bo".equals(country.toLowerCase())) {
            return true;
        }

        if ("dz".equals(country.toLowerCase())) {
            return true;
        }

        if ("ma".equals(country.toLowerCase())) {
            return true;
        }

        if ("lk".equals(country.toLowerCase())) {
            return true;
        }

        if ("cy".equals(country.toLowerCase())) {
            return true;
        }

        if ("ro".equals(country.toLowerCase())) {
            return true;
        }

        if ("ee".equals(country.toLowerCase())) {
            return true;
        }

        if ("ke".equals(country.toLowerCase())) {
            return true;
        }

        if ("za".equals(country.toLowerCase())) {
            return true;
        }

        if ("pt".equals(country.toLowerCase())) {
            return true;
        }

        if ("cl".equals(country.toLowerCase())) {
            return true;
        }

        if ("lv".equals(country.toLowerCase())) {
            return true;
        }

//        if ("in".equals(country.toLowerCase())) {
//            checkusTime(Mp3App.sContext);
//            return false;
//        }

        if ("fr".equals(country.toLowerCase())) {
            checkusTime(Mp3App.sContext);
            return false;
        }

        if ("la".equals(country.toLowerCase())) {
            return true;
        }

        return false;
    }

    public void checkusTime(final Context context) {
        Utils.runSingleThread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://www.google.com");
                    URLConnection uc = url.openConnection();
                    uc.setConnectTimeout(10 * 1000);
                    uc.setReadTimeout(10 * 1000);
                    uc.connect();

                    long ld = uc.getDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(ld);
                    int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;//0代表周日，6代表周六
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);

                    boolean isOpen = false;
                    boolean isWeek = false;
                    if (week == 0 || week == 6) {
                        setSoundCloud();
                        isOpen = true;
                        isWeek = true;
                    } else if (hour <= 8 || hour >= 20) {
                        setSoundCloud();
                        isOpen = true;
                        isWeek = true;
                    }

                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final String format = formatter.format(calendar.getTime());

                    if (isOpen) {
                        FacebookReport.logSentUSOpen(isWeek, format);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static boolean countryIfShow(String country) {
        if ("id".equals(country.toLowerCase())) {
            return true;
        } else if ("br".equals(country.toLowerCase())) {
            return true;
        } else if ("sa".equals(country.toLowerCase())) {
            return true;
        } else if ("th".equals(country.toLowerCase())) {
            return true;
        } else if ("ph".equals(country.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static boolean isFacebookOpen(String referrer) {
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


    public void countryIfShow(Context context) {
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

        if (!TextUtils.isEmpty(country3) && countryIfShow(country3)) {
            setYoutube();
            FacebookReport.logSentOpenSuper("country open ytb ");
            return;
        }

        if (!TextUtils.isEmpty(country3) && countryIfShow2(country3)) {
            setSoundCloud();
            FacebookReport.logSentOpenSuper("scoud country open ");
            return;
        }
    }
}
