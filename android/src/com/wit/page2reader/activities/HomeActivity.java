package com.wit.page2reader.activities;

import android.os.Bundle;
import android.content.Intent;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import com.wit.page2reader.R;
import com.wit.page2reader.model.DataStore;
import com.wit.page2reader.model.DataStore.FetchUserCallback;

public class HomeActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DataStore dataStore = DataStore.getInstance(
                HomeActivity.this.getApplicationContext());
        if (dataStore.sSID != null && dataStore.sID != null) {
            Intent intent = new Intent(HomeActivity.this, P2rActivity.class);
            HomeActivity.this.startActivity(intent);
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
                        // TODO: Clear task
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        HomeActivity.this.startActivity(intent);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_to_log_in) {
            onToLogInBtnClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onToLogInBtnClick() {
        Intent intent = new Intent(HomeActivity.this, LogInActivity.class);
        HomeActivity.this.startActivity(intent);
    }

}
