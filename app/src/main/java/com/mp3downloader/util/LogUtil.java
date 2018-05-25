package com.mp3downloader.util;

import android.util.Log;

import com.mp3downloader.BuildConfig;

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
