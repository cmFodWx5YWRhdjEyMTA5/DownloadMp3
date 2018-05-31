package com.downloadermp3.facebook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.downloadermp3.util.ReferrerHandler;

/**
 * Created by liyanju on 2018/5/18.
 */

public class FacebookReferrerBroadcastReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ReferrerHandler.getInstances().handleIntent(context, intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
