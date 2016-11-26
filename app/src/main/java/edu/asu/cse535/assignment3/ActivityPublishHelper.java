package edu.asu.cse535.assignment3;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Shashank on 11/26/2016.
 */

public class ActivityPublishHelper {
    public ActivityData activityData;
    private String TRAINING_DATA_SET = "training_data";
    public String MODEL_FILE = "model";
    public Context context;

    public ActivityPublishHelper(ActivityData activityData, Context context) {
        this.activityData = activityData;
        this.context = context;
        writeToFile(formatData(this.activityData), this.context);
    }

    private boolean fileExists() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + TRAINING_DATA_SET);
        if(!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    public boolean writeToFile(String data,Context context) {
        if(fileExists()) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download"+TRAINING_DATA_SET, Context.MODE_APPEND));
                outputStreamWriter.write(data);
                outputStreamWriter.close();
                return true;
            }
            catch (IOException e) {
                Log.e("IOException", "File write failed: " + e.toString());
                e.printStackTrace();

            }
        }
        return false;

    }

    private String formatData(ActivityData activityData) {
        StringBuilder data = new StringBuilder();
        data.append(getLabel(activityData.getActivity()));
        data.append(" ");
        for(int i = 0; i<50; i++){
            data.append("1:");
            data.append(activityData.getX_values()[i]);
            data.append(" ");
            data.append("2:");
            data.append(activityData.getY_values()[i]);
            data.append(" ");
            data.append("3:");
            data.append(activityData.getZ_values()[i]);
            data.append(" ");
        }
        return data.toString();
    }

    private int getLabel(String label){
        switch(label){
            case "eating": return 1;
            case "walking": return 2;
            case "running": return 3;
            default: return 0;
        }
    }





}
