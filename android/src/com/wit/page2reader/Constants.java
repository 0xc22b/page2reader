package com.wit.page2reader;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public class Constants {

    public enum RefreshBtnStatus {
        NORMAL,
        REFRESHING
    }

    public enum DelBtnStatus {
        NORMAL,
        DELETING
    }

    public enum ResendBtnStatus {
        NORMAL,
        RESENDING,
        DISABLED,
        SENT
    }

    public enum NextBtnStatus {
        NORMAL,
        GETTING,
        HIDE
    }

    public static final int LOG_IN_LOADER_ID = 1;
    public static final int UPDATE_USER_LOADER_ID = 2;
    public static final int LOG_OUT_LOADER_ID = 3;

    public static final int HOME_FETCH_USER_LOADER_ID = 4;
    public static final int P2R_FETCH_USER_LOADER_ID = 5;

    public static final int FT_GET_PAGE_URLS_LOADER_ID = 6;
    public static final int RF_GET_PAGE_URLS_LOADER_ID = 7;
    public static final int NE_GET_PAGE_URLS_LOADER_ID = 8;

    public static final int ADD_PAGE_URL_LOADER_ID = 9;
    public static final int DELETE_PAGE_URL_LOADER_ID = 10;
    public static final int RESEND_TO_READER_LOADER_ID = 11;

    public static final String USER = "user";

    public static final String KEY_STRING = "keyString";

    public static final String SSID = "ssid";
    public static final String SID = "sid";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";

    public static final String PAGE_URLS = "pageUrls";

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

    public static final String URL_STRING = "urlString";

    public static final String URL(Context context) {
        boolean isDebug = (context.getApplicationInfo().flags &
                ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        if (isDebug) {
            return "http://10.0.2.2:8888";  // localhost
            //return "http://192.168.1.38:8888";
            //return "http://192.168.43.43:8888";
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

    public static final String DID_GET_SESSION = "didGetSession";
    public static final String BEING_DELETED_POSITION = "beingDeletedPosition";
}
