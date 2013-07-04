package com.wit.base.model;

import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.wit.base.BaseConstants;

@SuppressWarnings("serial")
public class User implements Serializable {

    private Entity entity;

    public User(){

    }

    public User(Key userUnameGrpKey, Key userEmailGrpKey, String hashPassword) {
        entity = new Entity(User.class.getSimpleName());
        entity.setUnindexedProperty(BaseConstants.USER_UNAME_GRP_KEY, userUnameGrpKey);
        entity.setUnindexedProperty(BaseConstants.USER_EMAIL_GRP_KEY, userEmailGrpKey);
        entity.setUnindexedProperty(BaseConstants.HASH_PASSWORD, hashPassword);
        entity.setUnindexedProperty(BaseConstants.DID_CONFIRM_EMAIL, false);
        entity.setUnindexedProperty(BaseConstants.LANG, "en");
        entity.setUnindexedProperty(BaseConstants.SERVER_CREATE_DATE, new Date());
    }

    public User(Entity entity) {
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

    public Key getUserUnameGrpKey() {
        return (Key)entity.getProperty(BaseConstants.USER_UNAME_GRP_KEY);
    }

    public Key getUserEmailGrpKey() {
        return (Key)entity.getProperty(BaseConstants.USER_EMAIL_GRP_KEY);
    }

    public String getHashPassword() {
        return (String)entity.getProperty(BaseConstants.HASH_PASSWORD);
    }

    public boolean didConfirmEmail() {
        return (Boolean)entity.getProperty(BaseConstants.DID_CONFIRM_EMAIL);
    }

    public String getLang() {
        return (String)entity.getProperty(BaseConstants.LANG);
    }

    public Date getCreateDate() {
        return (Date)entity.getProperty(BaseConstants.SERVER_CREATE_DATE);
    }

    public void setUserUnameGrpKey(Key key) {
        entity.setUnindexedProperty(BaseConstants.USER_UNAME_GRP_KEY, key);
    }

    public void setUserEmailGrpKey(Key key) {
        entity.setUnindexedProperty(BaseConstants.USER_EMAIL_GRP_KEY, key);
    }

    public void setPassword(String hashPassword) {
        entity.setUnindexedProperty(BaseConstants.HASH_PASSWORD, hashPassword);
    }

    public void setConfirmEmail(boolean didConfirmEmail) {
        entity.setUnindexedProperty(BaseConstants.DID_CONFIRM_EMAIL, didConfirmEmail);
    }

    public void setLang(String lang) {
        entity.setUnindexedProperty(BaseConstants.LANG, lang);
    }
}
