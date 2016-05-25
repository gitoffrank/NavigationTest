package net.rahar.screenshotocr;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

/**
 * Created by Dodobal-2 on 5/17/2016.
 */
public class MyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Received an intent.", Toast.LENGTH_SHORT).show();
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();
        wl.release();

        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock=keyguardManager.newKeyguardLock(Activity.KEYGUARD_SERVICE);
        lock.disableKeyguard();
        new ListenActivity(context).start();
    }
}

