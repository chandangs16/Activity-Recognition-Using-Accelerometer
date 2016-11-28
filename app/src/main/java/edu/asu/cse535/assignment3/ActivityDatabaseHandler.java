package edu.asu.cse535.assignment3;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;

/**
 * Copyright 2016 Gowtham Ganesh Nayak,
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Purpose: A database helper class which initializes database from a
 * database file present in the raw folder of the application. Additionally
 * it assists in addition, updation, deletion of movies from database.
 * <p>
 * @author Gowtham Ganesh Nayak mailto:gnayak2@asu.edu
 * @version November 2016
 */
public class ActivityDatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "activity_description.db";
    private static final int DATABASE_VERSION = 1;
    private final String DATABASE_PATH = "/data/data/edu.asu.cse535.assignment3/databases/";
    private Context context;

    public ActivityDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        // create and ignore the object since we are setting the static string in activityentry class;
        new ActivityDatabaseContract();
        try {
            createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //getWritableDatabase();
    }

    /**
     * Overridden method to creates a database table.
     *
     * @param db The SQLite database
     * @return Returns void.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(this.getClass().getSimpleName(), " OnCreate");

        //db.execSQL(ActivityDatabaseContract.ActivityEntry.TABLE_COLUMN_QUERY_STRING);
        Log.w(this.getClass().getSimpleName(), " Table is created");
        //Log.w("Database Query: ", ActivityDatabaseContract.ActivityEntry.TABLE_COLUMN_QUERY_STRING);
    }

    /**
     * Overridden method to upgrade a database table.
     *
     * @param db         The SQLite database
     * @param oldVersion Old Version Number
     * @param newVersion New Version Number
     * @return Returns void.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + ActivityDatabaseContract.ActivityEntry.TABLE_NAME;
        db.execSQL(dropTable);
        onCreate(db);
    }

    /**
     * Adds the movie to the database.
     *
     * @param activityData Object.
     * @return Returns true if insertion is successful else returns false.
     */
    public boolean addActivityToDatabase(ActivityData activityData) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = getContentValues(activityData);

        long responseValue = db.insert(ActivityDatabaseContract.ActivityEntry.TABLE_NAME, null, contentValues);
        db.close();

        if (responseValue == -1) {
            // Also it may fail because of unique ID. Handle this case.
            Log.w(this.getClass().getSimpleName() , "Failed to add values to database");
            return false;
        } else {
            Log.w(this.getClass().getSimpleName() , "Successfully added values to database");
            return true;
        }
    }

    private ContentValues getContentValues(ActivityData activityData) {
        ContentValues contentValues = new ContentValues();

        // add a check to see if all the arrays are of equal length

        float[] xvalues = activityData.getX_values();
        float[] yvalues = activityData.getY_values();
        float[] zvalues = activityData.getZ_values();

        for (int i=0;i <xvalues.length;i++) {
            contentValues.put(ActivityDatabaseContract.ActivityEntry.ACCEL_X + String.valueOf(i+1), xvalues[i]);
            contentValues.put(ActivityDatabaseContract.ActivityEntry.ACCEL_Y + String.valueOf(i+1), yvalues[i]);
            contentValues.put(ActivityDatabaseContract.ActivityEntry.ACCEL_Z + String.valueOf(i+1), zvalues[i]);
        }

        contentValues.put(ActivityDatabaseContract.ActivityEntry.ACTIVITY_LABEL, activityData.getActivity());
        return contentValues;
    }

    /**
     * Deletes the movie description from the database.
     *
     * @return Returns true if deletion is successful else returns false.
     */
    public boolean deleteAllActivityDataFromDatabase() {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + ActivityDatabaseContract.ActivityEntry.TABLE_NAME);
        db.close();
        return true;
//        int rowCount = db.delete(ActivityDatabaseContract.ActivityEntry.TABLE_NAME, ActivityDatabaseContract.ActivityEntry.ID + " = ? ", new String[]{"*"});


