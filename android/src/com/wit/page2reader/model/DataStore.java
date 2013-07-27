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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.wit.page2reader.Constants;
import com.wit.page2reader.Constants.NextBtnStatus;
import com.wit.page2reader.Constants.RefreshBtnStatus;
import com.wit.page2reader.R;

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

    public boolean didGetPageUrls;
    public String msg;
    public RefreshBtnStatus refreshBtnStatus = RefreshBtnStatus.NORMAL;
    public NextBtnStatus nextBtnStatus = NextBtnStatus.NORMAL;

    private DatabaseHelper databaseHelper;

    private DataStore(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    /**
     * issue 14944: initLoader() nor restartLoader() actually starts the loader
     * http://code.google.com/p/android/issues/detail?id=14944
     */
    public static abstract class DataStoreLoader<D> extends AsyncTaskLoader<D> {

        D mD;

        public DataStoreLoader(Context context) {
            super(context);
        }

        @Override
        public void deliverResult(D d) {
            if (isReset()) {
                return;
            }

            mD = d;

            super.deliverResult(d);

            // After delivery the result, stop reporting for updates
            stopLoading();
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (mD != null) {
                deliverResult(mD);
            }

            if (takeContentChanged() || mD == null) {
                forceLoad();
            }
        }

        @Override
        protected void onStopLoading() {
            super.onStopLoading();
            cancelLoad();
        }

        @Override
        public void onCanceled(D d) {
            super.onCanceled(d);
        }

        @Override
        protected void onReset() {
            super.onReset();

            onStopLoading();

            mD = null;
        }
    }

    public interface FetchUserCallback {
        public void onFetchUserCallback(String sSID, String sID,
                String username, String email);
    }

    public boolean reconnectFetchUser(final SherlockFragmentActivity activity, int loaderID,
            final FetchUserCallback callback) {
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        Loader<Object> loader = loaderManager.getLoader(loaderID);
        if (loader != null && loader.isStarted()) {
            FetchUserLoaderCallbacks loaderCallbacks = new FetchUserLoaderCallbacks(activity,
                    callback);
            loaderManager.initLoader(loaderID, null, loaderCallbacks);
            return true;
        } else {
            return false;
        }
    }

    public void fetchUser(final SherlockFragmentActivity activity, int loaderID,
            final FetchUserCallback callback) {
        fetchUser(activity, loaderID, callback, false);
    }

    public void fetchUserWithNoAsync(final FetchUserCallback callback) {
        fetchUser(null, 0, callback, true);
    }

    private void fetchUser(final SherlockFragmentActivity activity, int loaderID,
            final FetchUserCallback callback, boolean withNoAsync) {

        // Caveat: if withNoAsync == true, activity is null as it's called from a service.

        if (withNoAsync) {
            UserObj userObj = fetchUserInBackground();
            if (userObj == null) {
                callback.onFetchUserCallback(null, null, null, null);
            } else {
                callback.onFetchUserCallback(userObj.sSID, userObj.sID,
                        userObj.username, userObj.email);
            }
        } else {
            FetchUserLoaderCallbacks loaderCallbacks = new FetchUserLoaderCallbacks(activity,
                    callback);
            activity.getSupportLoaderManager().restartLoader(loaderID, null, loaderCallbacks);
        }
    }

    private class FetchUserLoaderCallbacks implements LoaderCallbacks<UserObj> {
        private Context mContext;
        private FetchUserCallback mCallback;

        public FetchUserLoaderCallbacks(Context context, FetchUserCallback callback) {
            this.mContext = context;
            this.mCallback = callback;
        }

        @Override
        public Loader<UserObj> onCreateLoader(int id, final Bundle bundle) {
            DataStoreLoader<UserObj> loader = new DataStoreLoader<UserObj>(mContext) {
                @Override
                public UserObj loadInBackground() {
                    return fetchUserInBackground();
                }
            };
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<UserObj> loader, UserObj data) {
            if (data == null) {
                mCallback.onFetchUserCallback(null, null, null, null);
            } else {
                mCallback.onFetchUserCallback(data.sSID, data.sID,
                        data.username, data.email);
            }
        }

        @Override
        public void onLoaderReset(Loader<UserObj> loader) {
        }
    }

    private UserObj fetchUserInBackground() {
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

    public interface UpdateUserCallback {
        public void onUpdateUserCallback(boolean success, String msg);
    }

    public boolean reconnectUpdateUser(final SherlockFragmentActivity activity, int loaderID,
            final UpdateUserCallback callback) {
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        Loader<Object> loader = loaderManager.getLoader(loaderID);
        if (loader != null && loader.isStarted()) {
            UpdateUserLoaderCallbacks loaderCallbacks = new UpdateUserLoaderCallbacks(activity,
                    callback);
            loaderManager.initLoader(loaderID, null, loaderCallbacks);
            return true;
        } else {
            return false;
        }
    }

    public void updateUser(final SherlockFragmentActivity activity, int loaderID, String sSID,
            String sID, String username, String email, final UpdateUserCallback callback) {

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SSID, sSID);
        bundle.putString(Constants.SID, sID);
        bundle.putString(Constants.USERNAME, username);
        bundle.putString(Constants.EMAIL, email);

        UpdateUserLoaderCallbacks loaderCallbacks = new UpdateUserLoaderCallbacks(activity,
                callback);
        activity.getSupportLoaderManager().restartLoader(loaderID, bundle, loaderCallbacks);
    }

    private class UpdateUserLoaderCallbacks implements LoaderCallbacks<Long> {
        private Context mContext;
        private UpdateUserCallback mCallback;

        public UpdateUserLoaderCallbacks(Context context, UpdateUserCallback callback) {
            this.mContext = context;
            this.mCallback = callback;
        }

        @Override
        public Loader<Long> onCreateLoader(int id, final Bundle bundle) {
            DataStoreLoader<Long> loader = new DataStoreLoader<Long>(mContext) {
                @Override
                public Long loadInBackground() {
                    return updateUserInBackground(
                            bundle.getString(Constants.SSID),
                            bundle.getString(Constants.SID),
                            bundle.getString(Constants.USERNAME),
                            bundle.getString(Constants.EMAIL));
                }
            };
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Long> loader, Long data) {
            if (data > 0) {
                mCallback.onUpdateUserCallback(true, null);
            } else {
                // If the insert didn't succeed, then the rowID is <= 0. Throws
                // an exception.
                throw new SQLException("Failed to insert a new user.");
            }
        }

        @Override
        public void onLoaderReset(Loader<Long> loader) {
        }
    }

    private long updateUserInBackground(String sSID, String sID, String username, String email) {
        deleteUserInBackground();

        UserObj userObj = new UserObj();
        userObj.sSID = sSID;
        userObj.sID = sID;
        userObj.username = username;
        userObj.email = email;

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.insert(Constants.USER, null, userObj.getContentValues());
    }

    public void deleteUser() {
        // TODO: In background please.
        deleteUserInBackground();
    }

    private void deleteUserInBackground() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(Constants.USER, null, null);
    }

    public interface LogInCallback {
        public void onLogInCallback(Log log);
    }

    public boolean reconnectLogIn(final SherlockFragmentActivity activity, int loaderID,
            final LogInCallback callback) {
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        Loader<Object> loader = loaderManager.getLoader(loaderID);
        if (loader != null && loader.isStarted()) {
            LogInLoaderCallbacks loaderCallbacks = new LogInLoaderCallbacks(activity, callback);
            loaderManager.initLoader(loaderID, null, loaderCallbacks);
            return true;
        } else {
            return false;
        }
    }

    public void logIn(final SherlockFragmentActivity activity, int loaderID,
            String username, String password, final LogInCallback callback) {

        // Validate the inputs. Let all the hard work to the server
        Log log = new Log();
        if (TextUtils.isEmpty(username)) {
            log.addLogInfo(Constants.USERNAME, false, username,
                    activity.getString(R.string.error_field_required));
        }
        if (TextUtils.isEmpty(password)) {
            log.addLogInfo(Constants.PASSWORD, false, password,
                    activity.getString(R.string.error_field_required));
        } else if (password.length() < 7) {
            log.addLogInfo(Constants.PASSWORD, false, password,
                    activity.getString(R.string.error_invalid_password));
        }
        if (!log.isValid()) {
            callback.onLogInCallback(log);
            return;
        }

        if (isNetworkConnected(activity)) {

            String urlString = Constants.URL(activity.getApplicationContext());

            Bundle bundle = new Bundle();
            bundle.putString(Constants.URL_STRING, urlString);
            bundle.putString(Constants.USERNAME, username);
            bundle.putString(Constants.PASSWORD, password);

            LogInLoaderCallbacks loaderCallbacks = new LogInLoaderCallbacks(activity, callback);

            activity.getSupportLoaderManager().restartLoader(loaderID, bundle, loaderCallbacks);
        } else {
            log = new Log();
            log.addLogInfo(Constants.LOG_IN, false, null, activity.getString(R.string.no_network));
            callback.onLogInCallback(log);
        }
    }

    private class LogInLoaderCallbacks implements LoaderCallbacks<Log> {

        private Context mContext;
        private LogInCallback mCallback;

        public LogInLoaderCallbacks(Context context, LogInCallback callback) {
            this.mContext = context;
            this.mCallback = callback;
        }

        @Override
        public Loader<Log> onCreateLoader(int id, final Bundle bundle) {
            DataStoreLoader<Log> loader = new DataStoreLoader<Log>(mContext) {
                @Override
                public Log loadInBackground() {
                    try {
                        return logInInBackground(bundle.getString(Constants.URL_STRING),
                                bundle.getString(Constants.USERNAME),
                                bundle.getString(Constants.PASSWORD));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Log> loader, Log data) {
            mCallback.onLogInCallback(data);
        }

        @Override
        public void onLoaderReset(Loader<Log> loader) {
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

    public boolean reconnectLogOut(final SherlockFragmentActivity activity, int loaderID,
            final LogOutCallback callback) {
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        Loader<Object> loader = loaderManager.getLoader(loaderID);
        if (loader != null && loader.isStarted()) {
            LogOutLoaderCallbacks loaderCallbacks = new LogOutLoaderCallbacks(activity, callback);
            loaderManager.initLoader(loaderID, null, loaderCallbacks);
            return true;
        } else {
            return false;
        }
    }

    public void logOut(SherlockFragmentActivity activity, int loaderID, String sSID, String sID,
            final LogOutCallback callback) {
        if (isNetworkConnected(activity)) {
            String urlString = Constants.URL(activity.getApplicationContext())
                    + Constants.LOG_OUT_PATH;

            Bundle bundle = new Bundle();
            bundle.putString(Constants.URL_STRING, urlString);
            bundle.putString(Constants.SSID, sSID);
            bundle.putString(Constants.SID, sID);

            LogOutLoaderCallbacks loaderCallbacks = new LogOutLoaderCallbacks(activity, callback);

            activity.getSupportLoaderManager().restartLoader(loaderID, bundle, loaderCallbacks);
        } else {
            Log log = new Log();
            log.addLogInfo(Constants.LOG_OUT, false, null, activity.getString(R.string.no_network));
            callback.onLogOutCallback(log);
        }
    }

    private class LogOutLoaderCallbacks implements LoaderCallbacks<Log> {

        private Context mContext;
        private LogOutCallback mCallback;

        public LogOutLoaderCallbacks(Context context, LogOutCallback callback) {
            this.mContext = context;
            this.mCallback = callback;
        }

        @Override
        public Loader<Log> onCreateLoader(int id, final Bundle bundle) {
            DataStoreLoader<Log> loader = new DataStoreLoader<Log>(mContext) {
                @Override
                public Log loadInBackground() {
                    try {
                        return logOutInBackground(
                                bundle.getString(Constants.URL_STRING),
                                bundle.getString(Constants.SSID),
                                bundle.getString(Constants.SID));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Log> loader, Log data) {
            mCallback.onLogOutCallback(data);
        }

        @Override
        public void onLoaderReset(Loader<Log> loader) {
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

    public boolean reconnectGetPagingPageUrls(final SherlockFragmentActivity activity, int loaderID,
            final GetPagingPageUrlsCallback callback) {
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        Loader<Object> loader = loaderManager.getLoader(loaderID);
        if (loader != null && loader.isStarted()) {
            GetPagingPageUrlsLoaderCallbacks loaderCallbacks = new GetPagingPageUrlsLoaderCallbacks(
                    activity, callback);
            loaderManager.initLoader(loaderID, null, loaderCallbacks);
            return true;
        } else {
            return false;
        }
    }

    public void getPagingPageUrls(SherlockFragmentActivity activity, int loaderID, String sSID,
            String sID, String cursorString, final GetPagingPageUrlsCallback callback) {

        if (isNetworkConnected(activity)) {

            String urlString = Constants.URL(activity.getApplicationContext()) + Constants.P2R_PATH;

            Bundle bundle = new Bundle();
            bundle.putString(Constants.URL_STRING, urlString);
            bundle.putString(Constants.SSID, sSID);
            bundle.putString(Constants.SID, sID);
            bundle.putString(Constants.CURSOR_STRING, cursorString);

            GetPagingPageUrlsLoaderCallbacks loaderCallbacks = new GetPagingPageUrlsLoaderCallbacks(
                    activity, callback);

            activity.getSupportLoaderManager().restartLoader(loaderID, bundle, loaderCallbacks);
        } else {
            Log log = new Log();
            log.addLogInfo(Constants.GET_PAGING_PAGE_URLS, false, null,
                    activity.getString(R.string.no_network));
            callback.onGetPagingPageUrlsCallback(log);
        }
    }

    private class GetPagingPageUrlsLoaderCallbacks implements LoaderCallbacks<Log> {

        private Context mContext;
        private GetPagingPageUrlsCallback mCallback;

        public GetPagingPageUrlsLoaderCallbacks(Context context,
                GetPagingPageUrlsCallback callback) {
            this.mContext = context;
            this.mCallback = callback;
        }

        @Override
        public Loader<Log> onCreateLoader(int id, final Bundle bundle) {
            DataStoreLoader<Log> loader = new DataStoreLoader<Log>(mContext) {
                @Override
                public Log loadInBackground() {
                    try {
                        return getPagingPageUrlsInBackground(
                                bundle.getString(Constants.URL_STRING),
                                bundle.getString(Constants.SSID),
                                bundle.getString(Constants.SID),
                                bundle.getString(Constants.CURSOR_STRING));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Log> loader, Log data) {
            mCallback.onGetPagingPageUrlsCallback(data);
        }

        @Override
        public void onLoaderReset(Loader<Log> loader) {
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

    public boolean reconnectAddPageUrl(final SherlockFragmentActivity activity, int loaderID,
            final AddPageUrlCallback callback) {
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        Loader<Object> loader = loaderManager.getLoader(loaderID);
        if (loader != null && loader.isStarted()) {
            AddPageUrlLoaderCallbacks loaderCallbacks = new AddPageUrlLoaderCallbacks(activity,
                    callback);
            loaderManager.initLoader(loaderID, null, loaderCallbacks);
            return true;
        } else {
            return false;
        }
    }

    public void addPageUrl(SherlockFragmentActivity activity, int loaderID, String sSID, String sID,
            String pUrl, final AddPageUrlCallback callback) {
        addPageUrl(activity.getApplicationContext(), activity, loaderID, sSID, sID, pUrl,
                callback, false);
    }

    public void addPageUrlWithNoAsync(Context applicationContext, String sSID,
            String sID, String pUrl, final AddPageUrlCallback callback) {
        addPageUrl(applicationContext, null, 0, sSID, sID, pUrl, callback, true);
    }

    private void addPageUrl(Context applicationContext, SherlockFragmentActivity activity,
            int loaderID, String sSID, String sID, String pUrl, final AddPageUrlCallback callback,
            boolean withNoAsync) {

        // Caveat: if withNoAsync == true, activity is null as it's called from a service.

        // Put http:// if needed
        if (!pUrl.startsWith("http://") &&
            !pUrl.startsWith("https://")) {
          pUrl = "http://" + pUrl;
        }

        // Validate pUrl
        if (TextUtils.isEmpty(pUrl)) {
            Log log = new Log();
            log.addLogInfo(Constants.ADD_PAGE_URL, false, pUrl,
                    applicationContext.getResources().getString(R.string.error_field_required));
            callback.onAddPageUrlCallback(log);
            return;
        }

        try {
            new URL(pUrl);
        } catch(MalformedURLException e) {
            Log log = new Log();
            log.addLogInfo(Constants.ADD_PAGE_URL, false, pUrl,
                    applicationContext.getResources().getString(R.string.error_invalid_url));
            callback.onAddPageUrlCallback(log);
            return;
        }

        if (isNetworkConnected(applicationContext)) {

            String urlString = Constants.URL(applicationContext) + Constants.P2R_PATH;

            if (withNoAsync) {
                Log log = null;
                try {
                    log = addPageUrlInBackground(urlString, sSID, sID, pUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callback.onAddPageUrlCallback(log);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.URL_STRING, urlString);
                bundle.putString(Constants.SSID, sSID);
                bundle.putString(Constants.SID, sID);
                bundle.putString(Constants.P_URL, pUrl);

                AddPageUrlLoaderCallbacks loaderCallbacks = new AddPageUrlLoaderCallbacks(
                        activity, callback);

                activity.getSupportLoaderManager().restartLoader(loaderID, bundle, loaderCallbacks);
            }
        } else {
            Log log = new Log();
            log.addLogInfo(Constants.ADD_PAGE_URL, false, null,
                    applicationContext.getString(R.string.no_network));
            callback.onAddPageUrlCallback(log);
        }
    }

    private class AddPageUrlLoaderCallbacks implements LoaderCallbacks<Log> {

        private Context mContext;
        private AddPageUrlCallback mCallback;

        public AddPageUrlLoaderCallbacks(Context context, AddPageUrlCallback callback) {
            this.mContext = context;
            this.mCallback = callback;
        }

        @Override
        public Loader<Log> onCreateLoader(int id, final Bundle bundle) {
            DataStoreLoader<Log> loader = new DataStoreLoader<Log>(mContext) {
                @Override
                public Log loadInBackground() {
                    try {
                        return addPageUrlInBackground(
                                bundle.getString(Constants.URL_STRING),
                                bundle.getString(Constants.SSID),
                                bundle.getString(Constants.SID),
                                bundle.getString(Constants.P_URL));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Log> loader, Log data) {
            mCallback.onAddPageUrlCallback(data);
        }

        @Override
        public void onLoaderReset(Loader<Log> loader) {
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

    public boolean reconnectDeletePageUrl(final SherlockFragmentActivity activity, int loaderID,
            final DeletePageUrlCallback callback) {
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        Loader<Object> loader = loaderManager.getLoader(loaderID);
        if (loader != null && loader.isStarted()) {
            DeletePageUrlLoaderCallbacks loaderCallbacks = new DeletePageUrlLoaderCallbacks(
                    activity, callback);
            loaderManager.initLoader(loaderID, null, loaderCallbacks);
            return true;
        } else {
            return false;
        }
    }

    public void deletePageUrl(SherlockFragmentActivity activity, int loaderID, String sSID,
            String sID, String keyString, final DeletePageUrlCallback callback) {

        if (isNetworkConnected(activity)) {

            String urlString = Constants.URL(activity.getApplicationContext()) + Constants.P2R_PATH;

            Bundle bundle = new Bundle();
            bundle.putString(Constants.URL_STRING, urlString);
            bundle.putString(Constants.SSID, sSID);
            bundle.putString(Constants.SID, sID);
            bundle.putString(Constants.KEY_STRING, keyString);

            DeletePageUrlLoaderCallbacks loaderCallbacks = new DeletePageUrlLoaderCallbacks(
                    activity, callback);

            activity.getSupportLoaderManager().restartLoader(loaderID, bundle, loaderCallbacks);
        } else {
            Log log = new Log();
            log.addLogInfo(Constants.DELETE_PAGE_URL, false, null,
                    activity.getString(R.string.no_network));
            callback.onDeletePageUrlCallback(log);
        }
    }

    private class DeletePageUrlLoaderCallbacks implements LoaderCallbacks<Log> {

        private Context mContext;
        private DeletePageUrlCallback mCallback;

        public DeletePageUrlLoaderCallbacks(Context context, DeletePageUrlCallback callback) {
            this.mContext = context;
            this.mCallback = callback;
        }

        @Override
        public Loader<Log> onCreateLoader(int id, final Bundle bundle) {
            DataStoreLoader<Log> loader = new DataStoreLoader<Log>(mContext) {
                @Override
                public Log loadInBackground() {
                    try {
                        return deletePageUrlInBackground(
                                bundle.getString(Constants.URL_STRING),
                                bundle.getString(Constants.SSID),
                                bundle.getString(Constants.SID),
                                bundle.getString(Constants.KEY_STRING));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Log> loader, Log data) {
            mCallback.onDeletePageUrlCallback(data);
        }

        @Override
        public void onLoaderReset(Loader<Log> loader) {
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

    public boolean reconnectResendToReader(final SherlockFragmentActivity activity, int loaderID,
            final ResendToReaderCallback callback) {
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        Loader<Object> loader = loaderManager.getLoader(loaderID);
        if (loader != null && loader.isStarted()) {
            ResendToReaderLoaderCallbacks loaderCallbacks = new ResendToReaderLoaderCallbacks(
                    activity, callback);
            loaderManager.initLoader(loaderID, null, loaderCallbacks);
            return true;
        } else {
            return false;
        }
    }

    public void resendToReader(SherlockFragmentActivity activity, int loaderID, String sSID,
            String sID, String keyString, final ResendToReaderCallback callback) {

        if (isNetworkConnected(activity)) {

            String urlString = Constants.URL(activity.getApplicationContext()) + Constants.P2R_PATH;

            Bundle bundle = new Bundle();
            bundle.putString(Constants.URL_STRING, urlString);
            bundle.putString(Constants.SSID, sSID);
            bundle.putString(Constants.SID, sID);
            bundle.putString(Constants.KEY_STRING, keyString);

            ResendToReaderLoaderCallbacks loaderCallbacks = new ResendToReaderLoaderCallbacks(
                    activity, callback);

            activity.getSupportLoaderManager().restartLoader(loaderID, bundle, loaderCallbacks);
        } else {
            Log log = new Log();
            log.addLogInfo(Constants.SEND_TO_READER, false, null,
                    activity.getString(R.string.no_network));
            callback.onResendToReaderCallback(log);
        }
    }

    private class ResendToReaderLoaderCallbacks implements LoaderCallbacks<Log> {

        private Context mContext;
        private ResendToReaderCallback mCallback;

        public ResendToReaderLoaderCallbacks(Context context, ResendToReaderCallback callback) {
            this.mContext = context;
            this.mCallback = callback;
        }

        @Override
        public Loader<Log> onCreateLoader(int id, final Bundle bundle) {
            DataStoreLoader<Log> loader = new DataStoreLoader<Log>(mContext) {
                @Override
                public Log loadInBackground() {
                    try {
                        return resendToReaderInBackground(
                                bundle.getString(Constants.URL_STRING),
                                bundle.getString(Constants.SSID),
                                bundle.getString(Constants.SID),
                                bundle.getString(Constants.KEY_STRING));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Log> loader, Log data) {
            mCallback.onResendToReaderCallback(data);
        }

        @Override
        public void onLoaderReset(Loader<Log> loader) {
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
