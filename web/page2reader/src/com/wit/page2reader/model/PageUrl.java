package com.wit.page2reader.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.wit.base.BaseConstants;
import com.wit.page2reader.P2rConstants;

public class PageUrl {

    private Entity entity;

    public PageUrl(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        this.entity = entity;
    }

    public PageUrl(Key p2rGrpKey, String pUrl) {
        if (p2rGrpKey == null || pUrl == null || pUrl.isEmpty()) {
            throw new IllegalArgumentException();
        }

        entity = new Entity(PageUrl.class.getSimpleName(), p2rGrpKey);
        entity.setUnindexedProperty(P2rConstants.P_URL, pUrl);
        entity.setProperty(BaseConstants.SERVER_CREATE_DATE, new Date());

        setTitle(P2rConstants.PROCESSING_TITLE);
        setText(P2rConstants.PROCESSING_TEXT);
        setCleansedPage(P2rConstants.PROCESSING_CLEANSED_PAGE);
        setSentCount(0);
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(P2rConstants.KEY_STRING, getKeyString());
        jsonObject.put(P2rConstants.P_URL, getPUrl());
        jsonObject.put(BaseConstants.SERVER_CREATE_TIME_MILLIS, getServerCreateTimeMillis());
        jsonObject.put(P2rConstants.TITLE, getTitle());
        jsonObject.put(P2rConstants.TEXT, getText());
        // Save bandwidth
        //jsonObject.put(P2rConstants.CLEANSED_PAGE, getCleansedPage());
        jsonObject.put(P2rConstants.SENT_COUNT, getSentCount());
        return jsonObject;
    }

    public Entity getEntity() {
        return entity;
    }

    public Key getKey() {
        return entity.getKey();
    }

    public String getKeyString() {
        return KeyFactory.keyToString(entity.getKey());
    }

    public String getPUrl() {
        return (String)entity.getProperty(P2rConstants.P_URL);
    }

    public long getServerCreateTimeMillis() {
        return ((Date)entity.getProperty(BaseConstants.SERVER_CREATE_DATE)).getTime();
    }

    public String getTitle() {
        return (String)entity.getProperty(P2rConstants.TITLE);
    }

    public String getText() {
        return (String)entity.getProperty(P2rConstants.TEXT);
    }

    public String getCleansedPage() {
        Text t = (Text)entity.getProperty(P2rConstants.CLEANSED_PAGE);
        return t.getValue();
    }

    public int getSentCount() {
        return ((Number)entity.getProperty(P2rConstants.SENT_COUNT)).intValue();
    }

    public void setTitle(String title) {
        entity.setUnindexedProperty(P2rConstants.TITLE, title);
    }

    public void setText(String text) {
        entity.setUnindexedProperty(P2rConstants.TEXT, text);
    }

    public void setCleansedPage(String cleansedPage) {
        entity.setUnindexedProperty(P2rConstants.CLEANSED_PAGE, new Text(cleansedPage));
    }

    public void setSentCount(int count) {
        entity.setUnindexedProperty(P2rConstants.SENT_COUNT, count);
    }
}
