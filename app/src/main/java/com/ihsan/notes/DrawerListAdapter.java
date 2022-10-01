package com.ihsan.notes;

import java.util.ArrayList ;
import android.app.Activity ;
import android.content.Context;
import android.view.LayoutInflater ;
import android.view.View ;
import android.view.ViewGroup ;
import android.widget.BaseAdapter ;
import android.widget.ImageView ;
import android.widget.TextView;

public class DrawerListAdapter extends
BaseAdapter {
	Context mContext;
	ArrayList<NavItem> mNavItems;
	public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
		mContext = context;
		mNavItems = navItems;
	}
	@Override
	public int getCount() {
		return mNavItems.size();
	}
	@Override
	public Object getItem(int position) {
		return mNavItems.get(position);
	}
	@Override
	public long getItemId(int position) {
		return 0 ;
	}
	@Override
	public View getView( int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.drawer_item, null);
		}
		else {
			view = convertView;
		}
		TextView titleView = (TextView) view.findViewById(R.id.title);
		TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
		ImageView iconView = (ImageView) view.findViewById(R.id.icon);
		titleView.setText( mNavItems.get(position).mTitle );
		subtitleView.setText( mNavItems.get(position).mSubtitle );
		iconView.setImageResource(mNavItems.get(position).mIcon);
		return view;
	}
}