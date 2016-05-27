package com.test.www.deviceawake;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Dodobal-2 on 5/27/2016.
 */
public class MyWakefulReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Intent service=new Intent(context, MyIntentService.class);
        startWakefulService(context, service);
    }
}
