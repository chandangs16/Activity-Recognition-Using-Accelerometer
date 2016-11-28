package edu.asu.cse535.assignment3;

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

import java.security.Timestamp;

public class AccIntentService  extends Service implements SensorEventListener {
    public final IBinder localBinder = new LocalBinder();
    public Handler handler;
    private Sensor sensor;
    private SensorManager sensorManager;
    private final int SAMPLE_SIZE = 50;
    private int count = 0;
    float xvalues[];
    float yvalues[];
    float zvalues[];
    String activity = "";
    Intent parentIntent;
    long previousTimestamp;
    boolean serviceBound;

    public AccIntentService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(this.getClass().getSimpleName() , "Service started and values initialized");
        xvalues = new float[SAMPLE_SIZE];
        yvalues = new float[SAMPLE_SIZE];
        zvalues = new float[SAMPLE_SIZE];
        this.parentIntent = intent;
        this.activity = intent.getStringExtra("activity");
        sensorManager.registerListener((SensorEventListener) this, sensor, 1000000);
        previousTimestamp = System.currentTimeMillis();
        serviceBound = true;
        return super.onStartCommand(intent, flags, startId);
    }

    public class LocalBinder extends Binder {
        public AccIntentService getInstance() {
            return AccIntentService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.w(this.getClass().getSimpleName(), " Service is created");
        sensorManager = (SensorManager)  getSystemService(Context.SENSOR_SERVICE);
        sensor  = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.w(this.getClass().getSimpleName() , "Value sensed");
        if (handler != null && serviceBound) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            if (count < 50) {
                long currentTimestamp = System.currentTimeMillis();
                if (currentTimestamp-previousTimestamp >= 50) {
                    previousTimestamp = currentTimestamp;
                    Log.w(this.getClass().getSimpleName() , "Values getting added to array" + String.valueOf(count));
                    xvalues[count] = x;
                    yvalues[count] = y;
                    zvalues[count] = z;
                    count++;
                }
            }
            else {
                Log.w(this.getClass().getSimpleName() , " Finished collecting activity data");
                // stop the service after the completion of this if.
                ActivityData activityData = new ActivityData(xvalues, yvalues, zvalues, activity);

                xvalues = new float[SAMPLE_SIZE];
                yvalues = new float[SAMPLE_SIZE];
                zvalues = new float[SAMPLE_SIZE];

                Log.w(this.getClass().getSimpleName() , "Message is being sent back to handler");
                Message msg = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putSerializable("ActivityData", activityData);
                msg.setData(b);

                handler.sendMessage(msg);
                count = 0;
                sensorManager.unregisterListener(this, sensor);
                serviceBound = false;
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

    public void setHandler(Handler handler)
    {
        this.handler = handler;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

    }


}
