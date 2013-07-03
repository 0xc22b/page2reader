package com.wit.base.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.wit.base.BaseConstants;

public class UserUname {

    private Entity entity;

    public UserUname(Key userUnameKey, String username) {
        entity = new Entity(userUnameKey);
        entity.setProperty(BaseConstants.USERNAME, username);
    }

    public UserUname(Entity entity) {
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

    public Key getUserKey() {
        return (Key)entity.getProperty(BaseConstants.USER_KEY);
    }

    public String getUserKeyString() {
        return KeyFactory.keyToString(getUserKey());
    }

    public String getUsername() {
        return (String)entity.getProperty(BaseConstants.USERNAME);
    }

    public void setUserKey(Key userKey) {
        entity.setProperty(BaseConstants.USER_KEY, userKey);
    }

    public static Key createKey(Key userUnameGrpKey, String lowerCaseUsername) {
        // Lowercase only
        if (userUnameGrpKey == null || lowerCaseUsername == null ||
                !lowerCaseUsername.equals(lowerCaseUsername.toLowerCase())) {
            throw new IllegalArgumentException();
        }

        return KeyFactory.createKey(userUnameGrpKey,
                UserUname.class.getSimpleName(), lowerCaseUsername);
    }
}
