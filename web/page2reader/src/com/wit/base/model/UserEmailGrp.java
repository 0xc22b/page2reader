package com.wit.base.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class UserEmailGrp {

    private Entity entity;
    
    public UserEmailGrp(Key userEmailGrpKey) {
        entity = new Entity(userEmailGrpKey);
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public static Key createKey(String keyName) {
        // Lowercase only
        if (keyName == null || !keyName.equals(keyName.toLowerCase())) {
            throw new IllegalArgumentException();
        }
        
        return KeyFactory.createKey(
                UserEmailGrp.class.getSimpleName(), keyName);
    }
}
