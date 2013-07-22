package com.wit.page2reader.model;

import org.json.JSONObject;

import com.wit.page2reader.Constants;

public class PageUrlObj {

    public String keyString;
    public String pUrl;
    public long serverCreateTimeMillis;
    public String title;
    public String text;
    public int sentCount;

    public PageUrlObj(JSONObject jsonObject) {
        this.keyString = jsonObject.getString(Constants.KEY_STRING);
        this.pUrl = jsonObject.getString(Constants.P_URL);
        this.serverCreateTimeMillis = jsonObject.getLong(Constants.SERVER_CREATE_TIME_MILLIS);
        this.title = jsonObject.getString(Constants.TITLE);
        this.text = jsonObject.getString(Constants.TEXT);
        this.sentCount = jsonObject.getInt(Constants.SENT_COUNT);
    }
}
