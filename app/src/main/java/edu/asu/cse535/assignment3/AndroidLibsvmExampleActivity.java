package edu.asu.cse535.assignment3;

/**
 * Created by Shashank on 11/26/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AndroidLibsvmExampleActivity extends Activity {
    /** Called when the activity is first created. */
    private static final String TAG = "Libsvm";
    Intent intent;


    // svm native
    private native int trainClassifierNative(String trainingFile, int kernelType,
                                             int cost, float gamma, int isProb, String modelFile);
    private native int doClassificationNative(float values[][], int indices[][],
                                              int isProb, String modelFile, int labels[], double probs[]);

    static {
        System.loadLibrary("signal");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        Button trainButton = (Button)findViewById(R.id.train);
        Button classifyButton = (Button)findViewById(R.id.classifiy);
        intent  = getIntent();
        trainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                train();
            }
        });
        classifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classify((ActivityData) intent.getSerializableExtra("ActivityData"));
            }
        });
    }

    private void train() {
        // Svm training
        int kernelType = 2; // Radial basis function
        int cost = 4; // Cost
        int isProb = 0;
        float gamma = 0.25f; // Gamma
        String trainingFileLoc = Constants.TRAINING_DATA_FILE;
        String modelFileLoc = Constants.MODEL_FILE;
        if (trainClassifierNative(trainingFileLoc, kernelType, cost, gamma, isProb,
                modelFileLoc) == -1) {
            Log.w(TAG, "training err");
            //finish();
        }
        Log.w(this.getClass().getSimpleName(), "Finished training the classifier");
        Toast.makeText(this, "Training is done", Toast.LENGTH_SHORT).show();
    }

    /**
     * classify generate labels for features.
     * Return:
     * 	-1: Error
     * 	0: Correct
     */
    public int callSVM(float values[][], int indices[][], int groundTruth[], int isProb, String modelFile,
                       int labels[], double probs[]) {
        // SVM type
        final int C_SVC = 0;
        final int NU_SVC = 1;
        final int ONE_CLASS_SVM = 2;
        final int EPSILON_SVR = 3;
        final int NU_SVR = 4;

        // For accuracy calculation
        int correct = 0;
        int total = 0;
        float error = 0;
        float sump = 0, sumt = 0, sumpp = 0, sumtt = 0, sumpt = 0;
        float MSE, SCC, accuracy;

        int num = values.length;
        int svm_type = C_SVC;
        if (num != indices.length)
            return -1;
        // If isProb is true, you need to pass in a real double array for probability array
        int r = doClassificationNative(values, indices, isProb, modelFile, labels, probs);

        // Calculate accuracy
        if (groundTruth != null) {
            if (groundTruth.length != indices.length) {
                return -1;
            }
            for (int i = 0; i < num; i++) {
                int predict_label = labels[i];
                int target_label = groundTruth[i];
                if(predict_label == target_label)
                    ++correct;
                error += (predict_label-target_label)*(predict_label-target_label);
                sump += predict_label;
                sumt += target_label;
                sumpp += predict_label*predict_label;
                sumtt += target_label*target_label;
                sumpt += predict_label*target_label;
                ++total;
            }

            if (svm_type==NU_SVR || svm_type==EPSILON_SVR)
            {
                MSE = error/total; // Mean square error
                SCC = ((total*sumpt-sump*sumt)*(total*sumpt-sump*sumt)) / ((total*sumpp-sump*sump)*(total*sumtt-sumt*sumt)); // Squared correlation coefficient
            }
            accuracy = (float)correct/total*100;
            Log.d(TAG, "Classification accuracy is " + accuracy);
        }

        return r;
    }

    private void classify(ActivityData activityData) {
        // Svm classification
        float[][] values = new float[1][150];
        float[] xvalues = activityData.getX_values();
        float[] yvalues = activityData.getY_values();
        float[] zvalues = activityData.getZ_values();
        float[] ary = new float[150];

        int i=0;
        int count =0;
        values[0] = ary;

        while(i<150) {
            ary[i++] =xvalues[count];
            ary[i++] =yvalues[count];
            ary[i++] =zvalues[count];
            count++;
        }

        int[][] indices = new int [1][150];
        int[] ary1 = new int [150];

        indices[0] = ary1;
        for(int j=0;j<150;j++) {
            ary1[j] = j+1;
        }

        int[] groundTruth = {1};
        int[] labels = new int[4];
        double[] probs = new double[4];
        int isProb = 0; // Not probability prediction
        String modelFileLoc = Constants.MODEL_FILE;

        if (callSVM(values, indices, groundTruth, isProb, modelFileLoc, labels, probs) != 0) {
            Log.w(TAG, "Classification is incorrect");
        }
        else {
            String m = "";
            for (int l : labels)
                m += l + ", ";
            Toast.makeText(this, "Classification is done, the result is " + m, Toast.LENGTH_SHORT).show();
        }
    }
}
