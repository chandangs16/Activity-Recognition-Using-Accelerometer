package edu.asu.cse535.assignment3;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Date;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {
    AccIntentService accIntentService;
    Handler handler;
    ActivityDatabaseHandler activityDatabaseHandler;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityDatabaseHandler = new ActivityDatabaseHandler(MainActivity.this);

        setHandlerForService();
    }

    public void setHandlerForService() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //ideally do it in service. Because extra filling of array may be avoided.But
                // check if the built message will be sent to the queue if stopself is called
                // before sendig the message.
                Log.w(this.getClass().getSimpleName() , "Message received and stopping service");
//                accIntentService.stopSelf();
                stopService(intent);
                Bundle b = msg.getData();
                ActivityData activityData = (ActivityData) b.getSerializable("ActivityData");
                Log.w(this.getClass().getSimpleName() , "The data for " + activityData.getActivity() + " will be added to database");
                activityDatabaseHandler.addActivityToDatabase(activityData);
                Toast.makeText(MainActivity.this, "Activity added to Database", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void onStartRecordingEating(View v) {
        Log.w(this.getClass().getSimpleName() , "Button is clicked");
        intent = new Intent(MainActivity.this.getBaseContext(), AccIntentService.class);
        intent.putExtra("activity", "Eating");
        startService(intent);
        ServiceConnection serve = new ServiceConnection() {
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
}
