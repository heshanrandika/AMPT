package com.app.ampt.ampt.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.ampt.ampt.bean.User;

/**
 * Created by malith on 7/23/15.
 */
public class DBHelper extends SQLiteOpenHelper {


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ampt_db";


    private static final String TABLE_USERS = "users";


    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LAST_NAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IMAGEPATH = "imagepath";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("Creating database!");
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_LAST_NAME + " TEXT," + KEY_EMAIL + " TEXT," + KEY_PASSWORD + " TEXT,"
                + KEY_IMAGEPATH + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }


    // Adding new user
    public long addContact(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_LAST_NAME, user.getLastName());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_IMAGEPATH, user.getImagePath());


        // Inserting Row
        long id = db.insert(TABLE_USERS, null, values);

        db.close(); // Closing database connection
        return id;
    }

    // Getting single user
    public User getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID, KEY_NAME, KEY_LAST_NAME, KEY_EMAIL, KEY_PASSWORD, KEY_IMAGEPATH}, KEY_EMAIL + "=?", new String[]{String.valueOf(email)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            user = new User(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        }
        return user;
    }

    // Getting All users
//    public List<User> getAllUsers() {
//    }

    // Updating single user
//    public int updateUser(User user) {
//    }

    // Deleting single user
    public void deleteUser(User user) {
    }

}
