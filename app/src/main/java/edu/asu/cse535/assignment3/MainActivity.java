package edu.asu.cse535.assignment3;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import static java.lang.Math.abs;
/**
 * Copyright 2016 Gowtham Ganesh Nayak,
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Purpose: Main activity to collect data, train classifier, build the model, detect live activity
 * and test the models accuracy.
 *
 * @author Gowtham Ganesh Nayak mailto:gnayak2@asu.edu
 * @version November 2016
 */
public class MainActivity extends AppCompatActivity {
    AccIntentService accIntentService;
    Handler handler;
    ActivityDatabaseHandler activityDatabaseHandler;
    Intent intent;
    ServiceConnection serve;
    Intent testIntent;
    ServiceConnection testServiceConnection;
    TextView accuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        activityDatabaseHandler = new ActivityDatabaseHandler(MainActivity.this);
        setHandlerForService();
        accuracy = (TextView) findViewById(R.id.accuracyTextView);
    }

    public ArrayList<ActivityData> generateTrainingSetFile() {
        Log.w(this.getClass().getSimpleName(), "Generating training set");
        ArrayList<ActivityData> activityDataArrayList = activityDatabaseHandler.getAllActivityDataFromDatabase();
        File file = new File(Constants.TRAINING_DATA_FILE);
        file.delete();
        for (ActivityData activityData: activityDataArrayList) {
            new ActivityPublishHelper(activityData, this);
        }
        return activityDataArrayList;
    }

    public void createTestDatabase(View v){
//        activityDatabaseHandler.createTestDatabase();
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
        ArrayList<ActivityData> activityDataArrayList = generateTrainingSetFile();
       if (activityDataArrayList.size() > 0) {
               AndroidLibsvmClassifier androidLibsvmClassifier = new AndroidLibsvmClassifier();
               androidLibsvmClassifier.train();
       } else {
           Log.w(this.getClass().getSimpleName(), "Insufficient Data");
       }
    }

    public void classifyActivity(View v) {
        final Handler testHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.w(this.getClass().getSimpleName() , "Message received and stopping service");

                Bundle b = msg.getData();
                ActivityData activityData = (ActivityData) b.getSerializable("ActivityData");

                notifyCompletion();
                stopService(testIntent);
                unbindService(testServiceConnection);

                AndroidLibsvmClassifier androidLibsvmClassifier = new AndroidLibsvmClassifier();
                String activity = androidLibsvmClassifier.classify(activityData);
                toastActivity(activity);
            }
        };

        testIntent = new Intent(MainActivity.this.getBaseContext(), AccIntentService.class);
        testIntent.putExtra("activity", Constants.CLASSIFY);
        testServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AccIntentService testAaccIntentService = ((AccIntentService.LocalBinder) service).getInstance();
                testAaccIntentService.setHandler(testHandler);
                Log.w(this.getClass().getSimpleName() , "Test activity is connected to service");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        startService(testIntent);

        bindService(testIntent, testServiceConnection, Context.BIND_AUTO_CREATE);
    }

/*    public void classifyActivity(View v) {
        AndroidLibsvmClassifier androidLibsvmClassifier = new AndroidLibsvmClassifier();
        ArrayList<ActivityData> activityDataArrayList = activityDatabaseHandler.getAllActivityDataFromDatabase();
        String activity = androidLibsvmClassifier.classify(activityDataArrayList.get(40));
        Log.w("Activity Label ", activityDataArrayList.get(40).getActivity());
        toastActivity(activity);
    }*/

    public void  toastActivity(String activity) {
        Toast.makeText(this, activity, Toast.LENGTH_SHORT).show();
    }

    public void onTestAccuracy(View v) {
        AndroidLibsvmClassifier classifier = new AndroidLibsvmClassifier();
        float accuracyOfModel = classifier.testAccuracy();
        Log.w("Accuracy", String.valueOf(accuracyOfModel));
        accuracy.setText(String.valueOf(accuracyOfModel));
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
