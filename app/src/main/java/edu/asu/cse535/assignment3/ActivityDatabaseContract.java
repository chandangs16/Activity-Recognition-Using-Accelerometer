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
 * Purpose: A contract class which holds database table information.
 *
 * SER598 Mobile Applications
 * see http://pooh.poly.asu.edu/Mobile
 *
 * @author Gowtham Ganesh Nayak mailto:gnayak2@asu.edu
 * @version March 2016
 */
public class ActivityDatabaseContract {
    ActivityDatabaseContract() {
        StringBuilder stringBuilder = new StringBuilder();
        String SPACE = " ";
        String COMMA = ",";
        String ACCEL_X = "ACCEL_X";
        String ACCEL_Y = "ACCEL_Y";
        String ACCEL_Z = "ACCEL_Z";

        stringBuilder.append("CREATE TABLE");
        stringBuilder.append(SPACE);
        stringBuilder.append(ActivityEntry.TABLE_NAME);
        stringBuilder.append(SPACE);
        stringBuilder.append("(");
        stringBuilder.append(SPACE);

        //id primary key autoincrement
        stringBuilder.append("ID INTEGER PRIMARY KEY AUTOINCREMENT");
        stringBuilder.append(COMMA);
        stringBuilder.append(SPACE);
        // build the query
        for(int i=1;i<=50;i++) {
            // x value
            stringBuilder.append(ACCEL_X);
            stringBuilder.append(i);
            stringBuilder.append(SPACE);
            stringBuilder.append("REAL");
            stringBuilder.append(COMMA);
            stringBuilder.append(SPACE);

            // y value
            stringBuilder.append(ACCEL_Y);
            stringBuilder.append(i);
            stringBuilder.append(SPACE);
            stringBuilder.append("REAL");
            stringBuilder.append(COMMA);
            stringBuilder.append(SPACE);

            //z value
            stringBuilder.append(ACCEL_Z);
            stringBuilder.append(i);
            stringBuilder.append(SPACE);
            stringBuilder.append("REAL");
            stringBuilder.append(COMMA);
            stringBuilder.append(SPACE);
        }

        // activity label
        stringBuilder.append("ACTIVITY_LABEL TEXT");
        stringBuilder.append(SPACE);
        stringBuilder.append(");");

        ActivityEntry.TABLE_COLUMN_QUERY_STRING =  stringBuilder.toString();
    }

    public static abstract class ActivityEntry {
        public static final String TABLE_NAME = "ACTIVITY_TABLE";
        public static String TABLE_COLUMN_QUERY_STRING = "";
        public static final String ACCEL_X = "ACCEL_X";
        public static final String ACCEL_Y = "ACCEL_Y";
        public static final String ACCEL_Z = "ACCEL_Z";
        public static final String ACTIVITY_LABEL = "ACTIVITY_LABEL";
    }
}
