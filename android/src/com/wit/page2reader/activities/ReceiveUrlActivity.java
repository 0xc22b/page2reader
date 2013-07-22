package com.wit.page2reader.activities;

import com.wit.page2reader.Constants;
import com.wit.page2reader.services.AddPageUrlService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class ReceiveUrlActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        Bundle extras = new Bundle();
        extras.putString(Constants.P_URL, url);

        Intent intent = new Intent(this, AddPageUrlService.class);
        intent.putExtras(extras);
        this.startService(intent);
        
        this.finish();
    }
}