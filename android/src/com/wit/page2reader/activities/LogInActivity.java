package com.wit.page2reader.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.wit.page2reader.Constants;
import com.wit.page2reader.R;
import com.wit.page2reader.model.DataStore;
import com.wit.page2reader.model.DataStore.LogInCallback;
import com.wit.page2reader.model.DataStore.UpdateUserCallback;
import com.wit.page2reader.model.Log;

public class LogInActivity extends SherlockFragmentActivity
        implements LogInCallback, UpdateUserCallback {

    // Reference for convenience
    private DataStore mDataStore;

    // UI references.
    private TextView mUsernameMsgView;
    private EditText mUsernameTB;
    private TextView mPasswordMsgView;
    private EditText mPasswordTB;

    private boolean mIsLoggingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataStore = DataStore.getInstance(this.getApplicationContext());

        setResult(Activity.RESULT_CANCELED);
        setContentView(R.layout.activity_log_in);

        // Set up the login form.
        mUsernameMsgView = (TextView) findViewById(R.id.username_msg);
        mUsernameTB = (EditText) findViewById(R.id.username);
        mPasswordMsgView = (TextView) findViewById(R.id.password_msg);
        mPasswordTB = (EditText) findViewById(R.id.password);

        if (savedInstanceState != null) {
            mUsernameMsgView.setText(savedInstanceState.getString(Constants.USERNAME));
            mPasswordMsgView.setText(savedInstanceState.getString(Constants.PASSWORD));
        }

        if (mDataStore.reconnectLogIn(this, Constants.LOG_IN_LOADER_ID, this)
                || mDataStore.reconnectUpdateUser(this, Constants.UPDATE_USER_LOADER_ID, this)) {
            mIsLoggingIn = true;
            mUsernameTB.setEnabled(false);
            mPasswordTB.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.log_in, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem logInMenuItem = menu.findItem(R.id.action_log_in);
        logInMenuItem.setEnabled(!mIsLoggingIn);

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.USERNAME, mUsernameMsgView.getText().toString());
        outState.putString(Constants.PASSWORD, mPasswordMsgView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_log_in) {
            onLogInBtnClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onLogInBtnClick() {
        String username = mUsernameTB.getText().toString();
        String password = mPasswordTB.getText().toString();

        mIsLoggingIn = true;
        this.supportInvalidateOptionsMenu();

        mUsernameTB.setEnabled(false);
        mPasswordTB.setEnabled(false);

        mDataStore.logIn(this, Constants.LOG_IN_LOADER_ID, username, password, this);
    }

    public void onLogInCallback(Log log) {

        mUsernameMsgView.setText("");
        mPasswordMsgView.setText("");

        if (log == null) {
            mUsernameMsgView.setText(getString(R.string.connection_failed));
        } else {
            if (log.isValid()) {
                String sSID = log.getValue(Constants.SSID, true);
                String sID = log.getValue(Constants.SID, true);
                String username = log.getValue(Constants.USERNAME, true);
                String email = log.getValue(Constants.EMAIL, true);

                mDataStore.sSID = sSID;
                mDataStore.sID = sID;

                mDataStore.updateUser(this, Constants.UPDATE_USER_LOADER_ID, sSID, sID,
                        username, email, this);

                return;
            } else {

                String msg = null;

                msg = log.getMsg(Constants.USERNAME, false, null);
                if (msg != null) {
                    mUsernameMsgView.setText(msg);
                }

                msg = log.getMsg(Constants.EMAIL, false, null);
                if (msg != null) {
                    mUsernameMsgView.setText(msg);
                }

                msg = log.getMsg(Constants.PASSWORD, false, null);
                if (msg != null) {
                    mPasswordMsgView.setText(msg);
                }

                msg = log.getMsg(Constants.LOG_IN, false, null);
                if (msg != null) {
                    mUsernameMsgView.setText(msg);
                }
            }
        }

        mIsLoggingIn = false;
        this.supportInvalidateOptionsMenu();

        mUsernameTB.setEnabled(true);
        mPasswordTB.setEnabled(true);
    }

    @Override
    public void onUpdateUserCallback(boolean success, String msg) {
        if (success) {
            LogInActivity.this.setResult(Activity.RESULT_OK);
            LogInActivity.this.finish();
            return;
        }

        mUsernameMsgView.setText(msg);

        mIsLoggingIn = false;
        this.supportInvalidateOptionsMenu();

        mUsernameTB.setEnabled(true);
        mPasswordTB.setEnabled(true);
    }
}
