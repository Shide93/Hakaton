package com.example.nb.hakaton.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class MainService extends Service {
    final String LOG_TAG = "myLogs";
    private Thread thread;
    public MainService() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //TODO: вызов функций сбора и отправки телеметрии
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void someTask() {
       thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 5; i++) {
                    Log.d(LOG_TAG, "i = " + i);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

    }
}
