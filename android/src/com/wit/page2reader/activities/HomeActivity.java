package com.wit.page2reader.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.wit.page2reader.Constants;
import com.wit.page2reader.R;
import com.wit.page2reader.model.DataStore;
import com.wit.page2reader.model.DataStore.FetchUserCallback;

public class HomeActivity extends SherlockFragmentActivity implements FetchUserCallback {

    public static final int LOG_IN_REQUEST_CODE = 1;

    private DataStore mDataStore;

    private Button mToWebBtn;

    private boolean mDidGetSession;

    private ProgressDialog mSplashScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataStore = DataStore.getInstance(this.getApplicationContext());

        mDidGetSession = savedInstanceState == null ? false
                : savedInstanceState.getBoolean(Constants.DID_GET_SESSION);

        if (!mDidGetSession) {
            mSplashScreen = new ProgressDialog(this);
            mSplashScreen.setMessage(getResources().getString(R.string.loading));
            mSplashScreen.setCancelable(false);
            mSplashScreen.show();

            if (mDataStore.sSID != null && mDataStore.sID != null) {
                Intent intent = new Intent(HomeActivity.this, P2rActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                this.startActivity(intent);

                mSplashScreen.dismiss();
                this.finish();
                return;
            } else {
                boolean reconnect = mDataStore.reconnectFetchUser(this,
                        Constants.HOME_FETCH_USER_LOADER_ID, this);
                if (!reconnect) {
                    mDataStore.fetchUser(this, Constants.HOME_FETCH_USER_LOADER_ID, this);
                }
            }
        }

        setContentView(R.layout.activity_home);
        mToWebBtn = (Button) findViewById(R.id.to_web);
        mToWebBtn.setOnClickListener(mOnToWebBtnClickListener);
    }

    @Override
    public void onFetchUserCallback(String sSID, String sID, String username, String email) {
        if (sSID != null && sID != null) {

            mDataStore.sSID = sSID;
            mDataStore.sID = sID;

            Intent intent = new Intent(HomeActivity.this, P2rActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            HomeActivity.this.startActivity(intent);

            mSplashScreen.dismiss();
            HomeActivity.this.finish();
            return;
        }

        mSplashScreen.dismiss();
        mDidGetSession = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOG_IN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(HomeActivity.this, P2rActivity.class);
            // Clear task
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(intent);
            this.finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Constants.DID_GET_SESSION, mDidGetSession);
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
        if (item.getItemId() == R.id.action_to_log_in) {
            onToLogInBtnClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onToLogInBtnClick() {
        Intent intent = new Intent(HomeActivity.this, LogInActivity.class);
        HomeActivity.this.startActivityForResult(intent, LOG_IN_REQUEST_CODE);
    }

    private OnClickListener mOnToWebBtnClickListener = new OnClickListener() {    
        @Override
        public void onClick(View v) {
            String urlString = Constants.URL(getApplicationContext());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(urlString));
            startActivity(intent);
        }
    };
}
