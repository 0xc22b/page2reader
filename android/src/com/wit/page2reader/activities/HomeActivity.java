package com.wit.page2reader.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.wit.page2reader.R;
import com.wit.page2reader.model.DataStore;
import com.wit.page2reader.model.DataStore.FetchUserCallback;

public class HomeActivity extends SherlockActivity {

    public static final int LOG_IN_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DataStore dataStore = DataStore.getInstance(this.getApplicationContext());
        if (dataStore.sSID != null && dataStore.sID != null) {
            Intent intent = new Intent(HomeActivity.this, P2rActivity.class);
            // Clear task
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(intent);
            this.finish();
            return;
        } else {
            dataStore.fetchUser(new FetchUserCallback() {
                @Override
                public void onFetchUserCallback(String sSID, String sID, String username,
                        String email) {
                    if (sSID != null && sID != null) {

                        dataStore.sSID = sSID;
                        dataStore.sID = sID;

                        Intent intent = new Intent(HomeActivity.this, P2rActivity.class);
                        // Clear task
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                        HomeActivity.this.startActivity(intent);
                        HomeActivity.this.finish();
                        return;
                    }
                }
            });
        }

        setContentView(R.layout.activity_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
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

}
