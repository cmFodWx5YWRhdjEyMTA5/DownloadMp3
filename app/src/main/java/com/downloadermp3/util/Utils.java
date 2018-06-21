package com.downloadermp3.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.downloadermp3.Mp3App;
import com.downloadermp3.R;
import com.downloadermp3.ui.MainActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Intent.FLAG_GRANT_PREFIX_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

/**
 * Created by liyanju on 2018/5/7.
 */

public class Utils {

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static final ExecutorService sExecutorService = Executors.newSingleThreadExecutor();
    public static final ExecutorService sExecutorService2 = Executors.newSingleThreadExecutor();
    public static final ExecutorService sExecutorService3 = Executors.newSingleThreadExecutor();

    public static RequestOptions requestOptions = new RequestOptions()
            .placeholder(R.drawable.default_thumbnail_corners)
            .transforms(new RoundedCorners(Utils.dip2px(Mp3App.sContext, 4)));

    public static void setViewBackgroud(View view) {
        if (Mp3App.isYTB() && MainActivity.getSearchType() == MainActivity.YOUTUBE_TYPE) {
            view.setBackgroundColor(ContextCompat.getColor(Mp3App.sContext, R.color.colorPrimary));
        } else if (Mp3App.isYTB() && MainActivity.getSearchType() == MainActivity.SOUNDClOUND_TYPE) {
            view.setBackgroundColor(ContextCompat.getColor(Mp3App.sContext, R.color.sdcound_primary));
        } else if (Mp3App.isSCloud()) {
            view.setBackgroundColor(ContextCompat.getColor(Mp3App.sContext, R.color.sdcound_primary));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(Mp3App.sContext, R.color.colorPrimary2));
        }
    }

    public static void setActivityStatusColor(Activity activity) {
        if (Mp3App.isYTB() && MainActivity.getSearchType() == MainActivity.YOUTUBE_TYPE) {
            setStatusColor(activity, ContextCompat.getColor(Mp3App.sContext, R.color.colorPrimary));
        } else if (Mp3App.isYTB() && MainActivity.getSearchType() == MainActivity.SOUNDClOUND_TYPE) {
            setStatusColor(activity, ContextCompat.getColor(Mp3App.sContext, R.color.sdcound_primary));
        } else if (Mp3App.isSCloud()) {
            setStatusColor(activity, ContextCompat.getColor(Mp3App.sContext, R.color.sdcound_primary));
        } else {
            setStatusColor(activity, ContextCompat.getColor(Mp3App.sContext, R.color.colorPrimary2));
        }
    }

    public static void runSingleThread(Runnable runnable) {
        sExecutorService2.execute(runnable);
    }

    public static int getScreenWhith() {
        WindowManager wm = (WindowManager) Mp3App.sContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int durationChange(String time) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            int time2 = simpleDateFormat.parse(time).getMinutes();
            return time2 * 60 + simpleDateFormat.parse(time).getMinutes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static DisplayMetrics getMetrics(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics;
    }

    public static boolean checkAndRequestPermissions(Activity activity) {
        try {
            ArrayList<String> permissionList = new ArrayList<>();
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return checkAndRequestPermissions(activity, permissionList);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean checkAndStoreRequestPermissions(Activity activity) {
        try {
            ArrayList<String> permissionList = new ArrayList<>();
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return checkAndRequestPermissions(activity, permissionList);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isScreenOn() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= 20) {
                // I'm counting
                // STATE_DOZE, STATE_OFF, STATE_DOZE_SUSPENDED
                // all as "OFF"
                DisplayManager dm = (DisplayManager) Mp3App.sContext.getSystemService(Context.DISPLAY_SERVICE);
                Display[] displays = dm.getDisplays();
                for (Display display : displays) {
                    if (display.getState() == Display.STATE_ON
                            || display.getState() == Display.STATE_UNKNOWN) {
                        return true;
                    }
                }
                return false;
            }

            // If you use less than API20:
            PowerManager powerManager = (PowerManager) Mp3App.sContext.getSystemService(Context.POWER_SERVICE);
            if (powerManager.isScreenOn()) {
                return true;
            }
            return false;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    private static boolean checkAndRequestPermissions(Activity activity, ArrayList<String> permissionList) {
        ArrayList<String> list = new ArrayList<>(permissionList);
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String permission = it.next();
            //检查权限是否已经申请
            int hasPermission = ContextCompat.checkSelfPermission(activity, permission);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                it.remove();
            }
        }

        if (list.size() == 0) {
            return true;
        }
        String[] permissions = list.toArray(new String[0]);
        //正式请求权限
        ActivityCompat.requestPermissions(activity, permissions, 101);
        return false;
    }

    public static void transparence(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setStatusColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    public static void showLongToastSafe(final @StringRes int resId) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Mp3App.sContext, resId, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void runUIThread(Runnable runnable) {
        sHandler.post(runnable);
    }

    public static void showLongToastSafe(final String string) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Mp3App.sContext, string, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static boolean isRoot(){
        boolean bool = false;

        try{
            if ((!new File("/system/bin/su").exists())
                    && (!new File("/system/xbin/su").exists())){
                bool = false;
            } else {
                bool = true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bool;
    }

    public static void playMusic(Context context, String path) {
        try {
            Uri uri;
            if (!path.startsWith("http")) {
                String ourPackage = context.getApplicationContext().getPackageName();
                uri = FileProvider.getUriForFile(context, ourPackage + ".provider", new File(path));
            } else {
                uri = Uri.parse(path);
            }
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "audio/mp3");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(FLAG_GRANT_PREFIX_URI_PERMISSION);
            }
            Mp3App.sContext.startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void gotoGP(Activity context) {
        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (Throwable anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
