package com.wit.base.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class UserUnameGrp {

    private Entity entity;
    
    public UserUnameGrp(Key userUnameGrpKey) {
        entity = new Entity(userUnameGrpKey);
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public static Key createKey(String keyName) {
        // Lowercase only
        if (keyName == null || !keyName.equals(keyName.toLowerCase())) {
            throw new IllegalArgumentException();
        }
        
        return KeyFactory.createKey(UserUnameGrp.class.getSimpleName(), keyName);
    }
}
