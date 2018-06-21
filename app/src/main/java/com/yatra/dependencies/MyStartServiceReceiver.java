package com.yatra.dependencies;

/**
 * Created by Prasad on 6/18/2018.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyStartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkUtility.scheduleJob(context);
    }
}