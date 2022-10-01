package com.ihsan.notes;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Notelist_Adapter extends BaseAdapter {
    Context mContext;
    ArrayList<NoteItem> mNoteItems;
    String lmformated;

    public Notelist_Adapter(Context context, ArrayList<NoteItem> noteitems) {
        mContext = context;
        mNoteItems = noteitems;
    }

    @Override
    public int getCount() {
        return mNoteItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNoteItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.note_item, null);
        } else {
            view = convertView;
        }
        TextView titletext = (TextView) view.findViewById(R.id.ntitle);
        TextView contenttext = (TextView) view.findViewById(R.id.nDescription);
        TextView datetext = (TextView) view.findViewById(R.id.nDate);
        String title = mNoteItems.get(position).nTitle;
        String content = mNoteItems.get(position).nContent;
        if (!title.isEmpty()) {
            titletext.setText(title);
            titletext.setTextColor(0xFf000000);
        } else {
            titletext.setText("No title");
            titletext.setTextColor(0xFF676767);
        }
        if (!content.isEmpty()) {
            contenttext.setText(content);
            contenttext.setTextColor(0xFF000000);
        } else {
            contenttext.setText("No text");
            contenttext.setTextColor(0xFF676767);
        }
        long lm = mNoteItems.get(position).nModified;
        lmformated = Utility.getTimeAgo(lm, mContext);
        datetext.setText(lmformated);

        return view;
    }
}