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


        new ListenActivity(context).start();
    }
}

