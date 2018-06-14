package com.downloadermp3;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.downloadermp3.facebook.FBAdUtils;
import com.downloadermp3.util.ReferrerHandler;
import com.liulishuo.filedownloader.FileDownloader;
import com.lzx.musiclibrary.cache.CacheConfig;
import com.lzx.musiclibrary.cache.CacheUtils;
import com.lzx.musiclibrary.manager.MusicLibrary;
import com.lzx.musiclibrary.notification.NotificationCreater;
import com.downloadermp3.ui.WelcomeActivity;
import com.downloadermp3.util.Constants;
import com.downloadermp3.facebook.FacebookReport;
import com.rating.RatingActivity;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.List;

import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;

/**
 * Created by liyanju on 2018/5/7.
 */

public class Mp3App extends Application {

    public static Context sContext;

    public static SharedPreferences sPreferences;

    public static boolean isSCloud() {
        return ReferrerHandler.sRangeHandler.isSClound();
    }

    public static boolean isYTB() {
        return ReferrerHandler.sRangeHandler.isYTB();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private int iii;

    private void init() {
        iii = iii + 1;
        iii++;
        if (iii > 2) {
            iii = iii++;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = this;

        FileDownloader.setup(sContext);

        init();

        sPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final String packageName = getPackageName();
        if (!TextUtils.isEmpty(packageName) && !packageName.equals(getCurrentProcessName())) {
            return;
        }

        FBAdUtils.init(this);
        FBAdUtils.loadFBAds(Constants.NATIVE_ID_LIST);

        ReferrerHandler.initReferrer();

        if (!Mp3App.sPreferences.getBoolean("shortcut", false)) {
            addShortcut(this, WelcomeActivity.class, getString(R.string.app_name), R.mipmap.ic_launcher);
            Mp3App.sPreferences.edit().putBoolean("shortcut", true).apply();
        }

        Fragmentation.builder().handleException(new ExceptionHandler() {
            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        }).install();

        initMusicPlayer();

        init();

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
