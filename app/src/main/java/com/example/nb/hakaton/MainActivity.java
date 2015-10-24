package com.example.nb.hakaton;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import MainLogic.Constants;
import MainLogic.SensorObject;
import service.MyService;

public class MainActivity extends AppCompatActivity {


    final String LOG_TAG = "MainActivity";
    private ServiceConnection sConn;
    Intent intent;
    MyService myService;
    boolean bound;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent(this, MyService.class);
        sConn = new ServiceConnection() {

            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(LOG_TAG, "MainActivity onServiceConnected");
                myService = ((MyService.MyBinder) binder).getService();
                bound = true;
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(LOG_TAG, "MainActivity onServiceDisconnected");
                bound = false;
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        bindService(intent, sConn, 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!bound) return;
        unbindService(sConn);
        bound = false;
    }

    public void onClickStart(View v) {
        startService(intent);
    }

    public void onClickStop(View v) {
        stopService(new Intent(this, MyService.class));
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Log.d(LOG_TAG, "Key_Button:");
            myService.setButtonTrue();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)  {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Log.d(LOG_TAG, "Key_Button:");
            myService.setButtonFalse();
            return true;
        }
        return false;
    }


}
