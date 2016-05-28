package com.test.www.deviceawake;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dodobal-2 on 5/28/2016.
 */
public class SampleSchedulingService extends IntentService {
    public SampleSchedulingService(){
        super("SchedulingService");
    }

    public static final String TAG="Scheduling Demo";
    public static final int NOTIFICATION_ID=1;
    public static final String SEARCH_STRING = "doodle";

    public static final String URL="http://www.google.com";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    @Override
    protected void onHandleIntent(Intent intent){
        String urlString = URL;
        String result="";

        //Toast.makeText(this, "Working on!", Toast.LENGTH_SHORT).show();
        sendNotification("Working on!");
        Log.i(TAG, "Working on!");

        Handler handler=new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
                         public void run() {
                             //your operation...
                             Toast.makeText(getApplicationContext(), "Working on!", Toast.LENGTH_SHORT).show();
                         }
                     });

        try{
            // Get an instance of the SensorManager
            SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            // Get an instance of the PowerManager
            PowerManager mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

            // Get an instance of the WindowManager
            WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.getDefaultDisplay();

            // Create a bright wake lock
            PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass()
                    .getName());
            mWakeLock.acquire();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("onCreate", e.getMessage());
        }

//        try{
//            result=loadFromNetwork(urlString);
//        }catch (IOException e){
//            Log.i(TAG, "Connection error.");
//        }
//
//        if (result.indexOf(SEARCH_STRING) != -1){
//            sendNotification("This is a Google doodle today!!");
//            Log.i(TAG, "Found doodle!!");
//        }else{
//            sendNotification("No Google doodle today.");
//            Log.i(TAG, "No doodle found. :-(");
//        }

        SampleAlarmReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg){
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Working On")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private String loadFromNetwork(String urlString) throws IOException{
        InputStream stream = null;
        String str ="";

        try {
            stream = downloadUrl(urlString);
            str = readIt(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    private InputStream downloadUrl(String urlString) throws IOException {

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Start the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    private String readIt(InputStream stream) throws IOException {

        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        for(String line = reader.readLine(); line != null; line = reader.readLine())
            builder.append(line);
        reader.close();
        return builder.toString();
    }
}
