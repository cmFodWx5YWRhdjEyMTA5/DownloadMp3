package com.freedownloader.facebook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.freedownloader.util.ReferrerHandler;

/**
 * Created by liyanju on 2018/5/18.
 */

public class InstallStatisticsReferrerReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ReferrerHandler.getInstances().handleIntent(context, intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
