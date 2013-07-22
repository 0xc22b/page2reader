package com.wit.page2reader;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public class Constants {

    public static final String USER = "user";

    public static final String KEY_STRING = "keyString";

    public static final String SSID = "ssid";
    public static final String SID = "sid";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";

    public static final String P_URL = "pUrl";
    public static final String TITLE = "title";
    public static final String TEXT = "text";
    public static final String SENT_COUNT = "sentCount";

    public static final String SERVER_CREATE_TIME_MILLIS = "serverCreateTimeMillis";
    public static final String SERVER_UPDATE_TIME_MILLIS = "serverUpdateTimeMillis";

    public static final String CURSOR_STRING = "cursorString";

    public static final String TYPE = "type";
    public static final String IS_VALID = "isValid";
    public static final String VALUE = "value";
    public static final String MSG = "msg";

    public static final String URL(Context context) {
        boolean isDebug = (context.getApplicationInfo().flags &
                ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        if (isDebug) {
            //return "http://10.0.2.2:8888";  // localhost
            return "http://192.168.1.36:8888";
        } else {
            return "http://page2reader.appspot.com";
        }
    }

    public static final String LOG_OUT_PATH = "/logout";
    public static final String P2R_PATH = "/p2r";
    public static final String USER_PATH = "/user";

    public static final String LOG_IN = "logIn";
    public static final String LOG_IN_AND_GET_USER = "logInAndGetUser";
    public static final String LOG_OUT = "logOut";
    public static final String GET_PAGING_PAGE_URLS = "getPagingPageUrls";
    public static final String ADD_PAGE_URL = "addPageUrl";
    public static final String DELETE_PAGE_URL = "deletePageUrl";
    public static final String SEND_TO_READER = "sendToReader";
    public static final String RESEND_TO_READER = "resendToReader";

    public static final String PAGE_URLS = "pageUrls";

    public static final String NO_NETWORK = "No network connection available.";
    public static final String CONNECTION_FAILED = "Could not connect to the server.";
    public static final String NOT_LOGGED_IN = "Please go to the app and log in first.";
}
