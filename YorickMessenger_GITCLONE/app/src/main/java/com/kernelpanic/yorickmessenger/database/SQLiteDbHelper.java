package com.kernelpanic.yorickmessenger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 3;
    private static final String DB_NAME = "yorickmessenger_database";


    private static final String TABLE_MESSAGES      = "messages";
    private static final String TABLE_USER_INFO     = "user_info";

    public static final String ID                   = "message_id"; // Message ID
    public static final String DEVICE_MAC_ADDRESS   = "message_device_mac_address";
    public static final String USER_NAME            = "message_username";
    public static final String CONTENTS             = "message_contents"; // Content of the message
    public static final String TYPE                 = "message_type"; // Type of the message (Sent Or Received)
    public static final String TIMESTAMP            = "message_timestamp";

    public static final String uID          = "user_id";
    public static final String FULLNAME     = "fullname";

    private Context context;

    public SQLiteDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        //Executing a SQL command to CREATE messages table
        db.execSQL("CREATE TABLE " + TABLE_MESSAGES + "(" +
                DEVICE_MAC_ADDRESS + " VARCHAR, " +
                CONTENTS + " VARCHAR, " +
                USER_NAME + " VARCHAR, " +
                TYPE + " INTEGER, " +
                TIMESTAMP + " TIMESTAMP" + ")");

        Toast.makeText(context, DEVICE_MAC_ADDRESS, Toast.LENGTH_SHORT).show();

        //Executing a SQL command to CREATE user's info table
        db.execSQL("create table " + TABLE_USER_INFO + "(" +
                uID + " integer primary key autoincrement, " + FULLNAME + " text" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_INFO);
        onCreate(db);
    }

    /***
     * Method that allow to us add into the table a new user
     * @param user - object of the User class
     */
    public void createUser(@NonNull User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(uID, user.getId());
        values.put(FULLNAME, user.getFullname());

        db.insert(TABLE_USER_INFO, null, values);
        db.close();
    }

    // Return user's fullname
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

    public void addMessageRecord(@NonNull ChatMessage obj) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DEVICE_MAC_ADDRESS, obj.getMacAddress());
        values.put(CONTENTS, obj.getContent());
        values.put(TYPE, obj.getType());
        values.put(TIMESTAMP, obj.getCurrentTime());
        values.put(USER_NAME, obj.getUsername());

        db.insert(TABLE_MESSAGES, null, values);
        db.close();
        Toast.makeText(context, "New Chat Record have been successfully added", Toast.LENGTH_SHORT).show();
    }

    public List<ChatMessage> querySelect(String deviceMac) {
        ArrayList<ChatMessage> list = new ArrayList<>();
        Cursor cursor = queryCursor(deviceMac);
        while (cursor.moveToNext()) {
            ChatMessage message = new ChatMessage();
            message.setContent(cursor.getString(cursor.getColumnIndex(CONTENTS)));
            message.setMacAddress(cursor.getString(cursor.getColumnIndex(DEVICE_MAC_ADDRESS)));
            message.setUsername(cursor.getString(cursor.getColumnIndex(USER_NAME)));
            message.setType(cursor.getInt(cursor.getColumnIndex(TYPE)));
            message.setCurrentTime(cursor.getLong(cursor.getColumnIndex(TIMESTAMP)));
            list.add(message);
        }
        cursor.close();
        return list;
    }

    private Cursor queryCursor(String deviceMac) {
        SQLiteDatabase db = getReadableDatabase();
        Toast.makeText(context, DEVICE_MAC_ADDRESS, Toast.LENGTH_SHORT).show();
        return db.rawQuery("SELECT * FROM messages WHERE " + DEVICE_MAC_ADDRESS + "=?", new String[]{deviceMac});
    }


}
