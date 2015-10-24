package com.example.nb.hakaton;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import service.MyService;

public class MainActivity extends AppCompatActivity {

    private boolean isServiceRunning = false;
    private boolean bound = false;
    final String LOG_TAG = "MainActivity";
    private MyService myService;
    private ServiceConnection sConn;
    Intent intent;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this, MyService.class);
        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(LOG_TAG, "connected to service");
                myService = ((MyService.MyBinder) service).getService();
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(LOG_TAG, "disconnected from service");
                bound = false;
            }


        };

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (isServiceRunning && keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Log.d(LOG_TAG,"UP pressed");

            return true;
        }
        return false;
    }

    public void onClickStart(View v) {
        TextView ipAddr = (TextView) findViewById(R.id.editText);
        TextView text = (TextView) findViewById(R.id.textView2);
        text.setText("Service is running");

        Log.d(LOG_TAG, "IP = " + ipAddr.getText());
        ComponentName cn= startService(intent.putExtra("ipAddr",ipAddr.getText()));
        if(cn != null){
            isServiceRunning=true;
        }
    }

    public void onClickStop(View v) {

        boolean stopped = stopService(intent);
        if (stopped) {
            isServiceRunning=false;
        }
    }

}
