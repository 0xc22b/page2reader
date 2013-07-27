package com.wit.page2reader.activities;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.wit.page2reader.Constants;
import com.wit.page2reader.Constants.DelBtnStatus;
import com.wit.page2reader.Constants.NextBtnStatus;
import com.wit.page2reader.Constants.RefreshBtnStatus;
import com.wit.page2reader.Constants.ResendBtnStatus;
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

public class P2rActivity extends SherlockFragmentActivity {

    private DataStore mDataStore;

    private OnClickListener mOnTitleClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer)v.getTag();
            String urlString = mDataStore.pageUrls.get(position).pUrl;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(urlString));
            startActivity(intent);
        }
    };

    private OnClickListener mOnDelBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onDelBtnClick((Integer)v.getTag());
        }
    };

    private OnClickListener mOnResendBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onResendBtnClick((Integer)v.getTag());
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

    private AlertDialog mConfirmDeleteDialog;
    private ProgressDialog mLogOutDialog;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataStore = DataStore.getInstance(this.getApplicationContext());

        setContentView(R.layout.activity_p2r);
        mListView = (ListView) findViewById(R.id.list_view);

        int[] layouts = {R.layout.list_entry, R.layout.list_msg, R.layout.list_next};
        mAdapter = new P2rAdapter(this, layouts, mDataStore, mOnTitleClickListener,
                mOnDelBtnClickListener, mOnResendBtnClickListener, mOnNextBtnClickListener);
        mListView.setAdapter(mAdapter);

        if (mDataStore.didGetPageUrls == false) {
            // Set listview show loading
            mDataStore.msg = this.getResources().getString(R.string.loading);

            if (mDataStore.sSID == null || mDataStore.sID == null) {
                boolean reconnect = mDataStore.reconnectFetchUser(this,
                        Constants.P2R_FETCH_USER_LOADER_ID, mFetchUserCallback);
                if (!reconnect) {
                    mDataStore.fetchUser(this, Constants.P2R_FETCH_USER_LOADER_ID,
                            mFetchUserCallback);
                }
            } else {
                getFirstTimePagingPageUrls();
            }
        } else {
            // Try to reconnect loaders

            // Refresh button's status was kept in DataStore already
            mDataStore.reconnectGetPagingPageUrls(this, Constants.RF_GET_PAGE_URLS_LOADER_ID,
                    mRefreshGetPageUrlsCallback);

            // Next button's status was kept in DataStore already
            mDataStore.reconnectGetPagingPageUrls(this, Constants.NE_GET_PAGE_URLS_LOADER_ID,
                    mNextGetPageUrlsCallback);

            // Log out button's status was kept in DataStore already
            if (mDataStore.reconnectLogOut(this, Constants.LOG_OUT_LOADER_ID, mLogOutCallback)) {
                showLogOutDialog();
            }

            // Delete button's status was kept in DataStore already
            mDataStore.reconnectDeletePageUrl(this, Constants.DELETE_PAGE_URL_LOADER_ID,
                    mDeletePageUrlCallback);

            // Resend button's status was kept in DataStore already
            mDataStore.reconnectResendToReader(this, Constants.RESEND_TO_READER_LOADER_ID,
                    mResendToReaderCallback);

            if (savedInstanceState != null) {
                String alertDialogMessage = savedInstanceState.getString(Constants.MSG);
                if (alertDialogMessage != null) {
                    showAlertDialog(alertDialogMessage);
                }
                int beingDeletedPosition = savedInstanceState.getInt(
                        Constants.BEING_DELETED_POSITION, -1);
                if (beingDeletedPosition != -1) {
                    showConfirmDeleteDialog(beingDeletedPosition);
                }
            }
        }
    }

    private FetchUserCallback mFetchUserCallback = new FetchUserCallback() {
        @Override
        public void onFetchUserCallback(String sSID, String sID, String username, String email) {
            if (sSID == null || sID == null) {
                Intent intent = new Intent(P2rActivity.this,
                        HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                P2rActivity.this.startActivity(intent);
                P2rActivity.this.finish();
                return;
            }

            mDataStore.sSID = sSID;
            mDataStore.sID = sID;

            getFirstTimePagingPageUrls();
        }
    };

    private void getFirstTimePagingPageUrls() {
        boolean reconnect = mDataStore.reconnectGetPagingPageUrls(this,
                Constants.FT_GET_PAGE_URLS_LOADER_ID, mFirstTimeGetPageUrlsCallback);
        if (!reconnect) {
            mDataStore.getPagingPageUrls(this, Constants.FT_GET_PAGE_URLS_LOADER_ID,
                    mDataStore.sSID, mDataStore.sID, null, mFirstTimeGetPageUrlsCallback);
        }
    }

    private GetPagingPageUrlsCallback mFirstTimeGetPageUrlsCallback =
            new GetPagingPageUrlsCallback(){
        @Override
        public void onGetPagingPageUrlsCallback(Log log) {

            if (log == null) {
                mDataStore.msg = getString(R.string.connection_failed);
            } else {
                String value = log.getValue(Constants.GET_PAGING_PAGE_URLS, true);
                if (value != null) {
                    mDataStore.pageUrls.clear();

                    JSONObject jsonObj = new JSONObject(value);
                    JSONArray jsonPageUrls = jsonObj.getJSONArray(Constants.PAGE_URLS);
                    for (int i = 0; i < jsonPageUrls.length(); i++) {
                        JSONObject jsonPageUrl = jsonPageUrls.getJSONObject(i);
                        PageUrlObj pageUrl = new PageUrlObj(jsonPageUrl);
                        mDataStore.pageUrls.add(pageUrl);
                    }

                    mDataStore.cursorString = jsonObj.getString(Constants.CURSOR_STRING);

                    if (mDataStore.pageUrls.isEmpty()) {
                        mDataStore.msg = getResources().getString(R.string.no_data);
                        mDataStore.nextBtnStatus = NextBtnStatus.HIDE;
                    } else {
                        mDataStore.msg = null;
                        mDataStore.nextBtnStatus = NextBtnStatus.NORMAL;
                    }

                    mDataStore.didGetPageUrls = true;
                } else {
                    String msg = log.getMsg(Constants.GET_PAGING_PAGE_URLS, false, null);
                    if (msg != null) {
                        mDataStore.msg = msg;
                    }
                }
            }

            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.p2r, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem refreshMenuItem = menu.findItem(R.id.action_refresh);
        if (mDataStore.refreshBtnStatus == RefreshBtnStatus.NORMAL) {
            refreshMenuItem.setEnabled(true);
        } else if (mDataStore.refreshBtnStatus == RefreshBtnStatus.REFRESHING) {
            refreshMenuItem.setEnabled(false);
        } else {
            throw new IllegalArgumentException();
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mDataStore.pageUrls.isEmpty()) {
            mDataStore.msg = null;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mConfirmDeleteDialog != null && mConfirmDeleteDialog.isShowing()) {
            int beingDeletedPosition = (Integer) mConfirmDeleteDialog.getButton(
                    DialogInterface.BUTTON_POSITIVE).getTag();
            outState.putInt(Constants.BEING_DELETED_POSITION, beingDeletedPosition);
        }
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            String alertDialogMessage = (String) mAlertDialog.getButton(
                    DialogInterface.BUTTON_POSITIVE).getTag();
            outState.putString(Constants.MSG, alertDialogMessage);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (mConfirmDeleteDialog != null && mConfirmDeleteDialog.isShowing()) {
            mConfirmDeleteDialog.dismiss();
        }
        if (mLogOutDialog != null && mLogOutDialog.isShowing()) {
            mLogOutDialog.dismiss();
        }
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        
        super.onDestroy();
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

        // One at a time for now
        // TODO: Implement queue for deletion
        boolean foundDeleting = false;
        for (PageUrlObj pageUrl : mDataStore.pageUrls) {
            if (pageUrl.delBtnStatus == DelBtnStatus.DELETING) {
                foundDeleting = true;
                break;
            }
        }

        if (foundDeleting) {
            showAlertDialog("Queue is too long. "
                    + "Please wait until the deletion of others completes.");
            return;
        }

        showConfirmDeleteDialog(position);
    }

    private void onConfirmDelBtnClick(int position) {
        PageUrlObj pageUrl = mDataStore.pageUrls.get(position);
        pageUrl.delBtnStatus = DelBtnStatus.DELETING;
        pageUrl.resendBtnStatus = ResendBtnStatus.DISABLED;
        mAdapter.notifyDataSetChanged();

        mDataStore.deletePageUrl(this, Constants.DELETE_PAGE_URL_LOADER_ID,
                mDataStore.sSID, mDataStore.sID, pageUrl.keyString, mDeletePageUrlCallback);
    }

    private DeletePageUrlCallback mDeletePageUrlCallback = new DeletePageUrlCallback() {
        @Override
        public void onDeletePageUrlCallback(Log log) {

            // Find the one being deleted
            PageUrlObj pageUrl = null;
            for (PageUrlObj p : mDataStore.pageUrls) {
                if (p.delBtnStatus == DelBtnStatus.DELETING) {
                    pageUrl = p;
                    break;
                }
            }
            if (pageUrl == null) {
                throw new IllegalArgumentException();
            }

            pageUrl.delBtnStatus = DelBtnStatus.NORMAL;
            pageUrl.resendBtnStatus = ResendBtnStatus.NORMAL;

            if (log == null) {
                showAlertDialog(getString(R.string.connection_failed));
            } else {
                String keyString = log.getValue(Constants.DELETE_PAGE_URL, true);
                if (keyString != null) {
                    if (keyString.equals(pageUrl.keyString)) {
                        mDataStore.pageUrls.remove(pageUrl);

                        if (mDataStore.pageUrls.isEmpty()
                                && mDataStore.nextBtnStatus == NextBtnStatus.HIDE) {
                            mDataStore.msg = getResources().getString(R.string.no_data);
                        }
                    } else {
                        throw new IllegalArgumentException();
                    }
                } else {
                    String msg = log.getMsg(Constants.DELETE_PAGE_URL, false, null);
                    if (msg != null) {
                        showAlertDialog(msg);
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    };

    private void onResendBtnClick(int position) {

        // One at a time for now
        // TODO: Implement queue for resending
        boolean foundResending = false;
        for (PageUrlObj pageUrl : mDataStore.pageUrls) {
            if (pageUrl.resendBtnStatus == ResendBtnStatus.RESENDING) {
                foundResending = true;
                break;
            }
        }

        if (foundResending) {
            showAlertDialog("Queue is too long. "
                    + "Please wait until the resending of others completes.");
            return;
        }

        PageUrlObj pageUrl = mDataStore.pageUrls.get(position);
        pageUrl.resendBtnStatus = ResendBtnStatus.RESENDING;
        mAdapter.notifyDataSetChanged();

        mDataStore.resendToReader(this, Constants.RESEND_TO_READER_LOADER_ID, mDataStore.sSID,
                mDataStore.sID, pageUrl.keyString, mResendToReaderCallback);
    }

    private ResendToReaderCallback mResendToReaderCallback = new ResendToReaderCallback() {
        @Override
        public void onResendToReaderCallback(Log log) {

            // Find the one being resent
            PageUrlObj pageUrl = null;
            for (PageUrlObj p : mDataStore.pageUrls) {
                if (p.resendBtnStatus == ResendBtnStatus.RESENDING) {
                    pageUrl = p;
                    break;
                }
            }
            if (pageUrl == null) {
                throw new IllegalArgumentException();
            }

            pageUrl.resendBtnStatus = ResendBtnStatus.NORMAL;

            if (log == null) {
                showAlertDialog(getString(R.string.connection_failed));
            } else {
                String keyString = log.getValue(Constants.SEND_TO_READER, true);
                if (keyString != null) {
                    if (keyString.equals(pageUrl.keyString)) {
                        pageUrl.resendBtnStatus = ResendBtnStatus.SENT;
                    } else {
                        throw new IllegalArgumentException();
                    }
                } else {
                    String msg = log.getMsg(Constants.SEND_TO_READER, false, null);
                    if (msg != null) {
                        showAlertDialog(msg);
                    }
                }
            }

            mAdapter.notifyDataSetChanged();
        }
    };

    private void onRefreshBtnClick() {
        mDataStore.refreshBtnStatus = RefreshBtnStatus.REFRESHING;
        this.supportInvalidateOptionsMenu();

        mDataStore.getPagingPageUrls(this, Constants.RF_GET_PAGE_URLS_LOADER_ID, mDataStore.sSID,
                mDataStore.sID, null, mRefreshGetPageUrlsCallback);
    }

    private GetPagingPageUrlsCallback mRefreshGetPageUrlsCallback =
            new GetPagingPageUrlsCallback(){
        @Override
        public void onGetPagingPageUrlsCallback(Log log) {
            if (log == null) {
                mDataStore.msg = getString(R.string.connection_failed);
            } else {
                String value = log.getValue(Constants.GET_PAGING_PAGE_URLS, true);
                if (value != null) {
                    mDataStore.pageUrls.clear();

                    JSONObject jsonObj = new JSONObject(value);
                    JSONArray jsonPageUrls = jsonObj.getJSONArray(Constants.PAGE_URLS);
                    for (int i = 0; i < jsonPageUrls.length(); i++) {
                        JSONObject jsonPageUrl = jsonPageUrls.getJSONObject(i);
                        PageUrlObj pageUrl = new PageUrlObj(jsonPageUrl);
                        mDataStore.pageUrls.add(pageUrl);
                    }

                    mDataStore.cursorString = jsonObj.getString(Constants.CURSOR_STRING);

                    if (mDataStore.pageUrls.isEmpty()) {
                        mDataStore.msg = getResources().getString(R.string.no_data);
                        mDataStore.nextBtnStatus = NextBtnStatus.HIDE;
                    } else {
                        mDataStore.msg = null;
                        mDataStore.nextBtnStatus = NextBtnStatus.NORMAL;
                    }
                } else {
                    String msg = log.getMsg(Constants.GET_PAGING_PAGE_URLS, false, null);
                    if (msg != null) {
                        mDataStore.msg = msg;
                    }
                }
            }

            mAdapter.notifyDataSetChanged();

            mDataStore.refreshBtnStatus = RefreshBtnStatus.NORMAL;
            P2rActivity.this.supportInvalidateOptionsMenu();
        }
    };

    private void onNextBtnClick() {
        mDataStore.nextBtnStatus = NextBtnStatus.GETTING;
        mAdapter.notifyDataSetChanged();

        mDataStore.getPagingPageUrls(this, Constants.NE_GET_PAGE_URLS_LOADER_ID, mDataStore.sSID,
                mDataStore.sID, mDataStore.cursorString, mNextGetPageUrlsCallback);
    }

    private GetPagingPageUrlsCallback mNextGetPageUrlsCallback =
            new GetPagingPageUrlsCallback() {
        @Override
        public void onGetPagingPageUrlsCallback(Log log) {

            mDataStore.nextBtnStatus = NextBtnStatus.NORMAL;

            if (log == null) {
                showAlertDialog(getString(R.string.connection_failed));
            } else {
                String value = log.getValue(Constants.GET_PAGING_PAGE_URLS, true);
                if (value != null) {
                    JSONObject jsonObj = new JSONObject(value);
                    JSONArray jsonPageUrls = jsonObj.getJSONArray(Constants.PAGE_URLS);
                    for (int i = 0; i < jsonPageUrls.length(); i++) {
                        JSONObject jsonPageUrl = jsonPageUrls.getJSONObject(i);
                        PageUrlObj pageUrl = new PageUrlObj(jsonPageUrl);
                        mDataStore.pageUrls.add(pageUrl);
                    }

                    mDataStore.cursorString = jsonObj.getString(Constants.CURSOR_STRING);

                    if (jsonPageUrls.length() == 0) mDataStore.nextBtnStatus = NextBtnStatus.HIDE;

                    if (mDataStore.pageUrls.isEmpty()) {
                        mDataStore.msg = getResources().getString(R.string.no_data);
                    }
                } else {
                    String msg = log.getMsg(Constants.GET_PAGING_PAGE_URLS, false, null);
                    if (msg != null) {
                        showAlertDialog(msg);
                    }
                }
            }

            mAdapter.notifyDataSetChanged();
        }
    };

    private void onLogOutBtnClick() {
        showLogOutDialog();

        mDataStore.logOut(this, Constants.LOG_OUT_LOADER_ID, mDataStore.sSID, mDataStore.sID,
                mLogOutCallback);
    }

    private LogOutCallback mLogOutCallback = new LogOutCallback() {
        @Override
        public void onLogOutCallback(Log log) {
            mDataStore.deleteUser();

            // Clear all data
            mDataStore.sSID = null;
            mDataStore.sID = null;
            mDataStore.pageUrls.clear();
            mDataStore.cursorString = null;

            mDataStore.didGetPageUrls = false;
            mDataStore.msg = null;
            mDataStore.refreshBtnStatus = RefreshBtnStatus.NORMAL;
            mDataStore.nextBtnStatus = NextBtnStatus.NORMAL;

            mLogOutDialog.dismiss();

            Intent intent = new Intent(P2rActivity.this, HomeActivity.class);
            // Clear stack
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            P2rActivity.this.startActivity(intent);
            P2rActivity.this.finish();
        }
    };

    private void showConfirmDeleteDialog(int position) {
        if (mConfirmDeleteDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?")
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Button positiveButton = mConfirmDeleteDialog.getButton(
                                        DialogInterface.BUTTON_POSITIVE);
                                onConfirmDelBtnClick((Integer)positiveButton.getTag());
                                positiveButton.setTag(-1);
                            }
                        })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                mConfirmDeleteDialog.getButton(
                                        DialogInterface.BUTTON_POSITIVE).setTag(-1);
                                
                            }
                        });
            mConfirmDeleteDialog = builder.create();
        }
        mConfirmDeleteDialog.show();

        // Make sure the dialog is created.
        mConfirmDeleteDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTag(position);
    }

    private void showLogOutDialog() {
        if (mLogOutDialog == null) {
            mLogOutDialog = new ProgressDialog(this);
            mLogOutDialog.setMessage(getResources().getString(R.string.signing_out));
            mLogOutDialog.setCancelable(false);
        }
        mLogOutDialog.show();
    }

    private void showAlertDialog(String msg) {
        if (mAlertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTag(null);
                }
            });
            mAlertDialog = builder.create();
        }
        mAlertDialog.setMessage(msg);
        mAlertDialog.show();

        // Make sure the dialog is created.
        mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTag(msg);
    }
}
