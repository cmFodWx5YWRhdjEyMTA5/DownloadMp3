package com.freedownloader;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.freedownloader.facebook.FBAdUtils;
import com.freedownloader.util.ReferrerHandler;
import com.liulishuo.filedownloader.FileDownloader;
import com.lzx.musiclibrary.cache.CacheConfig;
import com.lzx.musiclibrary.cache.CacheUtils;
import com.lzx.musiclibrary.manager.MusicLibrary;
import com.lzx.musiclibrary.notification.NotificationCreater;
import com.freedownloader.ui.SplashActivity;
import com.freedownloader.util.Constants;
import com.freedownloader.facebook.FacebookReport;
import com.rating.RatingActivity;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.List;

import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;

/**
 * Created by liyanju on 2018/5/7.
 */

public class MusicApp extends Application {

    public static Context sContext;

    public static SharedPreferences sPreferences;

    public static boolean isSCloud() {
        return ReferrerHandler.sRangeHandler.isSClound();
    }

    public static boolean isSingYTB() {
        return ReferrerHandler.sRangeHandler.isSingYTB();
    }

    public static boolean isYTB() {
        return ReferrerHandler.sRangeHandler.isYTB();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = this;

        FileDownloader.setup(sContext);

        sPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final String packageName = getPackageName();
        if (!TextUtils.isEmpty(packageName) && !packageName.equals(getCurrentProcessName())) {
            return;
        }

        FBAdUtils.init(this);
        FBAdUtils.loadFBAds(Constants.NATIVE_ID_LIST);

        ReferrerHandler.initReferrer();

        if (!MusicApp.sPreferences.getBoolean("short_2cut", false)) {
            addShortcut(this, SplashActivity.class, getString(R.string.app_name), R.mipmap.ic_launcher);
            MusicApp.sPreferences.edit().putBoolean("short_2cut", true).apply();
        }

        Fragmentation.builder().handleException(new ExceptionHandler() {
            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        }).install();

        initMusicPlayer();

        CrashReport.initCrashReport(getApplicationContext());

        RatingActivity.setRatingClickListener(new RatingActivity.RatingClickListener() {
            @Override
            public void onClickFiveStart() {
                FacebookReport.logSentRating("five_start");
            }

            @Override
            public void onClickReject() {
                FacebookReport.logSentRating("no_rating");
            }
        });
        RatingActivity.setPopTotalCount(this, 2);
    }

    private void initMusicPlayer() {
        NotificationCreater creater = new NotificationCreater.Builder()
                .setTargetClass("MainActivity")
                .setCreateSystemNotification(true)
                .build();
        File file = getCacheDir();
        if (!file.canRead() || !file.canWrite()) {
            file = CacheUtils.getDefaultSongCacheDir();
        }
        CacheConfig cacheConfig = new CacheConfig.Builder()
                .setOpenCacheWhenPlaying(true)
                .setCachePath(file.getPath())
                .build();
        MusicLibrary musicLibrary = new MusicLibrary.Builder(this)
                .setNotificationCreater(creater)
                .setCacheConfig(cacheConfig)
                .build();
        musicLibrary.init();
    }

    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> appProcessInfos = am.getRunningAppProcesses();

        if (appProcessInfos != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcessInfos) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return "";
    }

    public static void addShortcut(Context context, Class clazz, String appName, int ic_launcher) {
        // 安装的Intent
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.putExtra("tName", appName);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
        shortcutIntent.setClassName(context, clazz.getName());
        //        shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // 快捷名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getResources().getString(R.string.app_name));
        // 快捷图标是否允许重复
        shortcut.putExtra("duplicate", false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        // 快捷图标
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, ic_launcher);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        // 发送广播
        context.sendBroadcast(shortcut);
    }
}
