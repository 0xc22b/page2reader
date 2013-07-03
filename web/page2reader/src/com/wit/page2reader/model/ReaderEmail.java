package com.wit.page2reader.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.wit.base.BaseConstants;
import com.wit.page2reader.P2rConstants;

public class ReaderEmail {

    private Entity entity;

    public ReaderEmail(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        this.entity = entity;
    }

    public ReaderEmail(Key readerEmailKey) {
        if (readerEmailKey == null) {
            throw new IllegalArgumentException();
        }

        this.entity = new Entity(readerEmailKey);
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(P2rConstants.KEY_STRING, getKeyString());
        jsonObject.put(P2rConstants.R_EMAIL, getREmail());
        jsonObject.put(BaseConstants.SERVER_UPDATE_TIME_MILLIS, getServerUpdateTimeMillis());
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

    public String getREmail() {
        return (String)entity.getProperty(P2rConstants.R_EMAIL);
    }

    public long getServerUpdateTimeMillis() {
        return ((Date)entity.getProperty(BaseConstants.SERVER_UPDATE_DATE)).getTime();
    }

    public void setREmail(String rEmail) {
        entity.setUnindexedProperty(P2rConstants.R_EMAIL, rEmail);
        entity.setUnindexedProperty(BaseConstants.SERVER_UPDATE_DATE, new Date());
    }

    public static Key createKey(Key p2rGrpKey) {
        if (p2rGrpKey == null) {
            throw new IllegalArgumentException();
        }
        return KeyFactory.createKey(p2rGrpKey, ReaderEmail.class.getSimpleName(),
                p2rGrpKey.getId());
    }
}
