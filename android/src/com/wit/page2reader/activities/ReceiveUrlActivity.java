package com.wit.page2reader.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;

import com.wit.page2reader.Constants;
import com.wit.page2reader.services.AddPageUrlService;

public class ReceiveUrlActivity extends Activity {

    @SuppressLint("Wakelock")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        Bundle extras = new Bundle();
        extras.putString(Constants.P_URL, url);

        Intent intent = new Intent(this, AddPageUrlService.class);
        intent.putExtras(extras);
        this.startService(intent);

        // It is possible that the phone will sleep before the requested service is launched.
        // To prevent this, your BroadcastReceiver and Service will need to
        // implement a separate wake lock policy to ensure that the phone
        // continues running until the service becomes available.
        // http://developer.android.com/reference/android/app/AlarmManager.html
        //     #set(int, long, android.app.PendingIntent)
        if(AddPageUrlService.wakeLock == null) {
            PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
            AddPageUrlService.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    AddPageUrlService.WAKE_LOCK_TAG);
        }
        if(!AddPageUrlService.wakeLock.isHeld()) {
            // Will be released when the service is done.
            AddPageUrlService.wakeLock.acquire();
        }
        this.finish();
    }
}
