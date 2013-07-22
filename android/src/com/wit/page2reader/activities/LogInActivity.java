package com.wit.page2reader.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.wit.page2reader.Constants;
import com.wit.page2reader.R;
import com.wit.page2reader.model.DataStore;
import com.wit.page2reader.model.DataStore.LogInCallback;
import com.wit.page2reader.model.DataStore.UpdateUserCallback;
import com.wit.page2reader.model.Log;

/**
 * Activity which displays a login screen to the user.
 */
public class LogInActivity extends SherlockActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private AsyncTask<String, Void, Log> mLogInTask = null;

    private String mUsername;
    private String mPassword;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_in);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.log_in, menu);
        return true;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mLogInTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);

            DataStore dataStore = DataStore.getInstance(this.getApplicationContext());
            dataStore.logIn(this, mUsername, mPassword, mLogInCallback);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private LogInCallback mLogInCallback = new LogInCallback() {

            @Override
            public void onLogInCallback(Log log) {

                mLogInTask = null;

                if (log == null) {
                    mUsernameView.setError(Constants.CONNECTION_FAILED);
                    showProgress(false);
                    return;
                }

                if (log.isValid()) {
                    final String sSID = log.getValue(Constants.SSID, true);
                    final String sID = log.getValue(Constants.SID, true);
                    String username = log.getValue(Constants.USERNAME, true);
                    String email = log.getValue(Constants.EMAIL, true);

                    final DataStore dataStore = DataStore.getInstance(
                            LogInActivity.this.getApplicationContext());
                    dataStore.updateUser(sSID, sID, username, email, new UpdateUserCallback() {
                        @Override
                        public void onUpdateUserCallback(boolean success, String msg) {
                            dataStore.sSID = sSID;
                            dataStore.sID = sID;

                            Intent intent = new Intent(LogInActivity.this, P2rActivity.class);
                            // TODO: Clear task
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            LogInActivity.this.startActivity(intent);
                        }
                    });
                } else {
                    String msg = null;

                    msg = log.getMsg(Constants.USERNAME, false, null);
                    if (msg != null) {
                        mUsernameView.setError(msg);
                    }

                    msg = log.getMsg(Constants.EMAIL, false, null);
                    if (msg != null) {
                        mUsernameView.setError(msg);
                    }

                    msg = log.getMsg(Constants.PASSWORD, false, null);
                    if (msg != null) {
                        mPasswordView.setError(msg);
                    }

                    msg = log.getMsg(Constants.LOG_IN, false, null);
                    if (msg != null) {
                        mUsernameView.setError(msg);
                    }

                    showProgress(false);
                }
            }
        };
}
