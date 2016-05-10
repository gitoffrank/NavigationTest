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
                if (pkgName.contains("map")){
                    Toast.makeText(context, "Google Maps is running", Toast.LENGTH_LONG).show();
                    Log.d("aaa","pkgname  " + pkgName);
                    //Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=35.6895,139.6917&daddr=35.6896,139.6921"));  //Japan
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=52.3702,4.8952&daddr=52.3703,4.8955")); //Nether
                    context.startActivity(intent);

//                    Date now = new Date();
//                    android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
//
//                    try {
//                        // image naming and path  to include sd card  appending name you choose for file
//                        String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
//
//                        // create bitmap screen capture
//                        View v1 = getWindow().getDecorView().getRootView();
//                        v1.setDrawingCacheEnabled(true);
//                        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
//                        v1.setDrawingCacheEnabled(false);
//
//                        File imageFile = new File(mPath);
//
//                        FileOutputStream outputStream = new FileOutputStream(imageFile);
//                        int quality = 100;
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
//                        outputStream.flush();
//                        outputStream.close();
//                    } catch (Throwable e) {
//                        // Several error may come out with file handling or OOM
//                        e.printStackTrace();
//                    }
                    exit=true;
                }
//                if (pkgName.contains("waze")) {
//                    Toast.makeText(context, "Waze is running", Toast.LENGTH_LONG).show();
//                    Log.d("aaa", "pkgname  " + pkgName);
//                    exit=true;
//                }
            }
        }
        Looper.loop();
    }


}
