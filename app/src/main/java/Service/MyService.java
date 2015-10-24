package Service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;

import MainLogic.SensorObject;

/**
 * Created by NB on 24.10.2015.
 */
public class MyService extends Service implements SensorEventListener {

    private final int type_accelerometer = 0;
    private final int type_gyroscope = 1;
    private final int type_orientation = 2;

    private Timer timer;
    private float time;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer, sensorGyroscope, sensorOrientation;
    private SensorObject sensorObject;

    final float alpha = (float)0.8; // Accelerometer constant
    private float gravity[] = new float[3];


    final String LOG_TAG = "MyService";

    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        gravity[0] = 0;
        gravity[1] = 0;
        gravity[2] = 0;
        time = 0;
        timer = new Timer();

        Log.d(LOG_TAG, " :onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, " :onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, " :onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, " :onBind");
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            sensorObject = new SensorObject(type_accelerometer,event.values[0] - gravity[0],
                    event.values[1] - gravity[1],
                    event.values[2] - gravity[2],
                    time);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
