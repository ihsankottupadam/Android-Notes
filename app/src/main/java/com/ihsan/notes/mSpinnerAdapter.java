package com.ihsan.notes;

import java.util.ArrayList;

import android.content.Context;
import android.widget.*;
import android.view.*;

public class mSpinnerAdapter extends ArrayAdapter<String> {
    ArrayList<String> stringList;
    Context context;

    public mSpinnerAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        stringList = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = layoutInflater.inflate(R.layout.spinner_item, parent, false);
        TextView textView = (TextView) rootView.findViewById(R.id.title);
        ImageView icon1 = (ImageView) rootView.findViewById(R.id.icon);
        icon1.setVisibility(View.GONE);
        ImageView icon = (ImageView) rootView.findViewById(R.id.icon1);
        LinearLayout linear = (LinearLayout) rootView.findViewById(R.id.linear);
        linear.setBackgroundResource(R.drawable.button_box_light_selector);
        icon.setImageResource(R.drawable.ic_list_icon);
        icon.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = layoutInflater.inflate
                (R.layout.spinner_item, parent, false);
        TextView textView = (TextView) rootView.findViewById(R.id.title);
        ImageView imageView =
                (ImageView) rootView.findViewById(R.id.icon);
        imageView.setVisibility(View.VISIBLE);
        textView.setText(stringList.get(position));
        return rootView;
    }
}
