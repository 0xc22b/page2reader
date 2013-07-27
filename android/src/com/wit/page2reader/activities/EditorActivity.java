package com.wit.page2reader.activities;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.wit.page2reader.Constants;
import com.wit.page2reader.R;
import com.wit.page2reader.model.DataStore;
import com.wit.page2reader.model.DataStore.AddPageUrlCallback;
import com.wit.page2reader.model.DataStore.FetchUserCallback;
import com.wit.page2reader.model.Log;
import com.wit.page2reader.model.PageUrlObj;

public class EditorActivity extends SherlockFragmentActivity implements FetchUserCallback,
        AddPageUrlCallback {

    // Reference for convenience
    private DataStore mDataStore;

    private TextView mTextView;
    private EditText mEditText;

    private boolean mIsConnecting;

    // Needs splash screen as sometimes all data is clear
    // but the system remembers the last activity and go directly to it.
    // So needs to load data.
    private ProgressDialog mSplashScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataStore = DataStore.getInstance(this.getApplicationContext());

        if (mDataStore.sSID == null || mDataStore.sID == null) {
            mSplashScreen = new ProgressDialog(this);
            mSplashScreen.setMessage(getResources().getString(R.string.loading));
            mSplashScreen.setCancelable(false);
            mSplashScreen.show();

            boolean reconnect = mDataStore.reconnectFetchUser(this,
                    Constants.P2R_FETCH_USER_LOADER_ID, this);
            if (!reconnect) {
                mDataStore.fetchUser(this, Constants.P2R_FETCH_USER_LOADER_ID, this);
            }
        }

        setContentView(R.layout.activity_editor);
        mTextView = (TextView) findViewById(R.id.textView);
        mEditText = (EditText) findViewById(R.id.editText);

        if (savedInstanceState != null) {
            mTextView.setText(savedInstanceState.getString(Constants.MSG));
        }

        if (mDataStore.reconnectAddPageUrl(this, Constants.ADD_PAGE_URL_LOADER_ID, this)) {
            mIsConnecting = true;
            mEditText.setEnabled(false);
        }
    }

    @Override
    public void onFetchUserCallback(String sSID, String sID, String username, String email) {

        mSplashScreen.dismiss();

        if (sSID == null || sID == null) {
            Intent intent = new Intent(EditorActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            EditorActivity.this.startActivity(intent);
            EditorActivity.this.finish();
            return;
        }

        mDataStore.sSID = sSID;
        mDataStore.sID = sID;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem okMenuItem = menu.findItem(R.id.action_ok);
        okMenuItem.setEnabled(!mIsConnecting);

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.MSG, mTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (mSplashScreen != null && mSplashScreen.isShowing()) {
            mSplashScreen.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ok) {
            onOkBtnClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onOkBtnClick() {
        String pUrl = mEditText.getText().toString();

        mIsConnecting = true;
        this.supportInvalidateOptionsMenu();

        mEditText.setEnabled(false);

        mDataStore.addPageUrl(this, Constants.ADD_PAGE_URL_LOADER_ID, mDataStore.sSID,
                mDataStore.sID, pUrl, this);
    }

    @Override
    public void onAddPageUrlCallback(Log log) {

        mTextView.setText("");

        if (log == null) {
            mTextView.setText(getString(R.string.connection_failed));
        } else {
            String value = log.getValue(Constants.ADD_PAGE_URL, true);
            if (value != null) {
                JSONObject jsonPageUrl = new JSONObject(value);
                PageUrlObj pageUrl = new PageUrlObj(jsonPageUrl);
                mDataStore.pageUrls.add(0, pageUrl);

                this.finish();
                return;
            } else {
                String msg = log.getMsg(Constants.ADD_PAGE_URL, false, null);
                if (msg != null) {
                    mTextView.setText(msg);
                }
            }
        }

        mIsConnecting = false;
        this.supportInvalidateOptionsMenu();

        mEditText.setEnabled(true);
    }
}
