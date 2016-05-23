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
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ScreenOCR extends Activity {
    //String TAG1 = "ScreenOCRMainActivity";
    String LANG = "eng";
    public static String DATA_DIR_PATH;
    String TESS_DATA_DIR;

    public static TextView textView;
    public static int flag=0;
    public static Button btn;
    public static ImageView image;
    public static final String ACTION = "com.test.www.Action";
    MyReceiver mReceiver;


    private static final String TAG = ScreenOCR.class.getName();
    private static final int REQUEST_CODE = 100;
    private static String STORE_DIRECTORY;
    private static int IMAGES_PRODUCED;
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static MediaProjection sMediaProjection;

    private MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private OrientationChangeCallback mOrientationChangeCallback;

    public File storeDirectory;


    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            final Handler handler= new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Image image = null;
                    FileOutputStream fos = null;
                    Bitmap bitmap = null;
                    try {

                        image = mImageReader.acquireLatestImage();
                        if (image != null) {
                            Image.Plane[] planes = image.getPlanes();
                            ByteBuffer buffer = planes[0].getBuffer();
                            int pixelStride = planes[0].getPixelStride();
                            int rowStride = planes[0].getRowStride();
                            int rowPadding = rowStride - pixelStride * mWidth;

                            // create bitmap
                            bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                            bitmap.copyPixelsFromBuffer(buffer);

                            // write bitmap to a file
                            File file= new File(storeDirectory, "myscreen_" + IMAGES_PRODUCED + ".png");
                            fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                            IMAGES_PRODUCED++;
                            Log.e(TAG, "captured image: " + IMAGES_PRODUCED);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (fos!=null) {
                            try {
                                fos.close();
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            }
                        }

                        if (bitmap!=null) {
                            bitmap.recycle();
                        }

                        if (image!=null) {
                            image.close();
                        }
                    }
                }
            }, 1500);

        }
    }

    private class OrientationChangeCallback extends OrientationEventListener {
        public OrientationChangeCallback(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            synchronized (this) {
                final int rotation = mDisplay.getRotation();
                if (rotation != mRotation) {
                    mRotation = rotation;
                    try {
                        // clean up
                        if(mVirtualDisplay != null) mVirtualDisplay.release();
                        if(mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);

                        // re-create virtual display depending on device width / height
                        createVirtualDisplay();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e("ScreenCapture", "stopping projection.");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mVirtualDisplay != null) mVirtualDisplay.release();
                    if(mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
                    if(mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                    sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }


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

        textView=(TextView)findViewById(R.id.textView2);
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

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

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        }.start();
    }


    public void mOnClick(View v){
        if (flag==0){
            startProjection();
            btn.setText("Stop Service");
            flag=1;
            mReceiver=new MyReceiver();
            registerReceiver(mReceiver, new IntentFilter(ACTION));
            Intent i=new Intent(ScreenOCR.ACTION);
            sendBroadcast(i);
        }
        else{
            stopProjection();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {

            sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);

            if (sMediaProjection != null) {

                File externalFilesDir = getExternalFilesDir(null);
                if (externalFilesDir != null) {

                    STORE_DIRECTORY = Environment.getExternalStorageDirectory() + "/Pictures/Screenshots";
                    Toast.makeText(this, STORE_DIRECTORY, Toast.LENGTH_LONG).show();
                    storeDirectory = new File(STORE_DIRECTORY);
                    if (!storeDirectory.exists()) {

                        boolean success = storeDirectory.mkdirs();
                        if (!success) {
                            Log.e(TAG, "failed to create file storage directory.");
                            return;
                        }
                    }
                } else {
                    Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
                    return;
                }

                // display metrics
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                mDensity = metrics.densityDpi;
                mDisplay = getWindowManager().getDefaultDisplay();

                // create virtual display depending on device width / height
                createVirtualDisplay();

                // register orientation change callback
                mOrientationChangeCallback = new OrientationChangeCallback(this);
                if (mOrientationChangeCallback.canDetectOrientation()) {
                    mOrientationChangeCallback.enable();
                }

                // register media projection stop callback
                sMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
            }
        }
    }

    /****************************************** UI Widget Callbacks *******************************/
    private void startProjection() {
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void stopProjection() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (sMediaProjection != null) {
                    sMediaProjection.stop();
                }
            }
        });
    }

    /****************************************** Factoring Virtual Display creation ****************/
    private void createVirtualDisplay() {
        // get width and height
        Point size = new Point();
        mDisplay.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }

}
