package com.wit.page2reader.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.wit.page2reader.Constants;

public class DataStore {

    // Used for debugging and logging
    public static final String TAG = "DataStore";

    public static final String DATABASE_NAME = "page2reader.db";
    public static final int DATABASE_VERSION = 1;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Creates the underlying database with table names and column names
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + Constants.USER + " ("
                    + Constants.SSID + " TEXT PRIMARY KEY,"
                    + Constants.SID + " TEXT,"
                    + Constants.USERNAME + " TEXT,"
                    + Constants.EMAIL + " TEXT,"
                    + Constants.SERVER_UPDATE_TIME_MILLIS + " INTEGER"
                    + ");");
        }

        /**
         * Demonstrates that the provider must consider what happens when the
         * underlying datastore is changed. In this sample, the database is
         * upgraded the database by destroying the existing data. A real
         * application should upgrade the database in place.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // Logs that the database is being upgraded
            android.util.Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            // Kills the table and existing data
            db.execSQL("DROP TABLE IF EXISTS " + Constants.USER);

            // Recreates the database with a new version
            onCreate(db);
        }

    }

    private static DataStore instance;

    public static DataStore getInstance(Context context) {
        if (instance == null) {
            instance = new DataStore(context);
        }
        return instance;
    }

    public String sSID;
    public String sID;
    public ArrayList<PageUrlObj> pageUrls = new ArrayList<PageUrlObj>();
    public String cursorString;

    private DatabaseHelper databaseHelper;

    private DataStore(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public interface FetchUserCallback {
        public void onFetchUserCallback(String sSID, String sID,
                String username, String email);
    }

    public interface UpdateUserCallback {
        public void onUpdateUserCallback(boolean success, String msg);
    }

    private UserObj fetchUser() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(Constants.USER, null, null, null, null, null, null);
        try {
            if (cursor.getCount() == 0) {
                return null;
            } else if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                UserObj userObj = new UserObj(cursor);
                return userObj;
            } else {
                // Throws an error
                throw new AssertionError("There are users more than 1.");
            }
        } finally {
            cursor.close();
        }
    }

    public void fetchUser(FetchUserCallback callback) {
        UserObj userObj = fetchUser();
        if (userObj == null) {
            callback.onFetchUserCallback(null, null, null, null);
        } else {
            callback.onFetchUserCallback(userObj.sSID, userObj.sID,
                    userObj.username, userObj.email);
        }
    }

    public void updateUser(String sSID, String sID, String username, String email,
            UpdateUserCallback callback) {
        deleteUser();

        UserObj userObj = new UserObj();
        userObj.sSID = sSID;
        userObj.sID = sID;
        userObj.username = username;
        userObj.email = email;

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        long rowId = db.insert(Constants.USER, null, userObj.getContentValues());
        if (rowId > 0) {
            callback.onUpdateUserCallback(true, null);
        } else {
            // If the insert didn't succeed, then the rowID is <= 0. Throws
            // an exception.
            throw new SQLException("Failed to insert a new user.");
        }
    }

    public void deleteUser() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(Constants.USER, null, null);
    }

    public interface LogInCallback {
        public void onLogInCallback(Log log);
    }

    public AsyncTask<String, Void, Log> logIn(Activity activity, String username, String password,
            final LogInCallback callback) {

        // TODO: Validate the inputs


        if (isNetworkConnected(activity)) {

            String urlString = Constants.URL(activity.getApplicationContext());

            return new AsyncTask<String, Void, Log>() {

                @Override
                protected Log doInBackground(String... params) {
                    try {
                        return logInInBackground(params[0], params[1], params[2]);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Log log) {
                    callback.onLogInCallback(log);
                }

                @Override
                protected void onCancelled() {
                    callback.onLogInCallback(null);
                }
            }.execute(urlString, username, password);
        } else {
            Log log = new Log();
            log.addLogInfo(Constants.LOG_IN, false, null, Constants.NO_NETWORK);
            callback.onLogInCallback(log);
            return null;
        }
    }

    private Log logInInBackground(String urlString, String username, String password)
            throws IOException {

        JSONObject jsonObj = new JSONObject();
        jsonObj.put(Constants.USERNAME, username);
        jsonObj.put(Constants.PASSWORD, password);

        String postContent = "method=" + Constants.LOG_IN_AND_GET_USER
                + "&content=" + jsonObj.toString();

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            return makeRequest(conn, postContent);
        } finally {
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            conn.disconnect();
        }
    }

    public interface LogOutCallback {
        public void onLogOutCallback(Log log);
    }

    public AsyncTask<String, Void, Log> logOut(Activity activity, String sSID, String sID,
            final LogOutCallback callback) {
        if (isNetworkConnected(activity)) {
            String urlString = Constants.URL(activity.getApplicationContext())
                    + Constants.LOG_OUT_PATH;
            return new AsyncTask<String, Void, Log>() {

                @Override
                protected Log doInBackground(String... params) {
                    try {
                        return logOutInBackground(params[0], params[1], params[2]);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Log log) {
                    callback.onLogOutCallback(log);
                }

                @Override
                protected void onCancelled() {
                    callback.onLogOutCallback(null);
                }
            }.execute(urlString, sSID, sID);
        } else {
            Log log = new Log();
            log.addLogInfo(Constants.LOG_OUT, false, null, Constants.NO_NETWORK);
            callback.onLogOutCallback(log);
            return null;
        }
    }

    private Log logOutInBackground(String urlString, String sSID, String sID)
            throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setDoInput(true);
            conn.setReadTimeout(60000 /* milliseconds */);
            conn.setConnectTimeout(60000 /* milliseconds */);

            // Add sSID, sID to cookie
            String cookie = "ssid=" + sSID + "; sid=" + sID;
            conn.setRequestProperty("Cookie", cookie);

            // Starts the query
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode / 100 == 2) {
                Log log = new Log();
                log.addLogInfo(Constants.LOG_OUT, true, null, null);
                return log;
            } else {
                return null;
            }
        } finally {
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            conn.disconnect();
        }
    }

    public interface GetPagingPageUrlsCallback {
        public void onGetPagingPageUrlsCallback(Log log);
    }

    public AsyncTask<String, Void, Log> getPagingPageUrls(Activity activity, String sSID,
            String sID, String cursorString, final GetPagingPageUrlsCallback callback) {

        if (isNetworkConnected(activity)) {

            String urlString = Constants.URL(activity.getApplicationContext()) + Constants.P2R_PATH;

            return new AsyncTask<String, Void, Log>() {

                @Override
                protected Log doInBackground(String... params) {
                    try {
                        return getPagingPageUrlsInBackground(params[0], params[1], params[2],
                                params[3]);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Log log) {
                    callback.onGetPagingPageUrlsCallback(log);
                }

                @Override
                protected void onCancelled() {
                    callback.onGetPagingPageUrlsCallback(null);
                }
            }.execute(urlString, sSID, sID, cursorString);
        } else {
            Log log = new Log();
            log.addLogInfo(Constants.GET_PAGING_PAGE_URLS, false, null, Constants.NO_NETWORK);
            callback.onGetPagingPageUrlsCallback(log);
            return null;
        }
    }

    private Log getPagingPageUrlsInBackground(String urlString, String sSID, String sID,
            String cursorString) throws IOException {

        cursorString = cursorString == null ? "null" : cursorString;
        String postContent = "method=" + Constants.GET_PAGING_PAGE_URLS
                + "&content=" + cursorString;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            // Add sSID, sID to cookie
            String cookie = "ssid=" + sSID + "; sid=" + sID;
            conn.setRequestProperty("Cookie", cookie);

            return makeRequest(conn, postContent);
        } finally {
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            conn.disconnect();
        }
    }

    public interface AddPageUrlCallback {
        public void onAddPageUrlCallback(Log log);
    }

    public AsyncTask<String, Void, Log> addPageUrl(Context applicationContext, String sSID,
            String sID, String pUrl, final AddPageUrlCallback callback) {

        if (isNetworkConnected(applicationContext)) {

            String urlString = Constants.URL(applicationContext) + Constants.P2R_PATH;

            return new AsyncTask<String, Void, Log>() {

                @Override
                protected Log doInBackground(String... params) {
                    try {
                        return addPageUrlInBackground(params[0], params[1], params[2],
                                params[3]);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Log log) {
                    callback.onAddPageUrlCallback(log);
                }

                @Override
                protected void onCancelled() {
                    callback.onAddPageUrlCallback(null);
                }
            }.execute(urlString, sSID, sID, pUrl);
        } else {
            Log log = new Log();
            log.addLogInfo(Constants.ADD_PAGE_URL, false, null, Constants.NO_NETWORK);
            callback.onAddPageUrlCallback(log);
            return null;
        }
    }

    private Log addPageUrlInBackground(String urlString, String sSID, String sID,
            String pUrl) throws IOException {

        String postContent = "method=" + Constants.ADD_PAGE_URL
                + "&content=" + pUrl;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            // Add sSID, sID to cookie
            String cookie = "ssid=" + sSID + "; sid=" + sID;
            conn.setRequestProperty("Cookie", cookie);

            return makeRequest(conn, postContent);
        } finally {
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            conn.disconnect();
        }
    }

    public interface DeletePageUrlCallback {
        public void onDeletePageUrlCallback(Log log);
    }

    public AsyncTask<String, Void, Log> deletePageUrl(Activity activity, String sSID,
            String sID, String keyString, final DeletePageUrlCallback callback) {

        if (isNetworkConnected(activity)) {

            String urlString = Constants.URL(activity.getApplicationContext()) + Constants.P2R_PATH;

            return new AsyncTask<String, Void, Log>() {

                @Override
                protected Log doInBackground(String... params) {
                    try {
                        return deletePageUrlInBackground(params[0], params[1], params[2],
                                params[3]);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Log log) {
                    callback.onDeletePageUrlCallback(log);
                }

                @Override
                protected void onCancelled() {
                    callback.onDeletePageUrlCallback(null);
                }
            }.execute(urlString, sSID, sID, keyString);
        } else {
            Log log = new Log();
            log.addLogInfo(Constants.DELETE_PAGE_URL, false, null, Constants.NO_NETWORK);
            callback.onDeletePageUrlCallback(log);
            return null;
        }
    }

    private Log deletePageUrlInBackground(String urlString, String sSID, String sID,
            String keyString) throws IOException {

        String postContent = "method=" + Constants.DELETE_PAGE_URL
                + "&content=" + keyString;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            // Add sSID, sID to cookie
            String cookie = "ssid=" + sSID + "; sid=" + sID;
            conn.setRequestProperty("Cookie", cookie);

            return makeRequest(conn, postContent);
        } finally {
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            conn.disconnect();
        }
    }

    public interface ResendToReaderCallback {
        public void onResendToReaderCallback(Log log);
    }

    public AsyncTask<String, Void, Log> resendToReader(Activity activity, String sSID,
            String sID, String keyString, final ResendToReaderCallback callback) {

        if (isNetworkConnected(activity)) {

            String urlString = Constants.URL(activity.getApplicationContext()) + Constants.P2R_PATH;

            return new AsyncTask<String, Void, Log>() {

                @Override
                protected Log doInBackground(String... params) {
                    try {
                        return resendToReaderInBackground(params[0], params[1], params[2],
                                params[3]);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Log log) {
                    callback.onResendToReaderCallback(log);
                }

                @Override
                protected void onCancelled() {
                    callback.onResendToReaderCallback(null);
                }
            }.execute(urlString, sSID, sID, keyString);
        } else {
            Log log = new Log();
            log.addLogInfo(Constants.SEND_TO_READER, false, null, Constants.NO_NETWORK);
            callback.onResendToReaderCallback(log);
            return null;
        }
    }

    private Log resendToReaderInBackground(String urlString, String sSID, String sID,
            String keyString) throws IOException {

        String postContent = "method=" + Constants.RESEND_TO_READER
                + "&content=" + keyString;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            // Add sSID, sID to cookie
            String cookie = "ssid=" + sSID + "; sid=" + sID;
            conn.setRequestProperty("Cookie", cookie);

            return makeRequest(conn, postContent);
        } finally {
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            conn.disconnect();
        }
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private Log makeRequest(HttpURLConnection conn, String postContent) throws IOException {
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setReadTimeout(60000 /* milliseconds */);
        conn.setConnectTimeout(60000 /* milliseconds */);

        OutputStream out = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        try {
            writer.write(postContent);
        } finally {
            writer.close();
            out.close();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode / 100 == 2) {
            InputStream in = conn.getInputStream();
            try {
                Reader reader = new InputStreamReader(in, "UTF-8");
                BufferedReader br = new BufferedReader(reader);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return new Log(sb.toString());
            } finally {
                in.close();
            }
        }
        return null;
    }
}
