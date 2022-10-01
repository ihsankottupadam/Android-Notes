package com.ihsan.notes;

public class CheckItem {
    int mid;
    int mImp;
    String mTitle;
    int mvalue;

    public CheckItem(int id, int imp, String title, int value) {
        mid = id;
        mImp = imp;
        mTitle = title;
        mvalue = value;

    }

    public void setItemStar(int val) {
        mImp = val;
    }

    public void setItemTitle(String title) {
        mTitle = title;
    }

    public void setItemChecked(int val) {
        mvalue = val;
    }
}