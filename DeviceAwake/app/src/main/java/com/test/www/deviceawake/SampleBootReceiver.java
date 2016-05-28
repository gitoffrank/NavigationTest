package com.test.www.deviceawake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Dodobal-2 on 5/28/2016.
 */
public class SampleBootReceiver extends BroadcastReceiver {

    SampleAlarmReceiver alarm = new SampleAlarmReceiver();

    @Override
    public void onReceive(Context context, Intent intent){
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            alarm.setAlarm(context);
        }
    }
}
