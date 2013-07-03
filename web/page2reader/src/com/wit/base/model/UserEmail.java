package com.wit.base.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.wit.base.BaseConstants;

public class UserEmail {

    private Entity entity;

    public UserEmail(Key userEmailKey) {
        entity = new Entity(userEmailKey);
    }

    public UserEmail(Entity entity) {
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

    public String getKeyName() {
        return entity.getKey().getName();
    }

    public String getKeyString() {
        return KeyFactory.keyToString(entity.getKey());
    }

    public Key getUserKey() {
        return (Key)entity.getProperty(BaseConstants.USER_KEY);
    }

    public String getUserKeyString() {
        return KeyFactory.keyToString(getUserKey());
    }

    public void setUserKey(Key userKey) {
        entity.setProperty(BaseConstants.USER_KEY, userKey);
    }

    public static Key createKey(Key userEmailGrpKey, String email) {
        // Lowercase only
        if (userEmailGrpKey == null || email == null ||
                !email.equals(email.toLowerCase())) {
            throw new IllegalArgumentException();
        }

        return KeyFactory.createKey(userEmailGrpKey,
                UserEmail.class.getSimpleName(), email);
    }
}
