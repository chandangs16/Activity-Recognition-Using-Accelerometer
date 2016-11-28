package edu.asu.cse535.assignment3;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {
    AccIntentService accIntentService;
    Handler handler;
    ActivityDatabaseHandler activityDatabaseHandler;
    Intent intent;
    ServiceConnection serve;
    ArrayList<ActivityData> activityDataArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        activityDatabaseHandler = new ActivityDatabaseHandler(MainActivity.this);

        setHandlerForService();
    }

    public void generateTrainingSetFile(View v) {
        Log.w(this.getClass().getSimpleName(), "Fetched Values from datase");
        activityDataArrayList = activityDatabaseHandler.getAllActivityDataFromDatabase();
        Log.w(this.getClass().getSimpleName(), "Fetched Values from datbase");
        File file = new File(Constants.TRAINING_DATA_FILE);
        file.delete();
        for (ActivityData a: activityDataArrayList) {
            new ActivityPublishHelper(a, this);
        }
    }

    public void setHandlerForService() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //ideally do it in service. Because extra filling of array may be avoided.But
                // check if the built message will be sent to the queue if stopself is called
                // before sendig the message.
                Log.w(this.getClass().getSimpleName() , "Message received and stopping service");

                Bundle b = msg.getData();
                ActivityData activityData = (ActivityData) b.getSerializable("ActivityData");
                Log.w(this.getClass().getSimpleName() , "The data for " + activityData.getActivity() + " will be added to database");
                activityDatabaseHandler.addActivityToDatabase(activityData);
                stopAccService();

                notifyCompletion();
                Toast.makeText(MainActivity.this, "Activity added to Database", Toast.LENGTH_SHORT).show();
                new ActivityPublishHelper(activityData, getApplicationContext());
            }
        };
    }

    public void notifyCompletion() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAccService() {
        this.stopService(intent);
        this.unbindService(serve);
    }

    public void onStartRecordingRunning(View v) {
        Log.w(this.getClass().getSimpleName() , "Button is clicked");
        intent = new Intent(MainActivity.this.getBaseContext(), AccIntentService.class);
        intent.putExtra("activity", Constants.ACTIVITY_RUNNING);
        startService(intent);
        serve = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                accIntentService = ((AccIntentService.LocalBinder) service).getInstance();
                accIntentService.setHandler(handler);
                Log.w(this.getClass().getSimpleName() , "Activity is connected to service");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        bindService(intent, serve, Context.BIND_AUTO_CREATE);
    }

    public void onStartRecordingEating(View v) {
        Log.w(this.getClass().getSimpleName() , "Button is clicked");
        intent = new Intent(MainActivity.this.getBaseContext(), AccIntentService.class);
        intent.putExtra("activity", Constants.ACTIVITY_EATING);
        startService(intent);
        serve = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                accIntentService = ((AccIntentService.LocalBinder) service).getInstance();
                accIntentService.setHandler(handler);
                Log.w(this.getClass().getSimpleName() , "Activity is connected to service");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        bindService(intent, serve, Context.BIND_AUTO_CREATE);
    }

    public void onStartRecordingWalking(View v) {
        Log.w(this.getClass().getSimpleName() , "Button is clicked");
        intent = new Intent(MainActivity.this.getBaseContext(), AccIntentService.class);
        intent.putExtra("activity", Constants.ACTIVITY_WALKING);
        startService(intent);
        serve = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                accIntentService = ((AccIntentService.LocalBinder) service).getInstance();
                accIntentService.setHandler(handler);
                Log.w(this.getClass().getSimpleName() , "Activity is connected to service");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        bindService(intent, serve, Context.BIND_AUTO_CREATE);
    }

    public void onTrain(View v) {
        Intent intent = new Intent(MainActivity.this, AndroidLibsvmExampleActivity.class);
        intent.putExtra("ActivityData", activityDataArrayList.get(0));
        Log.w(this.getClass().getSimpleName(), activityDataArrayList.get(0).getActivity());
        startActivity(intent);
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void clearData(View v) {
        if(activityDatabaseHandler !=null) {
            activityDatabaseHandler.deleteAllActivityDataFromDatabase();
        }

        File file = new File(Constants.TRAINING_DATA_FILE);
        file.delete();
        Toast.makeText(this, "Activity data cleared from database", Toast.LENGTH_SHORT).show();
        Log.w(this.getClass().getSimpleName(), "Data cleared from database");
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(MainActivity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
