package com.wedownloader.util;

import android.util.Log;

import com.wedownloader.BuildConfig;


/**
 * Created by liyanju on 2017/11/25.
 */

public class LogUtil {

    public static void v(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }
}
