package com.ihsan.notes;

public class NoteItem {
    int nId;
    String nType;
    String nTitle;
    String nContent;
    String nUrl;
    long nModified;

    public NoteItem(int id, String type, String title, String content, long lm, String Url) {
        nId = id;
        nType = type;
        nTitle = title;
        nContent = content;
        nModified = lm;
        nUrl = Url;
    }
}