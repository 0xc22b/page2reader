package com.wit.page2reader.activities;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.wit.page2reader.Constants;
import com.wit.page2reader.R;
import com.wit.page2reader.adapters.P2rAdapter;
import com.wit.page2reader.model.DataStore;
import com.wit.page2reader.model.DataStore.DeletePageUrlCallback;
import com.wit.page2reader.model.DataStore.FetchUserCallback;
import com.wit.page2reader.model.DataStore.GetPagingPageUrlsCallback;
import com.wit.page2reader.model.DataStore.LogOutCallback;
import com.wit.page2reader.model.DataStore.ResendToReaderCallback;
import com.wit.page2reader.model.Log;
import com.wit.page2reader.model.PageUrlObj;

public class P2rActivity extends SherlockActivity {

    // References for convenience
    private DataStore dataStore;

    private OnClickListener mOnDelBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onDelBtnClick((Integer)v.getTag());
        }
    };

    private OnClickListener mOnResendBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onResendBtnClick((Button)v, (Integer)v.getTag());
        }
    };

    private OnClickListener mOnNextBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onNextBtnClick();
        }
    };

    private P2rAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataStore = DataStore.getInstance(this.getApplicationContext());
        if (dataStore.sSID == null || dataStore.sID == null) {
            dataStore.fetchUser(new FetchUserCallback() {
                @Override
                public void onFetchUserCallback(String sSID, String sID, String username,
                        String email) {
                    if (sSID == null || sID == null) {
                        Intent intent = new Intent(P2rActivity.this, HomeActivity.class);
                        // TODO: Clear task
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        P2rActivity.this.startActivity(intent);
                        return;
                    }

                    dataStore.sSID = sSID;
                    dataStore.sID = sID;
                }
            });
        }

        setContentView(R.layout.activity_p2r);
        mListView = (ListView) findViewById(R.id.list_view);

        int[] layouts = {R.layout.list_entry, R.layout.list_msg, R.layout.list_next};
        mAdapter = new P2rAdapter(this, layouts, dataStore.pageUrls,
                mOnDelBtnClickListener, mOnResendBtnClickListener, mOnNextBtnClickListener);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.p2r, menu);
        return true;
    }

    @Override
    protected void onStart() {
         super.onStart();

         if (dataStore.pageUrls.isEmpty()) {

            // Set listview show loading
            mAdapter.msg = this.getResources().getString(R.string.loading);
            mAdapter.notifyDataSetChanged();

            dataStore.getPagingPageUrls(this, dataStore.sSID, dataStore.sID, null,
                    new GetPagingPageUrlsCallback(){
                @Override
                public void onGetPagingPageUrlsCallback(Log log) {

                    if (log == null) {
                        showAlertDialog(Constants.CONNECTION_FAILED);
                        return;
                    }

                    String value = log.getValue(Constants.GET_PAGING_PAGE_URLS, true);
                    if (value != null) {
                        dataStore.pageUrls.clear();

                        JSONObject jsonObj = new JSONObject(value);
                        JSONArray jsonPageUrls = jsonObj.getJSONArray(Constants.PAGE_URLS);
                        for (int i = 0; i < jsonPageUrls.length(); i++) {
                            JSONObject jsonPageUrl = jsonPageUrls.getJSONObject(i);
                            PageUrlObj pageUrl = new PageUrlObj(jsonPageUrl);
                            dataStore.pageUrls.add(pageUrl);
                        }

                        dataStore.cursorString = jsonObj.getString(Constants.CURSOR_STRING);

                        if (dataStore.pageUrls.isEmpty()) {
                            mAdapter.msg = getResources().getString(R.string.no_data);
                            mAdapter.shownNext = false;
                        } else {
                            mAdapter.msg = null;
                            mAdapter.shownNext = true;
                        }

                        mAdapter.notifyDataSetChanged();
                        return;
                    }

                    String msg = null;

                    msg = log.getMsg(Constants.GET_PAGING_PAGE_URLS, false, null);
                    if (msg != null) {
                        showAlertDialog(msg);
                    }
                }
            });
        } else {
            if (dataStore.pageUrls.isEmpty()) {
                mAdapter.msg = getResources().getString(R.string.no_data);
            } else {
                mAdapter.msg = null;
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            onRefreshBtnClick();
            return true;
        } else if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(P2rActivity.this, EditorActivity.class);
            P2rActivity.this.startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            String urlString = Constants.URL(this.getApplicationContext()) + Constants.USER_PATH;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(urlString));
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_log_out) {
            onLogOutBtnClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDelBtnClick(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            onConfirmDelBtnClick(position);
                        }
                    })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        builder.create().show();
    }

    private void onConfirmDelBtnClick(int position) {
        final PageUrlObj pageUrl = dataStore.pageUrls.get(position);

        dataStore.deletePageUrl(this, dataStore.sSID, dataStore.sID, pageUrl.keyString,
                new DeletePageUrlCallback() {
            @Override
            public void onDeletePageUrlCallback(Log log) {
                if (log == null) {
                    showAlertDialog(Constants.CONNECTION_FAILED);
                    return;
                }

                String keyString = log.getValue(Constants.DELETE_PAGE_URL, true);
                if (keyString != null) {
                    if (keyString.equals(pageUrl.keyString)) {
                        dataStore.pageUrls.remove(pageUrl);

                        if (dataStore.pageUrls.isEmpty() && mAdapter.shownNext == false) {
                            mAdapter.msg = getResources().getString(R.string.no_data);
                        }
                        mAdapter.notifyDataSetChanged();
                        return;
                    } else {
                        throw new IllegalArgumentException();
                    }
                }

                String msg = null;

                msg = log.getMsg(Constants.DELETE_PAGE_URL, false, null);
                if (msg != null) {
                    showAlertDialog(msg);
                }
            }
        });
    }

    private void onResendBtnClick(final Button resendBtn, int position) {
        final PageUrlObj pageUrl = dataStore.pageUrls.get(position);

        resendBtn.setClickable(false);
        resendBtn.setText(getResources().getString(R.string.sending));

        dataStore.resendToReader(this, dataStore.sSID, dataStore.sID, pageUrl.keyString,
                new ResendToReaderCallback() {
            @Override
            public void onResendToReaderCallback(Log log) {
                if (log == null) {
                    showAlertDialog(Constants.CONNECTION_FAILED);
                    return;
                }

                String keyString = log.getValue(Constants.SEND_TO_READER, true);
                if (keyString != null) {
                    if (keyString.equals(pageUrl.keyString)) {
                        resendBtn.setText(getResources().getString(R.string.sent));
                        return;
                    } else {
                        throw new IllegalArgumentException();
                    }
                }

                String msg = null;

                resendBtn.setClickable(true);
                resendBtn.setText(getResources().getString(R.string.resend));

                msg = log.getMsg(Constants.SEND_TO_READER, false, null);
                if (msg != null) {
                    showAlertDialog(msg);
                }
            }
        });
    }

    private void onRefreshBtnClick() {
        dataStore.getPagingPageUrls(this, dataStore.sSID, dataStore.sID, null,
                new GetPagingPageUrlsCallback(){
                    @Override
                    public void onGetPagingPageUrlsCallback(Log log) {

                        if (log == null) {
                            showAlertDialog(Constants.CONNECTION_FAILED);
                            return;
                        }

                        String value = log.getValue(Constants.GET_PAGING_PAGE_URLS, true);
                        if (value != null) {
                            dataStore.pageUrls.clear();

                            JSONObject jsonObj = new JSONObject(value);
                            JSONArray jsonPageUrls = jsonObj.getJSONArray(Constants.PAGE_URLS);
                            for (int i = 0; i < jsonPageUrls.length(); i++) {
                                JSONObject jsonPageUrl = jsonPageUrls.getJSONObject(i);
                                PageUrlObj pageUrl = new PageUrlObj(jsonPageUrl);
                                dataStore.pageUrls.add(pageUrl);
                            }

                            dataStore.cursorString = jsonObj.getString(Constants.CURSOR_STRING);

                            if (dataStore.pageUrls.isEmpty()) {
                                mAdapter.msg = getResources().getString(R.string.no_data);
                                mAdapter.shownNext = false;
                            } else {
                                mAdapter.msg = null;
                                mAdapter.shownNext = true;
                            }

                            mAdapter.notifyDataSetChanged();
                            return;
                        }

                        String msg = null;

                        msg = log.getMsg(Constants.GET_PAGING_PAGE_URLS, false, null);
                        if (msg != null) {
                            showAlertDialog(msg);
                        }
                    }
                });
    }

    private void onNextBtnClick() {
        dataStore.getPagingPageUrls(this, dataStore.sSID, dataStore.sID, dataStore.cursorString,
                new GetPagingPageUrlsCallback() {
            @Override
            public void onGetPagingPageUrlsCallback(Log log) {
                if (log == null) {
                    showAlertDialog(Constants.CONNECTION_FAILED);
                    return;
                }

                String value = log.getValue(Constants.GET_PAGING_PAGE_URLS, true);
                if (value != null) {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONArray jsonPageUrls = jsonObj.getJSONArray(Constants.PAGE_URLS);
                    for (int i = 0; i < jsonPageUrls.length(); i++) {
                        JSONObject jsonPageUrl = jsonPageUrls.getJSONObject(i);
                        PageUrlObj pageUrl = new PageUrlObj(jsonPageUrl);
                        dataStore.pageUrls.add(pageUrl);
                    }

                    dataStore.cursorString = jsonObj.getString(Constants.CURSOR_STRING);

                    if (jsonPageUrls.length() == 0) mAdapter.shownNext = false;

                    if (dataStore.pageUrls.isEmpty()) {
                        mAdapter.msg = getResources().getString(R.string.no_data);
                    }

                    mAdapter.notifyDataSetChanged();
                    return;
                }

                String msg = null;

                msg = log.getMsg(Constants.GET_PAGING_PAGE_URLS, false, null);
                if (msg != null) {
                    showAlertDialog(msg);
                }
            }
        });
    }

    private void onLogOutBtnClick() {
        dataStore.logOut(P2rActivity.this, dataStore.sSID, dataStore.sID, new LogOutCallback() {
            @Override
            public void onLogOutCallback(Log log) {
                dataStore.deleteUser();

                // Clear all data
                dataStore.sSID = null;
                dataStore.sID = null;
                dataStore.pageUrls.clear();
                dataStore.cursorString = null;

                Intent intent = new Intent(P2rActivity.this, HomeActivity.class);
                // TODO: Clear stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                P2rActivity.this.startActivity(intent);
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
