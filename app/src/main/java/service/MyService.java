package service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.app.Service;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import java.util.Timer;

import MainLogic.Constants;
import MainLogic.SensorObject;
import MainLogic.SensorObjectSerializer;

/**
 * Created by NB on 24.10.2015.
 */
public class MyService extends Service implements SensorEventListener {

    private final int type_accelerometer = 0;
    private final int type_gyroscope = 1;
    private final int type_orientation = 2;
    private final int type_Key = 3;

    private long lasttime;// Last of sensor

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer, sensorGyroscope, sensorOrientation;
    private SensorObject sensorObject, keyObject;

    final float alpha = (float)0.8; // Accelerometer constant
    private float gravity[] = new float[3];

    private boolean connect = false;
    private boolean button_pressed = false;

    private ClientTCP clientTCP;
    MyBinder binder = new MyBinder();

    // Create a constant to convert nanoseconds to miliseconds.
    private static final float NanoSecondsToMiliSeconds = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float EPSILON = 617;

    final String LOG_TAG = "MyService";

    public void onCreate() {
        super.onCreate();
        try {

        } catch (Exception e) {
            Log.e(LOG_TAG, "C: Error", e);
        }
        // connect to the server
        new connectTask().execute("");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorOrientation, SensorManager.SENSOR_DELAY_NORMAL);
        gravity[0] = 0;
        gravity[1] = 0;
        gravity[2] = 0;
        Log.d(LOG_TAG, " :onCreate");
    }


    public class connectTask extends AsyncTask<String,String,ClientTCP> {

        @Override
        protected ClientTCP doInBackground(String... message) {

            //we create a TCPClient object and
            clientTCP = new ClientTCP(new ClientTCP.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            }, "192.168.137.48");
            clientTCP.run();
            return null;
        }

        @Override
        protected void onPostExecute(ClientTCP clientTCP) {
            super.onPostExecute(clientTCP);
            connect = true;
            if(connect)
                Log.d("MEGA_LOG_TAG", "true");

        }
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, " :onStartCommand");


        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        clientTCP.stopClient();
        Log.d(LOG_TAG, " :onDestroy");
    }


    public IBinder onBind(Intent arg0) {
        Log.d(LOG_TAG, "MyService onBind");
        return binder;
    }

    public class MyBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    public void setButtonTrue() {
        button_pressed = true;
    }

    public void setButtonFalse() {
        button_pressed = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(connect) {

            if(button_pressed){
                keyObject = new SensorObject(type_Key,0,0,0,0);
                clientTCP.sendMessage((new SensorObjectSerializer().Serialize(keyObject)));
                Log.d(LOG_TAG, "KEYBUTTON: " + keyObject.getX() + " " + keyObject.getY() + " " + keyObject.getZ() + " " +
                        keyObject.getTime());
            }
            Log.d(LOG_TAG, "Last time:" + (lasttime));
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
                // Remove the gravity contribution with the high-pass filter.


                sensorObject = new SensorObject(type_accelerometer,event.values[0] - gravity[0],
                        event.values[1] - gravity[1],
                        event.values[1] - gravity[1],
                        ((int) ((event.timestamp - lasttime) / 1000)));
                 clientTCP.sendMessage((new SensorObjectSerializer().Serialize(sensorObject)));
                Log.d(LOG_TAG, "Accelerometer:" + sensorObject.getX() + " " + sensorObject.getY() + " " + sensorObject.getZ() + " " +
                        sensorObject.getTime());
                Log.d(LOG_TAG, "Serializabe: " + bytesToHex(new SensorObjectSerializer().Serialize(sensorObject)));

            }
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                //if (time != 0) {
                final float dT = (event.timestamp - lasttime) * NanoSecondsToMiliSeconds;
                // Axis of the rotation sample, not normalized yet.
                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];

                // Calculate the angular speed of the sample
                float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

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
                float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
                float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
                deltaRotationVector[0] = sinThetaOverTwo * axisX;
                deltaRotationVector[1] = sinThetaOverTwo * axisY;
                deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                deltaRotationVector[3] = cosThetaOverTwo;

                sensorObject = new SensorObject(type_gyroscope, event.values[0] - gravity[0],
                        event.values[1] - gravity[1],
                        event.values[2] - gravity[2],
                        (int) ((event.timestamp - lasttime) / 1000));

                clientTCP.sendMessage((new SensorObjectSerializer().Serialize(sensorObject)));

                Log.d(LOG_TAG, "Gyroscope:" + sensorObject.getX() + " " + sensorObject.getY() + " " + sensorObject.getZ() + " " +
                        sensorObject.getTime());
                Log.d(LOG_TAG, "Serializabe: " + bytesToHex(new SensorObjectSerializer().Serialize(sensorObject)));
                 }
                float[] deltaRotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
                // User code should concatenate the delta rotation we computed with the current rotation
                // in order to get the updated rotation.
                // rotationCurrent = rotationCurrent * deltaRotationMatrix;


            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                float azimuth_angle = event.values[0];
                float pitch_angle = event.values[1];
                float roll_angle = event.values[2];

                sensorObject = new SensorObject(type_orientation, azimuth_angle,
                        pitch_angle,
                        roll_angle,
                        0);
                Log.d(LOG_TAG, "Orientation:" + sensorObject.getX() + " " + sensorObject.getY() + " " + sensorObject.getZ() + " " +
                        sensorObject.getTime());

                Log.d(LOG_TAG, "Serializabe: " + bytesToHex(new SensorObjectSerializer().Serialize(sensorObject)));

                clientTCP.sendMessage((new SensorObjectSerializer().Serialize(sensorObject)));

            }

            lasttime = event.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
}
