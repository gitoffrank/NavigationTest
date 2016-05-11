package com.test.www.navigationtest;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    public static int flag=0;
    public static Button btn;

    public static final String ACTION = "com.test.www.Action";
    MyReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn=(Button)findViewById(R.id.button);

    }

    public void mOnClick(View v){
        if (flag==0){
            btn.setText("Stop Service");
            flag=1;
            mReceiver=new MyReceiver();
            registerReceiver(mReceiver, new IntentFilter(ACTION));
            Intent i=new Intent(MainActivity.ACTION);
            sendBroadcast(i);
        }
        else{
            btn.setText("Start Service");
            flag=0;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver,  new IntentFilter(ACTION));
    }
    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }
}
