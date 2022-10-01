package com.ihsan.notes;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageButton;


public class Mcatlist_adapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> itemsArrayList;

    public Mcatlist_adapter(Context context, ArrayList<String> itemsArrayList) {

        super(context, R.layout.category_list, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.category_list, parent, false);
        TextView labelView = (TextView) rowView.findViewById(R.id.title);
        labelView.setText(itemsArrayList.get(position).toString());
        ImageButton btndlt = (ImageButton) rowView.findViewById(R.id.btnDeleteCat);
        ImageButton btnrename = (ImageButton) rowView.findViewById(R.id.btnRenameCat);
        if (itemsArrayList.get(position).toString().equals("Notes")) {
            btndlt.setVisibility(View.GONE);
            btnrename.setVisibility(View.GONE);
        }
        return rowView;
    }
}