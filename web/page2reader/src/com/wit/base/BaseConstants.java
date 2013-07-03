package com.wit.base;

public class BaseConstants {
    
    public static final String CONTENT = "content";

    public static final String TYPE = "type";
    public static final String IS_VALID = "isValid";
    public static final String VALUE = "value";
    public static final String MSG = "msg";
    public static final String ERR_MSG = "errMsg";

    public static final String KEY_NAME = "keyName";
    public static final String STATUS = "status";
    
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_DELETED = 1;

    public static final String SERVER_CREATE_DATE = "serverCreateDate";
    public static final String SERVER_UPDATE_DATE = "serverUpdateDate";
    public static final String CLIENT_CREATE_DATE = "clientCreateDate";
    public static final String CLIENT_UPDATE_DATE = "clientUpdateDate";
    
    public static final String SERVER_CREATE_TIME_MILLIS = "serverCreateTimeMillis";
    public static final String SERVER_UPDATE_TIME_MILLIS = "serverUpdateTimeMillis";
    public static final String CLIENT_CREATE_TIME_MILLIS = "clientCreateTimeMillis";
    public static final String CLIENT_UPDATE_TIME_MILLIS = "clientUpdateTimeMillis";
    
    public static final int ALL_FIELDS = 0;
    public static final int UPDATED_FIELDS = 1;

    public static final String WEB_SAFE_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
    public static final String USERNAME_ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
    public static final String EMAIL_ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789-_.@";
    
    public static final String SSID = "ssid";
    public static final String SID = "sid";
    public static final String ST = "st";
    public static final String FID = "fid";
    public static final String VID = "vid";

    public static final String USER_UNAME_GRP_KEY = "userUnameGrpKey";
    public static final String USER_EMAIL_GRP_KEY = "userEmailGrpKey";

    public static final String USER_KEY = "userKey";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String HASH_PASSWORD = "hashPassword";
    public static final String REPEAT_PASSWORD = "repeatPassword";
    public static final String NEW_PASSWORD = "newPassword";
    public static final String LANG = "lang";
    public static final String DID_CONFIRM_EMAIL = "didConfirmEmail";

    public static final String SESSION_ID = "sessionID";
    public static final String IS_SESSION_VALID = "isSessionValid";

    public static final String SIGN_UP = "signUp";
    public static final String LOG_IN = "logIn";
    public static final String CHANGE_USERNAME = "changeUsername";
    public static final String CHANGE_EMAIL = "changeEmail";
    public static final String CHANGE_PASSWORD = "changePassword";
    public static final String CHANGE_LANG = "changeLang";
    public static final String DELETE_ACCOUNT = "deleteAccount";
    public static final String SEND_EMAIL_RESET_PASSWORD = "sendEmailResetPassword";
    public static final String RESET_PASSWORD = "resetPassword";
    public static final String RESEND_EMAIL_CONFIRM = "resendEmailConfirm";
    public static final String DID_LOG_IN = "didLogIn";

    // Error messages
    public static final String ERR_LOG_IN = "Your username or password are not correct.";
    public static final String ERR_NAME_TAKEN = "Someone already has that username. Please try another.";
    public static final String ERR_EMAIL_TAKEN = "Someone already has that email. Please try another.";
    public static final String ERR_PASSWORD = "Password is not correct.";
    public static final String ERR_USER_NOT_FOUND = "This username or email is not available.";
    public static final String ERR_EMPTY = "This field is required.";
    public static final String ERR_NAME_LENGTH = "Please use between 2 and 30 characters.";
    public static final String ERR_NAME_CHARACTERS = "Please use only letters (a-z, A-Z), numbers, and _.";
    public static final String ERR_EMAIL_SPACE = "Email can not contain space.";
    public static final String ERR_EMAIL_FORMAT = "Email must contain @ and domain name i.e. @example.com";
    public static final String ERR_PASSWORD_LENGTH = "Short passwords are easy to guess. Try one with at least 7 characters.";
    public static final String ERR_PASSWORD_SPACE = "Password can not contain space.";
    public static final String ERR_REPEAT_PASSWORD = "These passwords don't match. Please try again.";

    // Email message
    public static final String SENDER = "sender";
    public static final String RECIPIENTS = "recipients";
    public static final String SUBJECT = "subject";
    //public static final String CONTENT = "content";
    public static final String SENT_DATE = "sentDate";

    // Reset password messages
    public static final String RESET_PASSWORD_SUCCESS =
            "Your password has been reset. Please login.";
    public static final String RESET_PASSWORD_FAILURE =
            "Could not find your account. Please request to reset password again.";
    public static final String RESET_PASSWORD_SESSION_TOO_OLD =
            "Your request was too old. Please request to reset password again";

    public static final String CONCURRENT_MODIFICATION_EXCEPTION =
            "The servers are really busy right now. Please try again in minutes.";
}
