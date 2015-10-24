package com.example.nb.hakaton;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import service.MyService;

public class MainActivity extends AppCompatActivity {

    private boolean isServiceRunning = false;
    final String LOG_TAG = "MainActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        //???
//        if (!isServiceRunning) { Log.d(LOG_TAG,"Not running"); }
//
//        if (isServiceRunning && keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            Log.d(LOG_TAG,"UP pressed");
//
//            return true;
//        }
//        return false;
//    }

//    @Override
//    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        //???
//        if (!isServiceRunning) { Log.d(LOG_TAG,"Not running"); }
//
//        if (isServiceRunning && keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            Log.d(LOG_TAG,"UP released");
//
//            return true;
//        }
//        return false;
//    }

    public void onClickStart(View v) {
        TextView ipAddr = (TextView) findViewById(R.id.editText);
        TextView text = (TextView) findViewById(R.id.textView2);
        text.setText("Service is running");

        Log.d(LOG_TAG, "IP = " + ipAddr);
        ComponentName cn= startService(new Intent(this, MyService.class));
        if(cn != null){
            isServiceRunning=true;
        }
    }

    public void onClickStop(View v) {

        boolean stopped = stopService(new Intent(this, MyService.class));
        if (stopped) {
            isServiceRunning=false;
        }
    }

}
