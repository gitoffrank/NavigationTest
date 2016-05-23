package net.rahar.screenshotocr;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Dodobal-2 on 5/23/2016.
 */
public class TopButtonService extends Service {

    private WindowManager windowManager;
    private ImageView chatHead;

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "TopButtonService start", Toast.LENGTH_LONG).show();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.next);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Log.e("HELLO", "currentapiVersion !" + currentapiVersion);
        if (currentapiVersion >= 16)
            chatHead.setImageAlpha(70);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        params.x = -20;
        params.y = 20;
        params.type=WindowManager.LayoutParams.TYPE_TOAST;
        chatHead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                {
                    Log.e("CLICK", "CLICKED ON Button!!");
                    Toast.makeText(getApplicationContext(), "CLICKED ON Button", Toast.LENGTH_SHORT).show();
                    chatHead.setImageResource(R.drawable.next);


                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            chatHead.setImageResource(R.drawable.next);
                        }
                    }, 5000);

//                    Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
//                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    myIntent.putExtra("RETURN", true);
//                    getApplicationContext().startActivity(myIntent);
                }
            }
        });

        windowManager.addView(chatHead, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }
}
