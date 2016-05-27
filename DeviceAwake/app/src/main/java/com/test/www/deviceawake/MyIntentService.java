package com.test.www.deviceawake;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by Dodobal-2 on 5/27/2016.
 */
public class MyIntentService extends IntentService {
    public static final int NOTIFICATION_ID=1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public MyIntentService(){
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Bundle extras=intent.getExtras();
        Toast.makeText(this, "WakeFul", Toast.LENGTH_LONG).show();
        MyWakefulReceiver.completeWakefulIntent(intent);
    }
}
