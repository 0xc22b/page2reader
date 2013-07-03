package com.wit.page2reader.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.wit.base.model.User;

public class P2rGrp {

    private Entity entity;

    public P2rGrp(Key p2rGrpKey) {
        entity = new Entity(p2rGrpKey);
    }

    public Entity getEntity() {
        return entity;
    }

    public static Key createKey(User user) {
        return KeyFactory.createKey(P2rGrp.class.getSimpleName(),
                user.getKey().getId());
    }
}