package com.mp3downloader;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.liulishuo.filedownloader.FileDownloader;
import com.lzx.musiclibrary.cache.CacheConfig;
import com.lzx.musiclibrary.cache.CacheUtils;
import com.lzx.musiclibrary.manager.MusicLibrary;
import com.lzx.musiclibrary.notification.NotificationCreater;
import com.mp3downloader.musicgo.MainActivity;
import com.mp3downloader.util.Constants;
import com.mp3downloader.util.FBAdUtils;
import com.mp3downloader.util.ReferVersions;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.List;

import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;

/**
 * Created by liyanju on 2018/5/7.
 */

public class App extends Application {

    public static Context sContext;

    public static SharedPreferences sPreferences;

    public static boolean isSoundCloud() {
        return ReferVersions.SuperVersionHandler.isSoundClound();
    }

    public static boolean isYoutube() {
        return ReferVersions.SuperVersionHandler.isYoutube();
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
        FBAdUtils.loadFBAds(Constants.NATIVE_ID);

        ReferVersions.initSuper();

        if (!App.sPreferences.getBoolean("add_shortcut", false)) {
            addShortcut(this, MainActivity.class, getString(R.string.app_name), R.mipmap.ic_launcher);
            App.sPreferences.edit().putBoolean("add_shortcut", true).apply();
        }

        Fragmentation.builder().handleException(new ExceptionHandler() {
            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        }).install();

        CrashReport.initCrashReport(getApplicationContext());

        initMusicPlayer();
    }

    private void initMusicPlayer() {
        NotificationCreater creater = new NotificationCreater.Builder()
                .setTargetClass("com.mp3downloader.musicgo.MainActivity")
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
