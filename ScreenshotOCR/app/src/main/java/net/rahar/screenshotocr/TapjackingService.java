package net.rahar.screenshotocr;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by Dodobal-2 on 5/17/2016.
 */
public class TapjackingService extends Service {
    View view;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TapjackingService","aaaaaaaa");
        //Toast.makeText(this, "ssss", Toast.LENGTH_LONG).show();
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.capture);

            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
            //File imageFile = new File(mPath);
            File imageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Pictures/Screenshots/" + System.currentTimeMillis() + ".jpg");
            MediaScannerConnection.scanFile(this,
                    new String[]{imageFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
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
}
