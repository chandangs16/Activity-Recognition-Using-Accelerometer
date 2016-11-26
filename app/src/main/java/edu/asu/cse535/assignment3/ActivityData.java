package edu.asu.cse535.assignment3;

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
 * Purpose: A model class to hold activity data.
 *
 * @author Gowtham Ganesh Nayak mailto:gnayak2@asu.edu
 * @version November 2016
 */

public class ActivityData {
    private float[] x_values;
    private float[] y_values;
    private float[] z_values;
    private String activity = "";

    public ActivityData(float[] x_values, float[] y_values, float[] z_values, String activity) {
        this.x_values = x_values;
        this.y_values = y_values;
        this.z_values = z_values;
        this.activity = activity;
    }

    public float[] getX_values() {
        return x_values;
    }

    public void setX_values(float[] x_values) {
        this.x_values = x_values;
    }

    public float[] getZ_values() {
        return z_values;
    }

    public void setZ_values(float[] z_values) {
        this.z_values = z_values;
    }

    public float[] getY_values() {
        return y_values;
    }

    public void setY_values(float[] y_values) {
        this.y_values = y_values;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
