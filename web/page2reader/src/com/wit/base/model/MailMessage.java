package com.wit.base.model;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.wit.base.BaseConstants;

public class MailMessage {

    private Entity entity;

    public MailMessage(String sender, String recipients, String subject,
            String content, Date sentDate) {
        entity = new Entity(MailMessage.class.getSimpleName());
        entity.setUnindexedProperty(BaseConstants.SENDER, sender);
        entity.setUnindexedProperty(BaseConstants.RECIPIENTS, recipients);
        entity.setUnindexedProperty(BaseConstants.SUBJECT, subject);
        entity.setUnindexedProperty(BaseConstants.CONTENT, content);
        entity.setUnindexedProperty(BaseConstants.SENT_DATE, sentDate);
    }

    public MailMessage(Entity entity) {
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

    public String getSender() {
        return (String)entity.getProperty(BaseConstants.SENDER);
    }

    public String getRecipients() {
        return (String)entity.getProperty(BaseConstants.RECIPIENTS);
    }

    public String getSubject() {
        return (String)entity.getProperty(BaseConstants.SUBJECT);
    }

    public String getContent() {
        return (String)entity.getProperty(BaseConstants.CONTENT);
    }

    public Date getSentDate() {
        return (Date)entity.getProperty(BaseConstants.SENT_DATE);
    }
}
