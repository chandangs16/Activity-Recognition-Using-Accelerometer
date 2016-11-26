package edu.asu.cse535.assignment3;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;



import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AccIntentService  extends Service implements SensorEventListener {
    public final IBinder localBinder = new LocalBinder();
    public Handler handler;
    private Sensor sensor;
    private SensorManager sensorManager;
    private final int SAMPLE_SIZE = 150;
    int xvalues[];
    int yvalues[];
    int zvalues[];

    private int numSamples = 0;
    private boolean isActive = false;
    private double samplingRate = 0.0;
    public ArrayList<ArrayList<Float>> accelArray = new ArrayList<>();

    public AccIntentService() {
        xvalues = new int[SAMPLE_SIZE];
        yvalues = new int[SAMPLE_SIZE];
        zvalues = new int[SAMPLE_SIZE];
    }

    public class LocalBinder extends Binder {
        public AccIntentService getInstance() {
            return AccIntentService.this;
        }
    }

    public void onCreate() {
        Log.w(this.getClass().getSimpleName(), " OnCreate");

        sensorManager = (SensorManager)  getSystemService(Context.SENSOR_SERVICE);
        sensor  = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) this, sensor, 100000);

    }

    public void startRecording() {

        numSamples = 0;
        isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (handler != null && isActive()) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            numSamples++;
            long now = System.currentTimeMillis();
            if (numSamples > 50) {

                isActive = false;
                Message msg = handler.obtainMessage();
                float[] xAccelArray = new float[accelArray.get(0).size()];
                float[] yAccelArray = new float[accelArray.get(1).size()];
                float[] zAccelArray = new float[accelArray.get(2).size()];
                int i = 0;
                for(Float f: accelArray.get(0)){
                    xAccelArray[i++] = (f != null ? f : 0.0f);
                }
                i = 0;
                for(Float f: accelArray.get(1)) {
                    yAccelArray[i++] = (f != null ? f : 0.0f);
                }
                i = 0;
                for(Float f: accelArray.get(3)) {
                    zAccelArray[i++] = (f != null ? f: 0.0f);
                }

                Bundle b = new Bundle();
                b.putFloatArray("XAccelVals", xAccelArray);
                b.putFloatArray("YAccelVals", yAccelArray);
                b.putFloatArray("ZAccelVals", zAccelArray);
                msg.setData(b);
                handler.sendMessage(msg);
                numSamples = 0;
                accelArray.get(0).clear();
                accelArray.get(1).clear();
                accelArray.get(2).clear();

            }
            else{
                accelArray.get(0).add(x);
                accelArray.get(1).add(y);
                accelArray.get(2).add(z);

            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
