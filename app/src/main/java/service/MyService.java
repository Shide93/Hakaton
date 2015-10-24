package service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
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

    private String ipAddr;
    private Timer timer;
    private long time;
    private MyBinder binder = new MyBinder();

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer, sensorGyroscope, sensorOrientation;
    private SensorObject sensorObject;

    final float alpha = (float)0.8; // Accelerometer constant
    private float gravity[] = new float[3];


    // Create a constant to convert nanoseconds to miliseconds.
    private static final float NanoSecondsToMiliSeconds = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float EPSILON = 617;

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


        Log.d(LOG_TAG, " :onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, " :onStartCommand");
        ipAddr = intent.getStringExtra("ipAddr");
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, ipAddr);
        Log.d(LOG_TAG, " :onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, " :onBind");
        return binder;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        time = event.timestamp;
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
            // Remove the gravity contribution with the high-pass filter.
            sensorObject = new SensorObject(type_accelerometer,event.values[0] - gravity[0],
                    event.values[1] - gravity[1],
                    event.values[2] - gravity[2],
                    (event.timestamp - time)*NanoSecondsToMiliSeconds );
            Log.d(LOG_TAG,"Accelerometer:" + sensorObject.getX() +" " + sensorObject.getY() + " " + sensorObject.getZ() + " " +
                    sensorObject.getTime());

        }
        time = event.timestamp;
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (time != 0) {
                final float dT = (event.timestamp - time) * NanoSecondsToMiliSeconds;
                // Axis of the rotation sample, not normalized yet.
                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];

                // Calculate the angular speed of the sample
                float omegaMagnitude = (float)Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                // (that is, EPSILON should represent your maximum allowable margin of error)
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
                float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
                deltaRotationVector[0] = sinThetaOverTwo * axisX;
                deltaRotationVector[1] = sinThetaOverTwo * axisY;
                deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                deltaRotationVector[3] = cosThetaOverTwo;

                sensorObject = new SensorObject(type_gyroscope,
                        deltaRotationVector[0],
                        deltaRotationVector[1],
                        deltaRotationVector[2],
                        dT);

                Log.d(LOG_TAG,"Gyroscope:" + sensorObject.getX() +" " + sensorObject.getY() + " " + sensorObject.getZ() + " " +
                        sensorObject.getTime());
            }
            time = event.timestamp;
            float[] deltaRotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            // User code should concatenate the delta rotation we computed with the current rotation
            // in order to get the updated rotation.
            // rotationCurrent = rotationCurrent * deltaRotationMatrix;
        }

        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float azimuth_angle = event.values[0];
            float pitch_angle = event.values[1];
            float roll_angle = event.values[2];

            sensorObject = new SensorObject(type_orientation,azimuth_angle,
                    pitch_angle,
                    roll_angle,
                    (event.timestamp - time)*NanoSecondsToMiliSeconds );
            Log.d(LOG_TAG,"Orientation:" + sensorObject.getX() +" " + sensorObject.getY() + " " + sensorObject.getZ() + " " +
                    sensorObject.getTime());

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class MyBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

}
