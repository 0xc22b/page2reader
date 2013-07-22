package com.wit.page2reader.activities;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.wit.page2reader.Constants;
import com.wit.page2reader.R;
import com.wit.page2reader.model.DataStore;
import com.wit.page2reader.model.DataStore.AddPageUrlCallback;
import com.wit.page2reader.model.DataStore.FetchUserCallback;
import com.wit.page2reader.model.Log;
import com.wit.page2reader.model.PageUrlObj;

public class EditorActivity extends SherlockActivity {

    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DataStore dataStore = DataStore.getInstance(this.getApplicationContext());
        if (dataStore.sSID == null || dataStore.sID == null) {
            dataStore.fetchUser(new FetchUserCallback() {
                @Override
                public void onFetchUserCallback(String sSID, String sID, String username,
                        String email) {
                    if (sSID == null || sID == null) {
                        Intent intent = new Intent(EditorActivity.this, HomeActivity.class);
                        // TODO: Clear task
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        EditorActivity.this.startActivity(intent);
                        return;
                    }

                    dataStore.sSID = sSID;
                    dataStore.sID = sID;
                }
            });
        }

        setContentView(R.layout.activity_editor);
        mEditText = (EditText) findViewById(R.id.editText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.editor, menu);
        return true;
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
        // TODO: Check for a valid url.
        String pUrl = mEditText.getText().toString();
        if (TextUtils.isEmpty(pUrl)) {
            
            return;
        }

        final DataStore dataStore = DataStore.getInstance(this.getApplicationContext());
        dataStore.addPageUrl(this.getApplicationContext(), dataStore.sSID, dataStore.sID, pUrl,
                new AddPageUrlCallback() {
            @Override
            public void onAddPageUrlCallback(Log log) {
                if (log == null) {
                    showAlertDialog(Constants.CONNECTION_FAILED);
                    return;
                }

                String value = log.getValue(Constants.ADD_PAGE_URL, true);
                if (value != null) {
                    JSONObject jsonPageUrl = new JSONObject(value);
                    PageUrlObj pageUrl = new PageUrlObj(jsonPageUrl);
                    dataStore.pageUrls.add(0, pageUrl);

                    onBackPressed();
                    return;
                }

                String msg = null;

                msg = log.getMsg(Constants.ADD_PAGE_URL, false, null);
                if (msg != null) {
                    showAlertDialog(msg);
                }
            }
        });
    }

    private void showAlertDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }
}
