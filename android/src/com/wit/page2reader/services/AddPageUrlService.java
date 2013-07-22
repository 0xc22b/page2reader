package com.wit.page2reader.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.wit.page2reader.Constants;
import com.wit.page2reader.R;
import com.wit.page2reader.model.DataStore;
import com.wit.page2reader.model.DataStore.AddPageUrlCallback;
import com.wit.page2reader.model.DataStore.FetchUserCallback;
import com.wit.page2reader.model.Log;

public class AddPageUrlService extends IntentService {

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
            dataStore.fetchUser(new FetchUserCallback() {
                @Override
                public void onFetchUserCallback(String sSID, String sID, String username,
                        String email) {
                    if (sSID == null || sID == null) {
                        toastMakeText(Constants.NOT_LOGGED_IN, Toast.LENGTH_SHORT);
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
        DataStore dataStore = DataStore.getInstance(this.getApplicationContext());
        dataStore.addPageUrl(this.getApplicationContext(), dataStore.sSID, dataStore.sID, pUrl,
                new AddPageUrlCallback() {
            @Override
            public void onAddPageUrlCallback(Log log) {
                if (log == null) {
                    toastMakeText(Constants.CONNECTION_FAILED, Toast.LENGTH_SHORT);
                    return;
                }

                String value = log.getValue(Constants.ADD_PAGE_URL, true);
                if (value != null) {
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
