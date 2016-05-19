package net.rahar.screenshotocr;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ScreenOCR extends Activity {
    String TAG = "ScreenOCRMainActivity";
    String LANG = "eng";
    public static String DATA_DIR_PATH;
    String TESS_DATA_DIR;

    public static int flag=0;
    public static Button btn;
    public static ImageView image;
    public static final String ACTION = "com.test.www.Action";
    MyReceiver mReceiver;

    /**
     * Tesseract OCR works with a training data file that must be located at the file system. I'm shipping the training data
     * file in the assets of the apk. This function copies that assets file to the sdcard.
     */
    private void checkTrainData() {

        File pix = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        final  File screenshotsDir = new File(pix, "Screenshots");
        Toast.makeText(this, screenshotsDir.getPath(), Toast.LENGTH_LONG).show();
        if (!(new File(DATA_DIR_PATH + File.separator + "tessdata" + File.separator + LANG + ".traineddata")).exists()) {
            Toast.makeText(this, "sss", Toast.LENGTH_LONG).show();
            ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while I'm copying OCR training files to SD card. You need 20 mbs of free space.");
            progress.show();
            try {
                File f = new File(TESS_DATA_DIR);
                f.mkdir();
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata" + File.separator + LANG + ".traineddata");
                OutputStream out = new FileOutputStream(TESS_DATA_DIR + File.separator + LANG + ".traineddata");
                byte[] buf = new byte[8024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                android.util.Log.e(TAG, "Was unable to copy " + LANG + " traineddata " + e.toString());
            }
            progress.dismiss();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_ocr);

        btn=(Button)findViewById(R.id.button);

        DATA_DIR_PATH = getApplicationContext().getExternalFilesDir(null).getAbsolutePath();
        TESS_DATA_DIR = DATA_DIR_PATH + File.separator + "tessdata";

        // First checking if the training file is in place
        checkTrainData();
        // Starting the monitor service. This will monitor screenshots folder and run ocr over new images
        startService(new Intent(getApplicationContext(), BackgroundOCRService.class));

        // Set the switch value. This switch allows to turn on and off the automatic ocr.
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ScreenOCR.this);
        Switch toggle = (Switch) findViewById(R.id.switch1);
        toggle.setChecked(sharedPref.getBoolean("ISON", true));

        // Set the last recognized text into the text field,
        ((TextView) findViewById(R.id.textView_last_recognized)).setText(
                sharedPref.getString("LASTTEXT", "N/A")
        );

        // Automatic OCR on and off switch handler
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("ISON", isChecked);
                editor.commit();
            }
        });

        // Copy to clipboard button functionality
        ((Button) findViewById(R.id.buttonCopy)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", sharedPref.getString("LASTTEXT", "N/A"));
                clipboard.setPrimaryClip(clip);
            }
        });
    }


    public void mOnClick(View v){
        if (flag==0){
            btn.setText("Stop Service");
            flag=1;
            mReceiver=new MyReceiver();
            registerReceiver(mReceiver, new IntentFilter(ACTION));
            Intent i=new Intent(ScreenOCR.ACTION);
            sendBroadcast(i);
        }
        else{
            btn.setText("Start Service");
            flag=0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,  new IntentFilter(ACTION));
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ScreenOCR.this);
        ((TextView) findViewById(R.id.textView_last_recognized)).setText(
                sharedPref.getString("LASTTEXT", "N/A")
        );
    }

    @Override
    protected void onPause() {
    //    unregisterReceiver(mReceiver);
        super.onPause();
    }
}
