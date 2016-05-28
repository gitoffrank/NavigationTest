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
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    SampleAlarmReceiver alarm = new SampleAlarmReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void Start(View v){
        alarm.setAlarm(this);
    }

    public void Stop(View v){
        alarm.cancelAlarm(this);
    }
}
