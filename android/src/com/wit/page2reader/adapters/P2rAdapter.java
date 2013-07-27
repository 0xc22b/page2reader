package com.wit.page2reader.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wit.page2reader.Constants.DelBtnStatus;
import com.wit.page2reader.Constants.NextBtnStatus;
import com.wit.page2reader.Constants.ResendBtnStatus;
import com.wit.page2reader.R;
import com.wit.page2reader.model.DataStore;
import com.wit.page2reader.model.PageUrlObj;

public class P2rAdapter extends BaseAdapter {

    private static class ViewHolder {
        /*
         * Type of layouts (an index of mLayouts)
         */
        int type;
        TextView title;
        TextView text;
        Button btn1;
        Button btn2;
    }

    private Context mContext;

    private LayoutInflater mInflater;

    /**
     * 0: EntryView
     * 1: MsgView
     * 2: NextView
     */
    private int[] mLayouts;

    private DataStore mDataStore;

    private OnClickListener mOnTitleClickListener;

    private OnClickListener mOnDelBtnClickListener;

    private OnClickListener mOnResendBtnClickListener;

    private OnClickListener mOnNextBtnClickListener;

    public P2rAdapter(Context context, int[] layouts, DataStore dataStore,
            OnClickListener onTitleClickListener, OnClickListener onDelBtnClickListener,
            OnClickListener onResendBtnClickListener, OnClickListener onNextBtnClickListener) {
        this.mContext = context;
        // Cache the LayoutInflate to avoid asking for a new one each time.
        this.mInflater = LayoutInflater.from(context);
        this.mLayouts = layouts;
        this.mDataStore = dataStore;
        this.mOnTitleClickListener = onTitleClickListener;
        this.mOnDelBtnClickListener = onDelBtnClickListener;
        this.mOnResendBtnClickListener = onResendBtnClickListener;
        this.mOnNextBtnClickListener = onNextBtnClickListener;
    }

    @Override
    public int getCount() {
        if (mDataStore.msg != null) return 1;
        return mDataStore.nextBtnStatus == NextBtnStatus.HIDE ? mDataStore.pageUrls.size()
                : mDataStore.pageUrls.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (mDataStore.msg != null) {
            // Message view
            return null;
        }
        if (mDataStore.nextBtnStatus == NextBtnStatus.HIDE) {
            return mDataStore.pageUrls.get(position);
        } else {
            if (position == mDataStore.pageUrls.size()) {
                // Next view
                return null;
            } else if (position < mDataStore.pageUrls.size()) {
                return mDataStore.pageUrls.get(position);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder holder = null;
        if (convertView != null) {
            // Get the ViewHolder back to get fast access to the TextViews.
            holder = (ViewHolder) convertView.getTag();
        }

        if (mDataStore.msg != null) {
            // Msg view
            if (convertView == null || holder.type != 1) {
                convertView = mInflater.inflate(mLayouts[1], parent, false);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.type = 1;
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            }

            holder.text.setText(mDataStore.msg);

            return convertView;
        }

        if (mDataStore.nextBtnStatus != NextBtnStatus.HIDE
                && position >= mDataStore.pageUrls.size()) {
            if (position == mDataStore.pageUrls.size()) {
                // Next view
                if (convertView == null || holder.type != 2) {
                    convertView = mInflater.inflate(mLayouts[2], parent, false);

                    // Creates a ViewHolder and store references to the two children views
                    // we want to bind data to.
                    holder = new ViewHolder();
                    holder.type = 2;
                    holder.btn1 = (Button) convertView.findViewById(R.id.nextBtn);

                    holder.btn1.setOnClickListener(mOnNextBtnClickListener);

                    convertView.setTag(holder);
                }

                if (mDataStore.nextBtnStatus == NextBtnStatus.NORMAL) {
                    holder.btn1.setClickable(true);
                    holder.btn1.setTextColor(mContext.getResources().getColor(R.color.btn_text));
                } else if (mDataStore.nextBtnStatus == NextBtnStatus.GETTING) {
                    holder.btn1.setClickable(false);
                    holder.btn1.setTextColor(mContext.getResources().getColor(
                            R.color.disabled_btn_text));
                } else {
                    throw new IllegalArgumentException();
                }
                return convertView;
            } else {
                throw new IllegalArgumentException();
            }
        }

        // Entry view
        if (convertView == null || holder.type != 0) {
            convertView = mInflater.inflate(mLayouts[0], parent, false);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.type = 0;
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.btn1 = (Button) convertView.findViewById(R.id.delBtn);
            holder.btn2 = (Button) convertView.findViewById(R.id.resendBtn);

            holder.title.setOnClickListener(mOnTitleClickListener);
            holder.btn1.setOnClickListener(mOnDelBtnClickListener);
            holder.btn2.setOnClickListener(mOnResendBtnClickListener);

            convertView.setTag(holder);
        }

        PageUrlObj pageUrlObj = mDataStore.pageUrls.get(position);
        holder.title.setText(pageUrlObj.title);
        holder.title.setTag(position);
        holder.text.setText(pageUrlObj.text);
        holder.btn1.setTag(position);
        holder.btn2.setTag(position);

        if (pageUrlObj.delBtnStatus == DelBtnStatus.NORMAL) {
            holder.btn1.setClickable(true);
            holder.btn1.setTextColor(mContext.getResources().getColor(
                    R.color.btn_text));
        } else if (pageUrlObj.delBtnStatus == DelBtnStatus.DELETING) {
            holder.btn1.setClickable(false);
            holder.btn1.setTextColor(mContext.getResources().getColor(
                    R.color.disabled_btn_text));
        } else {
            throw new IllegalArgumentException();
        }

        if (pageUrlObj.resendBtnStatus == ResendBtnStatus.NORMAL) {
            holder.btn2.setClickable(true);
            holder.btn2.setTextColor(mContext.getResources().getColor(
                    R.color.btn_text));
            holder.btn2.setText(mContext.getResources().getString(R.string.resend));
        } else if (pageUrlObj.resendBtnStatus == ResendBtnStatus.RESENDING) {
            holder.btn2.setClickable(false);
            holder.btn2.setTextColor(mContext.getResources().getColor(
                    R.color.disabled_btn_text));
            holder.btn2.setText(mContext.getResources().getString(R.string.sending));
        } else if (pageUrlObj.resendBtnStatus == ResendBtnStatus.DISABLED) {
            holder.btn2.setClickable(false);
            holder.btn2.setTextColor(mContext.getResources().getColor(
                    R.color.disabled_btn_text));
            holder.btn2.setText(mContext.getResources().getString(R.string.resend));
        } else if (pageUrlObj.resendBtnStatus == ResendBtnStatus.SENT) {
            holder.btn2.setClickable(false);
            holder.btn2.setTextColor(mContext.getResources().getColor(
                    R.color.disabled_btn_text));
            holder.btn2.setText(mContext.getResources().getString(R.string.sent));
        } else {
            throw new IllegalArgumentException();
        }

        return convertView;
    }
}
