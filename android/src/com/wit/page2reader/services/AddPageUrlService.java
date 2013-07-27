package com.wit.page2reader.services;

import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.widget.Toast;

import com.wit.page2reader.Constants;
import com.wit.page2reader.R;
import com.wit.page2reader.model.DataStore;
import com.wit.page2reader.model.DataStore.AddPageUrlCallback;
import com.wit.page2reader.model.DataStore.FetchUserCallback;
import com.wit.page2reader.model.Log;
import com.wit.page2reader.model.PageUrlObj;

public class AddPageUrlService extends IntentService {

    public static final String WAKE_LOCK_TAG = "AddPageUrlServiceWakeLock";

    public static WakeLock wakeLock;

    private Handler mainThreadHandler = null;

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public AddPageUrlService() {
        super("AddPageUrlService");

        mainThreadHandler = new Handler();
    }

    @Override
    public void onDestroy() {
        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
            wakeLock = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        toastMakeText(getResources().getString(R.string.sending), Toast.LENGTH_LONG);

        Bundle extras = intent.getExtras();
        final String pUrl = extras.getString(Constants.P_URL);

        // TODO: Validate url
        if (TextUtils.isEmpty(pUrl)) {
            
            return;
        }

        final DataStore dataStore = DataStore.getInstance(this.getApplicationContext());
        if (dataStore.sSID == null || dataStore.sID == null) {
            // Don't use AsyncTask as this method is called in worker thread alredy!
            dataStore.fetchUserWithNoAsync(new FetchUserCallback() {
                @Override
                public void onFetchUserCallback(String sSID, String sID, String username,
                        String email) {
                    if (sSID == null || sID == null) {
                        toastMakeText(getString(R.string.external_not_logged_in),
                                Toast.LENGTH_SHORT);
                        return;
                    }

                    dataStore.sSID = sSID;
                    dataStore.sID = sID;

                    addPageUrl(pUrl);
                }
            });
        } else {
            addPageUrl(pUrl);
        }
    }

    private void addPageUrl(String pUrl) {
        // Don't use AsyncTask as this method is called in worker thread alredy!
        final DataStore dataStore = DataStore.getInstance(this.getApplicationContext());
        dataStore.addPageUrlWithNoAsync(this.getApplicationContext(), dataStore.sSID,
                dataStore.sID, pUrl, new AddPageUrlCallback() {
            @Override
            public void onAddPageUrlCallback(Log log) {
                if (log == null) {
                    toastMakeText(getString(R.string.connection_failed), Toast.LENGTH_SHORT);
                    return;
                }

                String value = log.getValue(Constants.ADD_PAGE_URL, true);
                if (value != null) {
                    JSONObject jsonPageUrl = new JSONObject(value);
                    PageUrlObj pageUrl = new PageUrlObj(jsonPageUrl);
                    dataStore.pageUrls.add(0, pageUrl);

                    toastMakeText(getResources().getString(R.string.sent), Toast.LENGTH_SHORT);
                    return;
                }

                String msg = null;

                msg = log.getMsg(Constants.ADD_PAGE_URL, false, null);
                if (msg != null) {
                    toastMakeText(msg, Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void toastMakeText(final String text, final int duration) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                        AddPageUrlService.this,
                        text,
                        duration).show();
            }
        });
    }
}