//        if (rowCount == 0) {
//            return false;
//        } else {
//            return true;
//        }
    }

    /**
     * Fetches the movie description from the database.
     *
     * @param movieId The Id of the movie.
     * @return Returns a Movie object if found else returns null.
     */

    /*public Movie getMovieInformationFromDatabase(String movieId) {
        SQLiteDatabase db = getWritableDatabase();

        String querySelection = "SELECT * FROM " + MovieDatabaseContract.MovieEntry.TABLE_NAME + " WHERE " + MovieDatabaseContract.MovieEntry.MOVIE_ID + " = ? ";
        Cursor cursor = db.rawQuery(querySelection, new String[]{movieId});

        if (cursor == null) {
            Log.w(this.getClass().getSimpleName(), " Cursor is null");
            db.close();
            return null;
        }

        // Close db only after cursor operation. Else cursor will be invalid.
        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return null;
        } else {
            Log.w(this.getClass().getSimpleName(), " getMovieInformationFromDatabase(String movieId) Successful Query");
            cursor.moveToFirst();
            Movie movie = getMovieFromCursor(cursor);
            // Close the cursor first before closing the database. Else cursor will be invalid.
            cursor.close();
            db.close();
            return movie;
        }
    }*/

    /**
     * Fetches the movie cell information from the database.
     *
     * @return ArrayList of Movie
     */
    // Change this to be compatible with the getAllMovieCellInformationDatabase() method
   /* public ArrayList<Movie> getAllMovieInformationFromDatabase() {
        ArrayList<Movie> moviesArrayList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();

        String querySelection = "SELECT * FROM " + MovieDatabaseContract.MovieEntry.TABLE_NAME + " WHERE 1";
        Cursor cursor = db.rawQuery(querySelection, null);

        if (cursor == null) {
            Log.w(this.getClass().getSimpleName(), " getAllMovieInformationFromDatabase() Unsuccessful Query");
            db.close();
            return null;
        }

        // Close db only after cursor operation. Else cursor will be invalid.
        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return null;
        } else {
            Log.w(this.getClass().getSimpleName(), " getAllMovieInformationFromDatabase() Successful Query");
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Movie movie = getMovieFromCursor(cursor);
                moviesArrayList.add(movie);
                cursor.moveToNext();
            }

            // Close the cursor first before closing the database. Else cursor will be invalid.
            cursor.close();
            db.close();
            return moviesArrayList;
        }
    }*/


    /*private Movie getMovieFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(MovieDatabaseContract.MovieEntry.MOVIE_ID));
        String title = cursor.getString(cursor.getColumnIndex(MovieDatabaseContract.MovieEntry.MOVIE_TITLE));
        String year = cursor.getString(cursor.getColumnIndex(MovieDatabaseContract.MovieEntry.MOVIE_YEAR));
        String rated = cursor.getString(cursor.getColumnIndex(MovieDatabaseContract.MovieEntry.MOVIE_RATED));
        String released = cursor.getString(cursor.getColumnIndex(MovieDatabaseContract.MovieEntry.MOVIE_RELEASED));
        String runtime = cursor.getString(cursor.getColumnIndex(MovieDatabaseContract.MovieEntry.MOVIE_RUNTIME));
        String genre = cursor.getString(cursor.getColumnIndex(MovieDatabaseContract.MovieEntry.MOVIE_GENRE));
        String actors = cursor.getString(cursor.getColumnIndex(MovieDatabaseContract.MovieEntry.MOVIE_ACTORS));
        String plot = cursor.getString(cursor.getColumnIndex(MovieDatabaseContract.MovieEntry.MOVIE_PLOT));
        String poster = cursor.getString(cursor.getColumnIndex(MovieDatabaseContract.MovieEntry.MOVIE_POSTER));
        return new Movie(id, title, year, rated, released, runtime, genre, actors, plot, poster);
    }*/

    /**
     * Uses existing database. Else copies from raw folder.
     *
     * @throws IOException
     */
    public void createDatabase() throws IOException {
        Log.w(this.getClass().getSimpleName(), "crateDatabase() method called");
        boolean doesDBExist = checkIfDatabaseExists();
        if (!doesDBExist) {
            try {
                /*This is gonna create an empty database in systems default path
                 then we can overwrite this database with our database.*/
                this.getReadableDatabase();
                copyDatabase();
                Log.w(this.getClass().getSimpleName(), "Database copied from raw folder");
            } catch (IOException e) {
                Log.w(this.getClass().getSimpleName(), "Error Copying Database " + e.getMessage());
            }
        } else {
            Log.w(this.getClass().getSimpleName(), "Database already exists");
        }
    }

    /**
     * Checks if database exists.
     *
     * @return True if present else False.
     */
    public boolean checkIfDatabaseExists() {
        SQLiteDatabase sqLiteDatabase = null;
        String database = DATABASE_PATH + DATABASE_NAME;
        try {
            sqLiteDatabase = SQLiteDatabase.openDatabase(database, null, SQLiteDatabase.OPEN_READONLY);
            Log.w(this.getClass().getSimpleName(), "Database exists");
        } catch (Exception e) {
            Log.w(this.getClass().getSimpleName(), "Database doesn't exist");
        }

        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Copies database from raw to application data
     *
     * @throws IOException
     */
    public void copyDatabase() throws IOException {
//        InputStream inputStream = context.getResources().openRawResource(R.raw.movie_description);
//        String database = DATABASE_PATH + DATABASE_NAME;
//        OutputStream outputStream = new FileOutputStream(database);
//        byte[] buffer = new byte[1024];
//        int length;
//        while ((length = inputStream.read(buffer)) > 0) {
//            outputStream.write(buffer, 0, length);
//        }
//        outputStream.flush();
//        outputStream.close();
//        inputStream.close();
    }
}
