package com.ihsan.notes;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.*;
import android.service.quicksettings.*;
import android.graphics.*;

public class CheckList_Adapter extends BaseAdapter {
    Context mContext;
    ArrayList<CheckItem> mCheckItems;

    public CheckList_Adapter(Context context, ArrayList<CheckItem> noteitems) {
        mContext = context;
        mCheckItems = noteitems;
    }

    @Override
    public int getCount() {
        return mCheckItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mCheckItems.get(position);
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
            view = inflater.inflate(R.layout.checkitem, null);
        } else {
            view = convertView;
        }
        TextView title = (TextView) view.findViewById(R.id.check_title);
        LinearLayout itemImp = (LinearLayout) view.findViewById(R.id.lay_imp);
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.check_itemCheckBox);
        ImageButton morebtn = (ImageButton) view.findViewById(R.id.btnMore);
        ImageButton deletebtn = (ImageButton) view.findViewById(R.id.btnDeleteItem);
        int imp = mCheckItems.get(position).mImp;
        int val = mCheckItems.get(position).mvalue;
        if (imp == 1) {
            itemImp.setBackgroundColor(0xffff5645);
        } else {
            itemImp.setBackgroundColor(0xffdedede);
        }
        if (val == 1) {
            checkbox.setChecked(true);
            title.setTextColor(0x99000000);
            title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            deletebtn.setVisibility(View.VISIBLE);
        } else {
            checkbox.setChecked(false);
            title.setTextColor(0xff000000);
            title.
                    setPaintFlags(title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            deletebtn.setVisibility(View.GONE);
        }
        title.setText(mCheckItems.get(position).mTitle);

        return view;
    }

    public void add(CheckItem hj) {
        mCheckItems.add(hj);
        notifyDataSetChanged();
    }

    public int indexOfIdb(int _n) {
        for (int i = 0; i < mCheckItems.size(); i++) {
            if (mCheckItems.get(i).mid == _n) {
                return i;
            }
        }
        return -1;
    }

    public String toSharetext() {
        String text = "";
        for (int i = 0; i < mCheckItems.size(); i++) {
            String check = (mCheckItems.get(i).mvalue == 1) ? "[✓]" : "[　]";
            text += check + " " + mCheckItems.get(i).mTitle + System.getProperty("line.separator");
        }
        return text;
    }
}