package com.wit.base;

import com.wit.base.Log;
import com.wit.base.model.UserEmail;
import com.wit.base.model.UserUname;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

public class UserVerifier {

    public static boolean isUsernameValid(String username, boolean checkDuplicate,
            Log log) {
        
        if (username == null) {
            throw new IllegalArgumentException(username);
        }
        
        if (username.isEmpty()) {
            log.addLogInfo(BaseConstants.USERNAME, false, username, BaseConstants.ERR_EMPTY);
            return false;
        }
        
        if (username.length() < 2 || username.length() > 30) {
            log.addLogInfo(BaseConstants.USERNAME, false, username, BaseConstants.ERR_NAME_LENGTH);
            return false;
        }
        
        //Loop every character, allow only letters, numbers, and _. No space.
        for (int i = 0; i < username.length(); i++) {
            CharSequence s = username.subSequence(i, i + 1);
            if(!BaseConstants.USERNAME_ALLOWED_CHARACTERS.contains(s)) {
                log.addLogInfo(BaseConstants.USERNAME, false, username,
                        BaseConstants.ERR_NAME_CHARACTERS);
                return false;
            }
        }
        
        // No need to check if duplicate for logging in.
        if (checkDuplicate) {
            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            
            // Check if username is duplicate. Compare it with lowercase.
            String lowerCaseUsername = username.toLowerCase();
            
            Key userUnameGrpKey = UserManager.getUserUnameGrpKey(lowerCaseUsername);
            Key userUnameKey = UserUname.createKey(userUnameGrpKey,
                    lowerCaseUsername);
            
            try {
                ds.get(userUnameKey);
                log.addLogInfo(BaseConstants.USERNAME, false, username, BaseConstants.ERR_NAME_TAKEN);
                return false;
            } catch (EntityNotFoundException e) {
                
            }
        }
        
        log.addLogInfo(BaseConstants.USERNAME, true, username, null);
        return true;
    }
    
    public static boolean isEmailValid(String email, boolean checkDuplicate,
            Log log) {
        if (email == null) {
            throw new IllegalArgumentException(email);
        }
        
        if (email.isEmpty()){
            log.addLogInfo(BaseConstants.EMAIL, false, email, BaseConstants.ERR_EMPTY);
            return false;
        }

        //No space
        if(email.contains(" ")) {
            log.addLogInfo(BaseConstants.EMAIL, false, email, BaseConstants.ERR_EMAIL_SPACE);
            return false;
        }
        
        if (email.length() < 5) {
            log.addLogInfo(BaseConstants.EMAIL, false, email, BaseConstants.ERR_EMAIL_FORMAT);
            return false;
        }

        //Loop every character, only allowed characters.
        for (int i = 0; i < email.length(); i++) {
            CharSequence s = email.subSequence(i, i + 1);
            if(!BaseConstants.EMAIL_ALLOWED_CHARACTERS.contains(s)) {
                log.addLogInfo(BaseConstants.EMAIL, false, email, BaseConstants.ERR_EMAIL_FORMAT);
                return false;
            }
        }
        
        if (!email.contains("@")) {
            log.addLogInfo(BaseConstants.EMAIL, false, email, BaseConstants.ERR_EMAIL_FORMAT);
            return false;
        }
        
        String x = email.substring(email.indexOf("@") + 1);
        
        if (x.contains("@")) {
            log.addLogInfo(BaseConstants.EMAIL, false, email, BaseConstants.ERR_EMAIL_FORMAT);
            return false;
        }
        
        if (!x.contains(".")){
            log.addLogInfo(BaseConstants.EMAIL, false, email, BaseConstants.ERR_EMAIL_FORMAT);
            return false;
        }
        
        // example.com, example.co.th
        if (x.charAt(0) == '.' || x.charAt(x.length() - 1) == '.') {
            log.addLogInfo(BaseConstants.EMAIL, false, email, BaseConstants.ERR_EMAIL_FORMAT);
            return false;
        }
        
        // No need to check if duplicate for logging in.
        if (checkDuplicate) {
            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            
            Key userEmailGrpKey = UserManager.getUserEmailGrpKey(email);
            Key userEmailKey = UserEmail.createKey(userEmailGrpKey, email);
            
            try {
                ds.get(userEmailKey);
                log.addLogInfo(BaseConstants.EMAIL, false, email, BaseConstants.ERR_EMAIL_TAKEN);
                return false;
            } catch (EntityNotFoundException e) {
                
            }
        }
        
        log.addLogInfo(BaseConstants.EMAIL, true, email, null);
        return true;
    }
    
    public static boolean isPasswordValid(String password, Log log, String type) {
        if (password == null) {
            throw new IllegalArgumentException(password);
        }
        
        if (password.isEmpty()) {
            log.addLogInfo(type, false, null, BaseConstants.ERR_EMPTY);
            return false;
        }
        
        //No space
        if(password.contains(" ")) {
            log.addLogInfo(type, false, null, BaseConstants.ERR_PASSWORD_SPACE);
            return false;
        }
        
        if (password.length() < 7) {
            log.addLogInfo(type, false, null, BaseConstants.ERR_PASSWORD_LENGTH);
            return false;
        }
        
        log.addLogInfo(type, true, null, null);
        return true;
    }
    
    public static boolean isRepeatPasswordValid(String password,
            String repeatPassword, Log log) {

        if (password == null || repeatPassword == null) {
            throw new IllegalArgumentException(password);
        }
        
        if (!password.equals(repeatPassword)) {
            log.addLogInfo(BaseConstants.REPEAT_PASSWORD, false, null, BaseConstants.ERR_REPEAT_PASSWORD);
            return false;
        }
        
        log.addLogInfo(BaseConstants.REPEAT_PASSWORD, true, null, null);
        return true;
    }
}
