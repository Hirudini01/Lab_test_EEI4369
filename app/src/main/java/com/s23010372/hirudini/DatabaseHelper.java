package com.s23010372.hirudini;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "UserData.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "user_data";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "username";
    private static final String COL_PASSWORD = "password"; // Add this line

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_PASSWORD + " TEXT)"); // Add password column
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Update this method to accept and save both username and password
    public boolean insertData(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, username);
        values.put(COL_PASSWORD, password); // Save password
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }
}
