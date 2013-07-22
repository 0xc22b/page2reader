package com.wit.page2reader.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wit.page2reader.R;
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

    public LayoutInflater inflater;

    /**
     * 0: EntryView
     * 1: MsgView
     * 2: NextView
     */
    public int[] layouts;

    public ArrayList<PageUrlObj> pageUrls;

    public String msg;

    public boolean shownNext;

    public OnClickListener onDelBtnClickListener;

    public OnClickListener onResendBtnClickListener;

    public OnClickListener onNextBtnClickListener;

    public P2rAdapter(Context context, int[] layouts, ArrayList<PageUrlObj> pageUrls,
            OnClickListener onDelBtnClickListener, OnClickListener onResendBtnClickListener,
            OnClickListener onNextBtnClickListener) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        this.inflater = LayoutInflater.from(context);
        this.layouts = layouts;
        this.pageUrls = pageUrls;
        this.onDelBtnClickListener = onDelBtnClickListener;
        this.onResendBtnClickListener = onResendBtnClickListener;
        this.onNextBtnClickListener = onNextBtnClickListener;
    }

    @Override
    public int getCount() {
        if (msg != null) return 1;
        return shownNext ? pageUrls.size() + 1 : pageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        if (msg != null) {
            // Message view
            return null;
        }
        if (shownNext) {
            if (position == pageUrls.size()) {
                // Next view
                return null;
            } else if (position < pageUrls.size()) {
                return pageUrls.get(position);
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            return pageUrls.get(position);
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

        if (msg != null) {
            // Msg view
            if (convertView == null || holder.type != 1) {
                convertView = inflater.inflate(layouts[1], parent, false);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.type = 1;
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            }

            holder.text.setText(msg);

            return convertView;
        }

        if (shownNext && position >= pageUrls.size()) {
            if (position == pageUrls.size()) {
                // Next view
                if (convertView == null || holder.type != 2) {
                    convertView = inflater.inflate(layouts[2], parent, false);
    
                    // Creates a ViewHolder and store references to the two children views
                    // we want to bind data to.
                    holder = new ViewHolder();
                    holder.type = 2;
                    holder.btn1 = (Button) convertView.findViewById(R.id.nextBtn);

                    holder.btn1.setOnClickListener(onNextBtnClickListener);

                    convertView.setTag(holder);
                }
    
                return convertView;
            } else {
                throw new IllegalArgumentException();
            }
        }

        // Entry view
        if (convertView == null || holder.type != 0) {
            convertView = inflater.inflate(layouts[0], parent, false);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.type = 0;
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.btn1 = (Button) convertView.findViewById(R.id.delBtn);
            holder.btn2 = (Button) convertView.findViewById(R.id.resendBtn);

            holder.btn1.setOnClickListener(onDelBtnClickListener);
            holder.btn2.setOnClickListener(onResendBtnClickListener);

            convertView.setTag(holder);
        }

        PageUrlObj pageUrlObj = pageUrls.get(position);
        holder.title.setText(pageUrlObj.title);
        holder.text.setText(pageUrlObj.text);
        holder.btn1.setTag(position);
        holder.btn2.setTag(position);
        return convertView;
    }
}
