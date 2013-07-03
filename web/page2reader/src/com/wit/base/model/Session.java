package com.wit.base.model;

import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.wit.base.BaseConstants;

@SuppressWarnings("serial")
public class Session implements Serializable {

    public static final int LOG_IN = 1;
    public static final int EMAIL_CONFIRM = 2;
    public static final int RESET_PASSWORD = 3;

    private Entity entity;

    public Session(int type, Key userKey, String sessionID) {

        if (userKey == null || sessionID == null) {
            throw new IllegalArgumentException();
        }

        entity = new Entity(Session.class.getSimpleName());
        entity.setProperty(BaseConstants.TYPE, type);
        entity.setProperty(BaseConstants.USER_KEY, userKey);
        entity.setUnindexedProperty(BaseConstants.SESSION_ID, sessionID);
        entity.setUnindexedProperty(BaseConstants.SERVER_CREATE_DATE, new Date());
    }

    public Session(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }

        this.entity = entity;
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

    public int getType() {
        // The value returned may not be the same type
        // as originally set via setProperty.
        return ((Number)entity.getProperty(BaseConstants.TYPE)).intValue();
    }

    public Key getUserKey() {
        return (Key)entity.getProperty(BaseConstants.USER_KEY);
    }

    public String getUserKeyString() {
        return KeyFactory.keyToString(getUserKey());
    }

    public String getSessionID() {
        return (String)entity.getProperty(BaseConstants.SESSION_ID);
    }

    public Date getCreateDate() {
        return (Date)entity.getProperty(BaseConstants.SERVER_CREATE_DATE);
    }
}
