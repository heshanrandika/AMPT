package com.ampt.bluetooth.database.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ampt.bluetooth.bean.User;
import com.ampt.bluetooth.database.model.ActivityData;
import com.ampt.bluetooth.database.model.DogsData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Heshanr on 4/16/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = DatabaseHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ampt";

    // Table Names
    private static final String TABLE_ACTIVITY_DATA = "activity_data";
    private static final String TABLE_DOGS_DATA = "dogs_data";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_IMAGES = "images";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // TABLE_DOGS_DATA - column names
    private static final String KEY_NAME = "name";
    private static final String KEY_AGE = "age";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_STATUS = "status";
    private static final String KEY_DEVICE_NAME = "device_name";
    private static final String KEY_DEVICE_ADDRESS = "device_address";
    private static final String KEY_GOAL = "goal";
    private static final String GOAL_WALK = "goal_walk";
    private static final String GOAL_PLAY = "goal_play";
    private static final String KEY_BREED = "breed";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_DOB = "dob";


    // TABLE_ACTIVITY_DATA  - column names
    private static final String KEY_DOG_ID = "dog_id";
    private static final String KEY_PLAY = "play";
    private static final String KEY_WALK = "walk";
    private static final String KEY_SWIMMING = "swimming";
    private static final String KEY_SLEEP = "sleep";

    //TABLE_USERS
    private static final String KEY_LAST_NAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IMAGEID = "imageid";

    //TABLE IMAGES
    private static final String KEY_IMAGE_DATA = "imageData";

    private static String CREATE_USERS_TABLE = "CREATE TABLE "
            + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT NOT NULL,"
            + KEY_LAST_NAME + " TEXT NOT NULL,"
            + KEY_EMAIL + " TEXT NOT NULL,"
            + KEY_PASSWORD + " TEXT NOT NULL,"
            + KEY_IMAGEID + " TEXT"
            + ")";

    private static String CREATE_IMAGE_DATA_TABLE = "CREATE TABLE "
            + TABLE_IMAGES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_IMAGE_DATA + " BLOB NOT NULL"
            + ")";

    // TABLE_DOGS_DATA create statement
    private static final String CREATE_TABLE_DOGS_DATA = "CREATE TABLE "
            + TABLE_DOGS_DATA + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT NOT NULL,"
            + KEY_GOAL + " TEXT,"
            + GOAL_WALK + " INTEGER,"
            + GOAL_PLAY + " INTEGER,"
            + KEY_AGE + " INTEGER,"
            + KEY_IMAGEID + " TEXT NOT NULL,"
            + KEY_BREED + " TEXT NOT NULL,"
            + KEY_GENDER + " TEXT NOT NULL,"
            + KEY_DOB + " TEXT,"
            + KEY_DEVICE_NAME + " TEXT,"
            + KEY_DEVICE_ADDRESS + " TEXT,"
            + KEY_STATUS + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME"
            + ")";

    // TABLE_ACTIVITY_DATA create statement
    private static final String CREATE_TABLE_ACTIVITY_DATA = "CREATE TABLE "
            + TABLE_ACTIVITY_DATA + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_DOG_ID + " INTEGER,"
            + KEY_PLAY + " INTEGER,"
            + KEY_WALK + " INTEGER,"
            + KEY_SWIMMING + " INTEGER,"
            + KEY_SLEEP + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME"
            + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_DOGS_DATA);
        db.execSQL(CREATE_TABLE_ACTIVITY_DATA);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_IMAGE_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOGS_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        // create new tables
        onCreate(db);
    }

    // ------------------------ "todos" table methods ----------------//


    // Adding new user
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_LAST_NAME, user.getLastName());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_IMAGEID, user.getImageId());


        // Inserting Row
        long id = db.insert(TABLE_USERS, null, values);

        db.close(); // Closing database connection
        return id;
    }

    public User getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID, KEY_NAME, KEY_LAST_NAME, KEY_EMAIL, KEY_PASSWORD, KEY_IMAGEID}, KEY_EMAIL + "=?", new String[]{String.valueOf(email)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            user = new User(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5));
        }
        db.close();
        return user;
    }

    //save image
    public long saveImage(byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE_DATA, image);


        // Inserting Row
        long id = db.insert(TABLE_IMAGES, null, values);

        db.close(); // Closing database connection
        return id;
    }

    public byte[] getImage(long user_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        byte[] image = null;
        Cursor cursor = db.query(TABLE_IMAGES, new String[]{KEY_IMAGE_DATA}, KEY_ID + "=?", new String[]{String.valueOf(user_id)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            image = cursor.getBlob(0);
        }
        db.close();
        return image;
    }


    /**
     * Creating a dog profile
     */
    public long addDog(DogsData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, data.getName());
        values.put(KEY_GOAL, data.getGoal());
        values.put(GOAL_WALK, data.getGoalWalk());
        values.put(GOAL_PLAY, data.getGoalPlay());
        values.put(KEY_AGE, data.getAge());
        values.put(KEY_IMAGEID, data.getImageID());
        values.put(KEY_DEVICE_NAME, data.getDeviceName());
        values.put(KEY_DEVICE_ADDRESS, data.getDeviceAddress());
        values.put(KEY_BREED, data.getBreed());
        values.put(KEY_DOB, data.getDob());
        values.put(KEY_GENDER, data.getGender());
        values.put(KEY_STATUS, 0);
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long dog_id = db.insert(TABLE_DOGS_DATA, null, values);
        db.close();
        return dog_id;
    }

    /**
     * get single DogProfile
     */
    public DogsData getDogProfile(long dog_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_DOGS_DATA + " WHERE "
                + KEY_ID + " = " + dog_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {

            DogsData dogProfile = new DogsData();
            dogProfile.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            dogProfile.setName((c.getString(c.getColumnIndex(KEY_NAME))));
            dogProfile.setGoal((c.getString(c.getColumnIndex(KEY_GOAL))));
            dogProfile.setGoalWalk((c.getInt(c.getColumnIndex(GOAL_WALK))));
            dogProfile.setGoalPlay((c.getInt(c.getColumnIndex(GOAL_PLAY))));
            dogProfile.setAge((c.getInt(c.getColumnIndex(KEY_AGE))));
            dogProfile.setImageID((c.getString(c.getColumnIndex(KEY_IMAGEID))));
            dogProfile.setDeviceName((c.getString(c.getColumnIndex(KEY_DEVICE_NAME))));
            dogProfile.setDeviceAddress((c.getString(c.getColumnIndex(KEY_DEVICE_ADDRESS))));
            dogProfile.setBreed((c.getString(c.getColumnIndex(KEY_BREED))));
            dogProfile.setDob((c.getString(c.getColumnIndex(KEY_DOB))));
            dogProfile.setGender((c.getString(c.getColumnIndex(KEY_GENDER))));
            dogProfile.setStatus(false);
            dogProfile.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

            db.close();
            return dogProfile;
        } else {
            db.close();
            return null;
        }
    }

    /**
     * get Dog Basic Data
     */
    public DogsData getDogBasic(long dog_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  " + KEY_NAME + "," + KEY_AGE + "," + KEY_GOAL + "," + GOAL_WALK + "," + GOAL_PLAY + "," + KEY_ID + " FROM " + TABLE_DOGS_DATA + " WHERE "
                + KEY_ID + " = " + dog_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {

            DogsData dogProfile = new DogsData();
            dogProfile.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            dogProfile.setName((c.getString(c.getColumnIndex(KEY_NAME))));
            dogProfile.setGoal((c.getString(c.getColumnIndex(KEY_GOAL))));
            dogProfile.setGoalWalk((c.getInt(c.getColumnIndex(GOAL_WALK))));
            dogProfile.setGoalPlay((c.getInt(c.getColumnIndex(GOAL_PLAY))));
            dogProfile.setAge((c.getInt(c.getColumnIndex(KEY_AGE))));
            dogProfile.setBreed((c.getString(c.getColumnIndex(KEY_BREED))));
            dogProfile.setDob((c.getString(c.getColumnIndex(KEY_DOB))));
            dogProfile.setGender((c.getString(c.getColumnIndex(KEY_GENDER))));
            dogProfile.setImageID((c.getString(c.getColumnIndex(KEY_IMAGEID))));

            db.close();
            return dogProfile;
        } else {
            db.close();
            return null;
        }
    }

    /**
     * get single DogProfile by deviceAddress
     */
    public DogsData getDogProfileByAddress(String deviceAddress) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_DOGS_DATA + " WHERE "
                + KEY_DEVICE_ADDRESS + " = " + "\"" + deviceAddress + "\"";

        Log.i(LOG, selectQuery);
        //Cursor c = db.query(TABLE_DOGS_DATA, null, KEY_DEVICE_ADDRESS + "=?",new String[] { deviceAddress }, null, null, null, null);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            DogsData dogProfile = new DogsData();
            dogProfile.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            dogProfile.setName((c.getString(c.getColumnIndex(KEY_NAME))));
            dogProfile.setGoal((c.getString(c.getColumnIndex(KEY_GOAL))));
            dogProfile.setGoalWalk((c.getInt(c.getColumnIndex(GOAL_WALK))));
            dogProfile.setGoalPlay((c.getInt(c.getColumnIndex(GOAL_PLAY))));
            dogProfile.setAge((c.getInt(c.getColumnIndex(KEY_AGE))));
            dogProfile.setImageID((c.getString(c.getColumnIndex(KEY_IMAGEID))));
            dogProfile.setDeviceName((c.getString(c.getColumnIndex(KEY_DEVICE_NAME))));
            dogProfile.setDeviceAddress((c.getString(c.getColumnIndex(KEY_DEVICE_ADDRESS))));
            dogProfile.setBreed((c.getString(c.getColumnIndex(KEY_BREED))));
            dogProfile.setDob((c.getString(c.getColumnIndex(KEY_DOB))));
            dogProfile.setGender((c.getString(c.getColumnIndex(KEY_GENDER))));
            dogProfile.setStatus(false);
            dogProfile.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

            db.close();
            return dogProfile;

        } else {
            db.close();
            return null;
        }


    }

    /**
     * getting all DogProfile
     */
    public List<DogsData> getAllDogProfile() {
        List<DogsData> dogList = new ArrayList<DogsData>();
        String selectQuery = "SELECT  * FROM " + TABLE_DOGS_DATA;

        Log.i(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c != null && c.moveToFirst()) {
            Log.i(LOG, "cursor size : " + c.getCount());
            do {
                DogsData dogProfile = new DogsData();
                dogProfile.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                dogProfile.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                dogProfile.setGoal((c.getString(c.getColumnIndex(KEY_GOAL))));
                dogProfile.setGoalWalk((c.getInt(c.getColumnIndex(GOAL_WALK))));
                dogProfile.setGoalPlay((c.getInt(c.getColumnIndex(GOAL_PLAY))));
                dogProfile.setAge((c.getInt(c.getColumnIndex(KEY_AGE))));
                dogProfile.setImageID((c.getString(c.getColumnIndex(KEY_IMAGEID))));
                dogProfile.setDeviceName((c.getString(c.getColumnIndex(KEY_DEVICE_NAME))));
                dogProfile.setDeviceAddress((c.getString(c.getColumnIndex(KEY_DEVICE_ADDRESS))));
                dogProfile.setBreed((c.getString(c.getColumnIndex(KEY_BREED))));
                dogProfile.setDob((c.getString(c.getColumnIndex(KEY_DOB))));
                dogProfile.setGender((c.getString(c.getColumnIndex(KEY_GENDER))));
                dogProfile.setStatus(false);
                dogProfile.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                dogList.add(dogProfile);
            } while (c.moveToNext());
        }else{
            Log.i(LOG,"cursor did not move to 1st");
        }
        db.close();
        return dogList;
    }

    /**
     * getting all DeviceAddress
     */
    public ArrayList<String> getAllDeviceAddress() {
        ArrayList<String> deviceAddressList = new ArrayList<String>();
        String selectQuery = "SELECT  " + KEY_DEVICE_ADDRESS + " FROM " + TABLE_DOGS_DATA;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c != null && c.moveToFirst()) {
            do {
                deviceAddressList.add((c.getString(c.getColumnIndex(KEY_DEVICE_ADDRESS))));
            } while (c.moveToNext());
        }
        db.close();
        return deviceAddressList;
    }

    /**
     * getting DogProfile count
     */
    public int getDogProfileCount() {
        String countQuery = "SELECT  * FROM " + TABLE_DOGS_DATA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Updating a DogProfile
     */
    public int updateDogProfile(DogsData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, data.getName());
        values.put(KEY_GOAL, data.getGoal());
        values.put(GOAL_WALK, data.getGoalWalk());
        values.put(GOAL_PLAY, data.getGoalPlay());
        values.put(KEY_AGE, data.getAge());
        values.put(KEY_IMAGE, data.getImageID());
        values.put(KEY_STATUS, 0);
        values.put(KEY_BREED, data.getBreed());
        values.put(KEY_DOB, data.getDob());
        values.put(KEY_GENDER, data.getGender());
        values.put(KEY_CREATED_AT, getDateTime());


        int updated = db.update(TABLE_DOGS_DATA, values, KEY_ID + " = ?", new String[]{String.valueOf(data.getId())});
        db.close();
        return updated;
    }


    /**
     * Set Goal
     */
    public int setGoal(DogsData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GOAL_WALK, data.getGoalWalk());
        values.put(GOAL_PLAY, data.getGoalPlay());


        int updated = db.update(TABLE_DOGS_DATA, values, KEY_ID + " = ?", new String[]{String.valueOf(data.getId())});
        db.close();
        return updated;
    }


    /**
     * Deleting a DogProfile
     */
    public void deleteDogProfile(long dog_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOGS_DATA, KEY_ID + " = ?", new String[]{String.valueOf(dog_id)});
    }


    // ------------------------ "DogActivity" table methods ----------------//

    /**
     * creating DogActivity
     */
    public long createDogActivity(ActivityData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DOG_ID, data.getDogId());
        values.put(KEY_PLAY, data.getPlay());
        values.put(KEY_WALK, data.getWalk());
        values.put(KEY_SWIMMING, data.getSwimming());
        values.put(KEY_SLEEP, data.getSleep());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long activity_id = db.insert(TABLE_ACTIVITY_DATA, null, values);
        db.close();
        return activity_id;
    }

    /**
     * getting all ActivityDog
     */
    public ArrayList<ActivityData> getAllActivityDog(long dog_id) {
        ArrayList<ActivityData> activities = new ArrayList<ActivityData>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITY_DATA + " WHERE " + KEY_DOG_ID + " = " + dog_id;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c != null && c.moveToFirst()) {
            do {
                ActivityData activityData = new ActivityData();
                activityData.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                activityData.setDogId(c.getInt(c.getColumnIndex(KEY_DOG_ID)));
                activityData.setPlay(c.getInt(c.getColumnIndex(KEY_PLAY)));
                activityData.setSleep(c.getInt(c.getColumnIndex(KEY_SLEEP)));
                activityData.setSwimming(c.getInt(c.getColumnIndex(KEY_SWIMMING)));
                activityData.setWalk(c.getInt(c.getColumnIndex(KEY_WALK)));
                activityData.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));


                // adding to tags list
                activities.add(activityData);
            } while (c.moveToNext());
        }
        db.close();
        return activities;
    }


    /**
     * getting all ActivityDog
     */
    public ArrayList<ActivityData> getAllActivityDogDateRange(long dog_id, int dateBack) {
        ArrayList<ActivityData> activities = new ArrayList<ActivityData>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITY_DATA + " WHERE " + KEY_DOG_ID + " = " + dog_id + " AND " + KEY_CREATED_AT + " > DATE('NOW','-" + dateBack + " DAYS')";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c != null && c.moveToFirst()) {
            do {
                ActivityData activityData = new ActivityData();
                activityData.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                activityData.setDogId(c.getInt(c.getColumnIndex(KEY_DOG_ID)));
                activityData.setPlay(c.getInt(c.getColumnIndex(KEY_PLAY)));
                activityData.setSleep(c.getInt(c.getColumnIndex(KEY_SLEEP)));
                activityData.setSwimming(c.getInt(c.getColumnIndex(KEY_SWIMMING)));
                activityData.setWalk(c.getInt(c.getColumnIndex(KEY_WALK)));
                activityData.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));


                // adding to tags list
                activities.add(activityData);
            } while (c.moveToNext());
        }
        db.close();
        return activities;
    }


    /**
     * getting all Activity
     */

    public ArrayList<ActivityData> getAllActivityDateRange(int dateBack) {
        ArrayList<ActivityData> activities = new ArrayList<ActivityData>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITY_DATA + " WHERE " + KEY_CREATED_AT + " > DATE('NOW','-" + dateBack + " DAYS')";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c != null && c.moveToFirst()) {
            do {
                ActivityData activityData = new ActivityData();
                activityData.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                activityData.setDogId(c.getInt(c.getColumnIndex(KEY_DOG_ID)));
                activityData.setPlay(c.getInt(c.getColumnIndex(KEY_PLAY)));
                activityData.setSleep(c.getInt(c.getColumnIndex(KEY_SLEEP)));
                activityData.setSwimming(c.getInt(c.getColumnIndex(KEY_SWIMMING)));
                activityData.setWalk(c.getInt(c.getColumnIndex(KEY_WALK)));
                activityData.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));


                // adding to tags list
                activities.add(activityData);
            } while (c.moveToNext());
        }
        db.close();
        return activities;
    }

    /**
     * Deleting a Activity
     */
    public void deleteActivity(long activity_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVITY_DATA, KEY_ID + " = ?", new String[]{String.valueOf(activity_id)});
        db.close();
    }


    /**
     * Deleting a Activity by dog_id
     */
    public void deleteActivityByDog(long dog_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVITY_DATA, KEY_DOG_ID + " = ?", new String[]{String.valueOf(dog_id)});
        db.close();
    }

    /**
     * get datetime
     */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}

