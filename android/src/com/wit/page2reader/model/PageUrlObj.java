package com.wit.page2reader.model;

import java.util.ArrayList;

import org.json.JSONObject;

import com.wit.page2reader.Constants;
import com.wit.page2reader.Constants.DelBtnStatus;
import com.wit.page2reader.Constants.ResendBtnStatus;

public class PageUrlObj {

    public String keyString;
    public String pUrl;
    public long serverCreateTimeMillis;
    public String title;
    public String text;
    public int sentCount;

    public DelBtnStatus delBtnStatus = DelBtnStatus.NORMAL;
    public ResendBtnStatus resendBtnStatus = ResendBtnStatus.NORMAL;

    public PageUrlObj(JSONObject jsonObject) {
        this.keyString = jsonObject.getString(Constants.KEY_STRING);
        this.pUrl = jsonObject.getString(Constants.P_URL);
        this.serverCreateTimeMillis = jsonObject.getLong(Constants.SERVER_CREATE_TIME_MILLIS);
        this.title = jsonObject.getString(Constants.TITLE);
        this.text = jsonObject.getString(Constants.TEXT);
        this.sentCount = jsonObject.getInt(Constants.SENT_COUNT);
    }

    public static PageUrlObj getPageUrl(ArrayList<PageUrlObj> pageUrls, String keyString) {
        for (PageUrlObj pageUrl : pageUrls) {
            if (pageUrl.keyString.equals(keyString)) {
                return pageUrl;
            }
        }
        return null;
    }
}
