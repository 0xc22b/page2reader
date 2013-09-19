package com.wit.page2reader.activities;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.wit.page2reader.Constants;
import com.wit.page2reader.Constants.NextBtnStatus;
import com.wit.page2reader.Constants.PageUrlObjStatus;
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
import com.wit.page2reader.model.Log.LogInfo;
import com.wit.page2reader.model.PageUrlObj;

public class P2rActivity extends SherlockFragmentActivity {

    public static final int EDITOR_REQUEST_CODE = 1;

    private DataStore mDataStore;

    private OnClickListener mOnTitleClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer)v.getTag();
            String urlString = mDataStore.pageUrls.getWithNormalStatus(position).pUrl;

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

    private int[] mDeleteQueue = new int[7];
    private int[] mResendQueue = new int[7];

    private HashMap<String, Integer> mItemTopMap = new HashMap<String, Integer>();

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

                // Delete button's status was kept in DataStore already
                mDeleteQueue = savedInstanceState.getIntArray(Constants.DELETE_QUEUE);
                for (int i = 0; i < mDeleteQueue.length; i++) {
                    if (mDeleteQueue[i] == 1) {
                        int loaderID = Constants.DELETE_PAGE_URL_LOADER_ID + i;
                        mDataStore.reconnectDeletePageUrl(this, loaderID, mDeletePageUrlCallback);
                    }
                }

                // Resend button's status was kept in DataStore already
                mResendQueue = savedInstanceState.getIntArray(Constants.RESEND_QUEUE);
                for (int i = 0; i < mResendQueue.length; i++) {
                    if (mResendQueue[i] == 1) {
                        int loaderID = Constants.RESEND_TO_READER_LOADER_ID + i;
                        mDataStore.reconnectResendToReader(this, loaderID, mResendToReaderCallback);
                    }
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

                    if (mDataStore.pageUrls.isEmptyWithNormalStatus()) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDITOR_REQUEST_CODE && resultCode == Activity.RESULT_FIRST_USER) {
            Intent intent = new Intent(P2rActivity.this, HomeActivity.class);
            this.startActivity(intent);
            this.finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mDataStore.pageUrls.isEmptyWithNormalStatus()) {
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
        outState.putIntArray(Constants.DELETE_QUEUE, mDeleteQueue);
        outState.putIntArray(Constants.RESEND_QUEUE, mResendQueue);
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
            P2rActivity.this.startActivityForResult(intent, EDITOR_REQUEST_CODE);
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
        showConfirmDeleteDialog(position);
    }

    private void onConfirmDelBtnClick(int deletedPosition) {

        int queueIndex;
        for (queueIndex = 0; queueIndex < mDeleteQueue.length; queueIndex++) {
            if (mDeleteQueue[queueIndex] == 0) {
                mDeleteQueue[queueIndex] = 1;
                break;
            }
        }

        if (queueIndex >= mDeleteQueue.length) {
            showAlertDialog("Network is too busy. "
                    + "Please wait until the deletion of others completes.");
            return;
        }

        // Freeze the screen for animation on deletion
        mListView.setEnabled(false);

        final PageUrlObj pageUrl = mDataStore.pageUrls.getWithNormalStatus(deletedPosition);

        // Prepare for an animation
        int firstVisiblePosition = mListView.getFirstVisiblePosition();

        for (int i = 0; i < mListView.getChildCount(); i++) {
            int position = firstVisiblePosition + i;
            if (position == deletedPosition) {
                continue;
            }

            View child = mListView.getChildAt(i);
            String key = mAdapter.getItemKey(position);
            mItemTopMap.put(key, child.getTop());
        }

        int visibleDeletedPosition = deletedPosition - firstVisiblePosition;
        View deletedChild = mListView.getChildAt(visibleDeletedPosition);
        final int deletedChildHeight = deletedChild.getHeight() + mListView.getDividerHeight();

        final int loaderID = Constants.DELETE_PAGE_URL_LOADER_ID + queueIndex;

        final Runnable onAnimationEnd = new Runnable() {
            public void run() {
                mListView.setEnabled(true);

                mDataStore.deletePageUrl(P2rActivity.this,
                                         loaderID,
                                         mDataStore.sSID,
                                         mDataStore.sID,
                                         pageUrl.keyString,
                                         mDeletePageUrlCallback);
            }
        };

        final ViewTreeObserver observer = mListView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);

                boolean firstAnimation = true;
                int firstVisiblePosition = mListView.getFirstVisiblePosition();
                for (int i = 0; i < mListView.getChildCount(); ++i) {
                    View child = mListView.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    String key = mAdapter.getItemKey(position);
                    if (key.equals(Constants.MSG)) {
                        // No animatin on MsgView
                        continue;
                    }
                    int top = child.getTop();
                    Integer startTop = mItemTopMap.get(key);
                    if (startTop == null) {
                        startTop = top + (i > 0 ? deletedChildHeight
                                : -deletedChildHeight);
                    }
                    int delta = startTop - top;
                    if (delta != 0) {
                        final Runnable endAction = firstAnimation ? onAnimationEnd: null;
                        firstAnimation = false;

                        TranslateAnimation translator = new TranslateAnimation(0, 0, delta, 0);

                        // Duration depends on height in dp
                        DisplayMetrics metrics = getResources().getDisplayMetrics();
                        int duration = (int)((deletedChildHeight / metrics.density) * 1);
                        translator.setDuration(duration);

                        child.startAnimation(translator);
                        if (endAction != null) {
                            child.getAnimation().setAnimationListener(
                                    new AnimationListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    endAction.run();
                                }
                            });
                        }
                    }
                }
                mItemTopMap.clear();
                return true;
            }
        });

        // Be careful, don't change the status before preparing for animation
        //     indexes might get wrong.
        pageUrl.status = PageUrlObjStatus.BEING_DELETED;

        if (mDataStore.pageUrls.isEmptyWithNormalStatus()
                && mDataStore.nextBtnStatus == NextBtnStatus.HIDE) {
            mDataStore.msg = getResources().getString(R.string.no_data);
        }

        mAdapter.notifyDataSetChanged();
    }

    private DeletePageUrlCallback mDeletePageUrlCallback = new DeletePageUrlCallback() {
        @Override
        public void onDeletePageUrlCallback(int loaderID, String keyString, Log log) {

            // Find the one being deleted
            PageUrlObj pageUrl = null;
            for (PageUrlObj p : mDataStore.pageUrls) {
                if (p.keyString.equals(keyString)) {
                    pageUrl = p;
                    break;
                }
            }
            if (pageUrl == null) {
                throw new IllegalArgumentException();
            }

            if (log == null) {
                showAlertDialog(getString(R.string.connection_failed));

                pageUrl.status = PageUrlObjStatus.NORMAL;
                if (mDataStore.msg != null
                        && mDataStore.msg.equals(getResources().getString(R.string.no_data))) {
                    mDataStore.msg = null;
                }
                mAdapter.notifyDataSetChanged();
                return;
            }

            String msg = log.getMsg(Constants.DELETE_PAGE_URL, false, keyString);
            if (msg != null) {
                showAlertDialog(msg);

                pageUrl.status = PageUrlObjStatus.NORMAL;
                if (mDataStore.msg !=null
                        && mDataStore.msg.equals(getResources().getString(R.string.no_data))) {
                    mDataStore.msg = null;
                }
                mAdapter.notifyDataSetChanged();
                return;
            }

            LogInfo logInfo = log.getLogInfo(Constants.DELETE_PAGE_URL, true, keyString);
            if (logInfo != null) {
                mDataStore.pageUrls.remove(pageUrl);
                mDeleteQueue[loaderID - Constants.DELETE_PAGE_URL_LOADER_ID] = 0;
                return;
            }

            throw new IllegalArgumentException();
        }
    };

    private void onResendBtnClick(int position) {

        int queueIndex;
        for (queueIndex = 0; queueIndex < mResendQueue.length; queueIndex++) {
            if (mResendQueue[queueIndex] == 0) {
                mResendQueue[queueIndex] = 1;
                break;
            }
        }

        if (queueIndex >= mResendQueue.length) {
            showAlertDialog("Network is too busy. Please try again later.");
            return;
        }

        PageUrlObj pageUrl = mDataStore.pageUrls.getWithNormalStatus(position);
        pageUrl.resendBtnStatus = ResendBtnStatus.RESENDING;
        mAdapter.notifyDataSetChanged();

        int loaderID = Constants.RESEND_TO_READER_LOADER_ID + queueIndex;

        mDataStore.resendToReader(this, loaderID, mDataStore.sSID,
                mDataStore.sID, pageUrl.keyString, mResendToReaderCallback);
    }

    private ResendToReaderCallback mResendToReaderCallback = new ResendToReaderCallback() {
        @Override
        public void onResendToReaderCallback(int loaderID, String keyString, Log log) {

            // Find the one being resent
            PageUrlObj pageUrl = null;
            for (PageUrlObj p : mDataStore.pageUrls) {
                if (p.keyString.equals(keyString)) {
                    pageUrl = p;
                    break;
                }
            }
            if (pageUrl == null) {
                // Might already be deleted
                return;
            }

            pageUrl.resendBtnStatus = ResendBtnStatus.NORMAL;

            if (log == null) {
                showAlertDialog(getString(R.string.connection_failed));
            } else {
                String msg = log.getMsg(Constants.SEND_TO_READER, false, keyString);
                if (msg != null) {
                    showAlertDialog(msg);
                } else {
                    LogInfo logInfo = log.getLogInfo(Constants.SEND_TO_READER, true, keyString);
                    if (logInfo != null) {
                        pageUrl.resendBtnStatus = ResendBtnStatus.SENT;
                        mResendQueue[loaderID - Constants.RESEND_TO_READER_LOADER_ID] = 0;
                    } else {
                        throw new IllegalArgumentException();
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

                    if (mDataStore.pageUrls.isEmptyWithNormalStatus()) {
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

                    if (mDataStore.pageUrls.isEmptyWithNormalStatus()) {
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

    /**
     * Utility, to avoid having to implement every method in AnimationListener in
     * every implementation class
     */
    static class AnimationListenerAdapter implements AnimationListener {

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }
}
