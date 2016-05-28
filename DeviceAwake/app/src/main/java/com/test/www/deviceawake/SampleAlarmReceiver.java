package com.test.www.deviceawake;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

/**
 * Created by Dodobal-2 on 5/28/2016.
 */
public class SampleAlarmReceiver extends WakefulBroadcastReceiver {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private PowerManager pm1;
    private PowerManager.WakeLock wakeLock;
    @Override
    public void onReceive(Context context, Intent intent){
        Intent service = new Intent(context, SampleSchedulingService.class);
        startWakefulService(context, service);
    }

    public void setAlarm(Context context){

//        PowerManager pm1 = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        wakeLock = pm1.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "TEST");
//        if ((wakeLock!=null) && (wakeLock.isHeld() == false)){
//            wakeLock.acquire();
//        }

        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SampleAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context,0,intent,0);

//        Calendar calendar=Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//
//        calendar.set(Calendar.HOUR_OF_DAY, 8);
//        calendar.set(Calendar.MINUTE, 30);
//
//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+5*1000, 5*1000, alarmIntent);

        ComponentName receiver = new ComponentName (context, SampleBootReceiver.class);
        PackageManager pm=context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context){
        if (alarmMgr != null){
            alarmMgr.cancel(alarmIntent);
        }

        ComponentName receiver = new ComponentName(context, SampleBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
