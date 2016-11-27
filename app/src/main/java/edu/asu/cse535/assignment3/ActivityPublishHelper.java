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
    public ActivityData activityData;
    private String TRAINING_DATA_SET = "training_data";
    public String MODEL_FILE = "model";
    public Context context;

    public ActivityPublishHelper(ActivityData activityData, Context context) {
        this.activityData = activityData;
        this.context = context;
        writeToFile(formatData(this.activityData), this.context);
    }




    public boolean writeToFile(String data,Context context) {
        try {

                File external = Environment.getExternalStorageDirectory();
                String sdcardPath = external.getPath();
                File file = new File(sdcardPath + "/Download/"+TRAINING_DATA_SET);
                file.createNewFile();
                FileWriter filewriter = new FileWriter(file, true);
                BufferedWriter out = new BufferedWriter(filewriter);

                out.write(data);

                out.close();
                filewriter.close();
                return true;

        } catch (Exception e) {
            android.util.Log.d("failed to save file", e.toString());
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
        data.append(System.lineSeparator());
        return data.toString();
    }

    private int getLabel(String label){
        switch(label){
            case "Eating": return 1;
            case "Walking": return 2;
            case "Running": return 3;
            default: return 0;
        }
    }





}
