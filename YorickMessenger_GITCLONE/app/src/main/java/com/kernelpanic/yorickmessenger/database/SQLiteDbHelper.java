package com.kernelpanic.yorickmessenger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "yorickmessenger_database";


    private static final String TABLE_MESSAGES      = "messages";
    private static final String TABLE_USER_INFO     = "user_info";

    public static final String ID         = "ID"; // Message ID
    public static final String CONTENTS   = "message_contents"; // Content of the message
    public static final String SENDER     = "message_sender_id"; // ID of the sender
    public static final String RECEIVER   = "message_receiver_id"; // ID of the receiver
    public static final String TIMESTAMP  = "message_timestamp";

    public static final String uID          = "user_id";
    public static final String FULLNAME     = "fullname";
    public static final String NAME         = "name";
    public static final String SURNAME      = "surname";
    public static final String PROFILE_PIC  = "profilepic";

    public SQLiteDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_MESSAGES + "(" +
                ID + " integer primary key autoincrement, " +
                CONTENTS + " text, " + SENDER + " text," + RECEIVER + " text," +
                TIMESTAMP + " text" + ")");

        db.execSQL("create table " + TABLE_USER_INFO + "(" +
                uID + " integer primary key autoincrement, " + FULLNAME + " text, " +
                PROFILE_PIC + " blob" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_INFO);
        onCreate(db);
    }

    public void createUser(User user, byte[] image) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(uID, user.getId());
        values.put(FULLNAME, user.getFullname());
        values.put(PROFILE_PIC, image);

        db.insert(TABLE_USER_INFO, null, values);
        db.close();
    }

    public String getUserInfo() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER_INFO, null, null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String userInfo = cursor.getString(1);
        cursor.close();
        db.close();
        return userInfo;
    }

    public Bitmap getProfilePic() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER_INFO, null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        byte[] imageBytes = cursor.getBlob(2);
        cursor.close();

        Bitmap profilePic = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        return profilePic;
    }

}
