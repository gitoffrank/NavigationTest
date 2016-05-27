/*****
 Frank Robben developer.android.com/training/scheduling/wakelock.html
 developer.android.com/training/scheduling/alarms.html
 */

package com.test.www.deviceawake;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
        wakeLock.acquire();

        AlarmManager alarmMgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent inten = new Intent(this, MyIntentService.class);
        PendingIntent pi= PendingIntent.getActivity(this, 0, inten, 0);
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 100, pi);
    }
}
