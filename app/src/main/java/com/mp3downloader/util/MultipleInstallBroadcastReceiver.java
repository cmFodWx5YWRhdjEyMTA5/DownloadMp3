package com.mp3downloader.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by liyanju on 2018/5/18.
 */

public class MultipleInstallBroadcastReceiver extends BroadcastReceiver{

    private ReferVersions.MultipleReferrerReceiverHandler mHandler = ReferVersions
            .createInstallReferrerReceiverHandler();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            mHandler.onHandleIntent(context, intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
