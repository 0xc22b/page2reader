package com.wit.page2reader.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.wit.page2reader.Constants;

public class UserObj {

    public String sSID;
    public String sID;
    public String username;
    public String email;
    public long serverUpdateTimeMillis;

    public UserObj() {

    }

    public UserObj(Cursor cursor) {
        this.sSID = cursor.getString(0);
        this.sID = cursor.getString(1);
        this.username = cursor.getString(2);
        this.email = cursor.getString(3);
        this.serverUpdateTimeMillis = cursor.getLong(4);
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Constants.SSID, sSID);
        values.put(Constants.SID, sID);
        values.put(Constants.USERNAME, username);
        values.put(Constants.EMAIL, email);
        values.put(Constants.SERVER_UPDATE_TIME_MILLIS, serverUpdateTimeMillis);
        return values;
    }
}
