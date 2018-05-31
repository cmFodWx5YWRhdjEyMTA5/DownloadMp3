package com.downloadermp3.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.downloadermp3.Mp3App;
import com.downloadermp3.facebook.FacebookReport;

import java.net.URL;
import java.net.URLConnection;
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
            return true;
        }

        if ("ph".equals(country.toLowerCase())) {
            return true;
        }

//            if ("de".equals(country.toLowerCase())) {
//                return true;
//            }

        if ("ca".equals(country.toLowerCase())) {
            return true;
        }

        if ("au".equals(country.toLowerCase())) {
            return true;
        }

        if ("gb".equals(country.toLowerCase())) {
            return true;
        }

        if ("mx".equals(country.toLowerCase())) {
            return true;
        }

        if ("id".equals(country.toLowerCase())) {
            return true;
        }

        if ("fr".equals(country.toLowerCase())) {
            return true;
        }


        return false;
    }

    public static void checkusTime(final Context context) {
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

                    if (week == 0) {

                    } else if (hour <= 8 || hour >= 20) {

                    }

                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final String format = formatter.format(calendar.getTime());


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
        } else if ("in".equals(country.toLowerCase())) {
            return true;
        } else if ("sa".equals(country.toLowerCase())) {
            return true;
        } else if ("th".equals(country.toLowerCase())) {
            return true;
        }
        return false;
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

        if (countryIfShow(country)) {
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
