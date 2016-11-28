package edu.asu.cse535.assignment3;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Shashank on 11/26/2016.
 */

public class ActivityPublishHelper {
    public Context context;

    public ActivityPublishHelper(ActivityData activityData, Context context) {
        this.context = context;
        writeToFile(formatData(activityData), this.context);
    }

    public boolean writeToFile(String data,Context context) {
        try {
                File file = new File(Constants.TRAINING_DATA_FILE);
                file.createNewFile();
                FileWriter filewriter = new FileWriter(file, true);
                BufferedWriter out = new BufferedWriter(filewriter);

                out.write(data);

                out.close();
                filewriter.close();
                Log.w(this.getClass().getSimpleName(), "Successfully added data to file");
                return true;
        } catch (Exception e) {
            Log.w(this.getClass().getSimpleName(), e.toString());
        }
        return false;
    }

    private String formatData(ActivityData activityData) {
        StringBuilder data = new StringBuilder();
        data.append(getLabel(activityData.getActivity()));
        data.append(" ");

        int count= 0;
        for(int i = 0; i<50; i++) {
            count+=1;
            data.append(count+":");
            data.append(activityData.getX_values()[i]);
            data.append(" ");
            count+=1;
            data.append(count+":");
            data.append(activityData.getY_values()[i]);
            data.append(" ");
            count+=1;
            data.append(count+":");
            data.append(activityData.getZ_values()[i]);
            data.append(" ");
            data.append(System.lineSeparator());
        }

        return data.toString();
    }

    public static void clearFile() {
        File file = new File(Constants.TRAINING_DATA_FILE);
        file.delete();
    }

    private int getLabel(String label){
        switch(label){
            case Constants.ACTIVITY_EATING: return 1;
            case Constants.ACTIVITY_WALKING: return 2;
            case Constants.ACTIVITY_RUNNING: return 3;
            default: return 0;
        }
    }
}
