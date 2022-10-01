package com.ihsan.notes;

import android.app.*;
import android.os.Bundle;
import android.os.Environment;
import android.os.Build;
import android.widget.*;
import android.view.View;
import android.view.Menu;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.view.View.*;
import android.view.*;
import android.provider.CalendarContract;
import android.provider.CalendarContract.*;
import android.graphics.*;
import android.view.inputmethod.EditorInfo;


import java.io.*;
import java.util.*;

//import org.apache.commons.codec.binary.*;
//import org.apache.http.util.*;
import android.support.v4.util.*;
import android.text.*;


public class NoteDisplayActivity extends Activity {
    private String Title;
    private String CurrentFileUrl;
    private String category;
    private int fileId;
    private int checkId;
    private String line;
    private boolean isnewlist;
    private int fontSizeInt;
    private ArrayList<CheckItem> mcheckitems = new ArrayList<CheckItem>();
    private boolean isTouchToEdit = true;
    private ArrayList<String> currChecklistitems = new ArrayList<String>();

    private EditText dTitle;
    private TextView dcontent;
    private ListView checklist;
    private EditText itemaddtext;
    private ScrollView displayScroll;
    private LinearLayout checkDiv;
    private LinearLayout linearadditem;
    private ImageButton btnresetadd;
    private ImageView imgaddicn;

    private CheckList_Adapter adap;
    private SharedPreferences S_pref;
    private SharedPreferences pref;
    private File currentFile;
    private File NewFileDir;
    private File NewFile;
    private Context context;
    StringBuilder text;
    String type;
    String Action;
    private String NewfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notedisplay);

        context = this;
        category = getIntent().getStringExtra("Category");
        Context context = this;
        S_pref = getSharedPreferences("init_pref", Activity.MODE_PRIVATE);
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        Action = getIntent().getStringExtra("Action");
        category = getIntent().getStringExtra("Category");

        if (Action.equals("View")) {
            Title = getIntent().getStringExtra("Title");
            fileId = Integer.parseInt(getIntent().getStringExtra("FileId"));
            type = getIntent().getStringExtra("fType");
            CurrentFileUrl = getIntent().getStringExtra("cFile");
            currentFile = new File(CurrentFileUrl, "");
        } else {
            isnewlist = true;
            Title = "";
            fileId = S_pref.getInt("FileId", 0);
            type = "Checklist";
        }
        btnresetadd = (ImageButton) findViewById(R.id.btnResetItem);
        imgaddicn = (ImageView) findViewById(R.id.itemadd_plus);
        dTitle = (EditText) findViewById(R.id.note_tite);
        dcontent = (TextView) findViewById(R.id.note_maintext);
        checklist = (ListView) findViewById(R.id.checklist);
        checkDiv = (LinearLayout) findViewById(R.id.checkDiv);
        itemaddtext = (EditText) findViewById(R.id.itemaddtext);
        displayScroll = (ScrollView) findViewById(R.id.notedisplayScroll);
        linearadditem = (LinearLayout) findViewById(R.id.linear_itemAdd);
        if (!type.equals("Checklist")) {
            setnote();
        } else {
            setchecklist();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (type.equals("Note")) {
            getMenuInflater().inflate(R.menu.view, menu);
            return true;
        } else {
            getMenuInflater().inflate(R.menu.checklist, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ACTION_Edit:
                Intent intent_view = new Intent();
                intent_view.setClass(getApplicationContext(), EditActivity.class);
                intent_view.putExtra("Title", Title);
                intent_view.putExtra("FileId", String.valueOf(fileId));
                intent_view.putExtra("cFile", CurrentFileUrl);
                intent_view.putExtra("Content", text.toString());
                intent_view.putExtra("Category", category);
                intent_view.putExtra("Action", "UPDATE");
                intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_view);
                return true;
            case R.id.ACTION_DELETE:
                Delete();
                return true;
            case R.id.ACTION_SHARE:
                Intent sharingIntent = new
                        Intent
                        (android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra
                        (android.content.Intent.EXTRA_SUBJECT, Title);
                sharingIntent.putExtra
                        (android.content.Intent.EXTRA_TEXT, text.toString());
                startActivity
                        (Intent.createChooser
                                (sharingIntent, "Share via"));
                return true;
            case R.id.ACTION_SAVEASFILE:
                if (Title == "" || Title == " ") {
                    NewfileName = "NoteFile";
                } else {
                    NewfileName = Title;
                }

                NewFileDir = new File("/storage/emulated/0/Notes/Exported");
                if (SaveNote(text.toString())) {
                    showMessage("Saved in " + NewFileDir.toString());
                } else {
                    showMessage("Saving Failed");
                }
                return true;
            case R.id.ACTION_ADD_TO_CALENDER:
                addEvent();
                return true;
            case R.id.ACTION_TOLIST:
                toChecklist();
                return true;

            case R.id.ACTION_DEL_LIST:
                deleteList();
                return true;
            case R.id.ACTION_SHARE_LIST:
                Intent intent = new
                        Intent
                        (android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra
                        (android.content.Intent.EXTRA_SUBJECT, Title + " Checklist");
                intent.putExtra
                        (android.content.Intent.EXTRA_TEXT, adap.toSharetext());
                startActivity
                        (Intent.createChooser
                                (intent, "Share via"));
                return true;
            case R.id.ACTION_TONOTE:
                toTextNote();
                return true;
            case R.id.ACTION_DEL_SEL_LIST:
                deleteChecked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (isnewlist && currChecklistitems.size() != 0) {
            NewFileDir = new File("/data/data/com.ihsan.notes/note/" + category);
            NewfileName = String.valueOf(fileId);
            SaveNote("");
            S_pref.edit().putString(fileId + "T", dTitle.getText().toString()).commit();
            S_pref.edit().putString(fileId + "C", "Checklist").commit();
            S_pref.edit().putString(fileId + "type", "Checklist").commit();
            S_pref.edit().putInt("FileId", fileId + 1).commit();
            isnewlist = false;
        }
        Intent intent_view = new Intent();
        intent_view.setClass(getApplicationContext(), MainActivity.class);
        intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent_view.putExtra("State", "Open");
        startActivity(intent_view);

    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void setnote() {
        dcontent.setVisibility(View.VISIBLE);
        checklist.setVisibility(View.GONE);
        checkDiv.setVisibility(View.GONE);
        linearadditem.setVisibility(View.GONE);
        Context context = this;
        dTitle.setText(Title);
        isTouchToEdit = pref.getBoolean("isTouchToEdit", false);
        String FontSize = pref.getString("textSize", "Medium (Default)");
        switch (FontSize) {
            case "Small":
                fontSizeInt = R.style.FontSmall;
                break;
            case "Medium (Default)":
                fontSizeInt = R.style.FontMedium;
                break;
            case "Large":
                fontSizeInt = R.style.FontLarge;
                break;
            case "Extra large":
                fontSizeInt = R.style.FontExtraLarge;
                break;
            default:
                break;
        }
        if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.M) {
            dcontent.setTextAppearance
                    (context, fontSizeInt);
        } else {
            dcontent.setTextAppearance(fontSizeInt);
        }
        text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(currentFile));
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            showMessage("An error occured");
        }
        dcontent.setText(text.toString());
        dcontent.setOnTouchListener(new OnTouchListener() {
            private long startClickTime;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (isTouchToEdit) {
                    if (event.getAction() ==
                            MotionEvent.ACTION_DOWN) {
                        startClickTime =
                                System.currentTimeMillis();
                    } else if (event.getAction()
                            == MotionEvent.ACTION_UP) {
                        if (System.currentTimeMillis() - startClickTime <
                                ViewConfiguration.getTapTimeout()) {
                            Intent intent_view = new Intent();
                            intent_view.setClass(getApplicationContext(), EditActivity.class);
                            intent_view.putExtra("Title", Title);
                            intent_view.putExtra("FileId", String.valueOf(fileId));
                            intent_view.putExtra("cFile", CurrentFileUrl);
                            intent_view.putExtra("Content", text.toString());
                            intent_view.putExtra("Category", category);
                            intent_view.putExtra("Action", "UPDATE");
                            intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_view);
                        }
                    }
                } else {
                    return onTouchEvent(event);
                }
                return true;
            }
        });

    }

    private void setchecklist() {
        dcontent.setVisibility(View.GONE);
        checklist.setVisibility(View.VISIBLE);
        checkDiv.setVisibility(View.VISIBLE);
        linearadditem.setVisibility(View.VISIBLE);
        if (isnewlist) {
            dTitle.setFocusableInTouchMode(true);
            dTitle.requestFocus();
        } else {
            dTitle.setText(Title);
        }
        checkId = S_pref.getInt("checkId", 0);
        currChecklistitems.clear();
        String temp = S_pref.getString(fileId + "checkids", "");
        if (!temp.equals("")) {
            currChecklistitems = convertToArray(temp);
        }
        for (int i = 0; i < currChecklistitems.size(); i++) {
            int cId = Integer.parseInt(currChecklistitems.get(i));
            mcheckitems.add(new CheckItem(cId, S_pref.getInt(cId + "Imp", 0), S_pref.getString(cId + "Title", ""), S_pref.getInt(cId + "Value", 0)));
        }
        adap = new CheckList_Adapter(this, mcheckitems);
        checklist.setAdapter(adap);
        Utility.setListViewHeightBasedOnChildren(checklist);
        checklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView item = (TextView) view.findViewById(R.id.check_title);
                CheckBox checkbox = (CheckBox) view.findViewById(R.id.check_itemCheckBox);
                ImageButton btnRemove = (ImageButton) view.findViewById(R.id.btnDeleteItem);
                if (!checkbox.isChecked()) {
                    checkbox.setChecked(true);
                    item.setTextColor(0x99000000);
                    mcheckitems.get(position).setItemChecked(1);
                    item.setPaintFlags(item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    btnRemove.setVisibility(View.VISIBLE);
                    S_pref.edit().putInt(mcheckitems.get(position).mid + "Value", 1).commit();
                } else {
                    checkbox.setChecked(false);
                    item.setTextColor(0xff000000);
                    item.setPaintFlags(item.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    mcheckitems.get(position).setItemChecked(0);
                    btnRemove.setVisibility(View.GONE);
                    S_pref.edit().putInt(mcheckitems.get(position).mid + "Value", 0).commit();
                }
            }
        });
        checklist.post(new Runnable() {
            @Override
            public void run() {
                Utility.setListViewHeightBasedOnChildren(checklist);
            }
        });
        dTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _v) {
                dTitle.setFocusableInTouchMode(true);
                dTitle.requestFocus();
            }
        });
        itemaddtext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0,
                                      boolean hasfocus) {

                if (hasfocus) {
                    String s = itemaddtext.getText().toString();
                    checkAddValue(s);
                } else {
                    btnresetadd.setVisibility(View.GONE);
                }
            }
        });
        itemaddtext.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence
                                                  s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAddValue(s.toString());
            }
        });
        itemaddtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String text = itemaddtext.getText().toString();
                    if (text.trim().equals("")) {
                        return false;
                    }
                    adap.add(new CheckItem(checkId, 0, text, 0));
                    Utility.setListViewHeightBasedOnChildren(checklist);
                    itemaddtext.setText("");
                    itemaddtext.requestFocus();
                    currChecklistitems.add(String.valueOf(checkId));
                    S_pref.edit().putInt(checkId + "Imp", 0).commit();
                    S_pref.edit().putString(checkId + "Title", text).commit();
                    S_pref.edit().putInt(checkId + "Value", 0).commit();
                    checkId++;
                    S_pref.edit().putInt("checkId", checkId).commit();
                    S_pref.edit().putString(fileId + "checkids", convertToString(currChecklistitems)).commit();


                }
                return true;
            }
        });
    }

    private void checkAddValue(String s) {
        if (s.trim().equals("")) {
            btnresetadd.setVisibility(View.INVISIBLE);
        } else {
            btnresetadd.setVisibility(View.VISIBLE);
        }
    }

    public void setstar(View v) {
        final int position = checklist.getPositionForView((View) v.getParent());
        int val = mcheckitems.get(position).mImp;
        if (val == 0) {
            v.setBackgroundColor(0xFFFF5644);
            mcheckitems.get(position).setItemStar(1);
            S_pref.edit().putInt(mcheckitems.get(position).mid + "Imp", 1).commit();
        } else {
            v.setBackgroundColor(0xFFDEDEDE);
            mcheckitems.get(position).setItemStar(0);
            S_pref.edit().putInt(mcheckitems.get(position).mid + "Imp", 0).commit();
        }
    }

    public void resetAddText(View v) {
        itemaddtext.setText("");
    }

    private String convertToString
            (ArrayList<String> list) {
        StringBuilder sb = new
                StringBuilder();
        String delim = "";
        for (String s : list) {
            sb.append(delim);
            sb.append(s);
            delim = ",";
        }
        return sb.toString();
    }

    private ArrayList<String> convertToArray
            (String string) {
        ArrayList<String> list = new
                ArrayList<String>(Arrays.asList
                (string.split(",")));
        return list;
    }

    public void showMenu(View view) {
        final int position = checklist.getPositionForView((View) view.getParent());
        final View perent = (View) view.getParent();
        PopupMenu menu = new PopupMenu
                (this, view);
        menu.setOnMenuItemClickListener
                (new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick
                            (MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.ACTION_STAR:
                                int val = mcheckitems.get(position).mImp;
                                LinearLayout v = (LinearLayout) perent.findViewById(R.id.lay_imp);
                                if (val == 0) {
                                    v.setBackgroundColor(0xFFFF5644);
                                    mcheckitems.get(position).setItemStar(1);
                                    S_pref.edit().putInt(mcheckitems.get(position).mid + "Imp", 1).commit();
                                } else {
                                    v.setBackgroundColor(0xFFDEDEDE);
                                    mcheckitems.get(position).setItemStar(0);
                                    S_pref.edit().putInt(mcheckitems.get(position).mid + "Imp", 0).commit();
                                }
                                break;
                            case R.id.ACTION_SEARCH:
                                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                intent.putExtra(SearchManager.QUERY, mcheckitems.get(position).mTitle);
                                // catch event that there's no activity to handle intent
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(intent);
                                } else {
                                    showMessage("Sorry, there's no web browser available");
                                }
                                break;
                            case R.id.ACTION_CLIP:
                                String text = mcheckitems.get(position).mTitle;
                                Utility.setClipboard(context, text);
                                showMessage("Copyed to clipboard");
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
        menu.inflate(R.menu.checklistpopup);
        int val = mcheckitems.get(position).mImp;
        Menu mMenu = menu.getMenu();
        if (val == 1) {
            mMenu.findItem(R.id.ACTION_STAR).setTitle("Unstar");
        }
        menu.show();
    }

    private void deleteChecklist(int mFileId) {
        for (int i = 0; i < currChecklistitems.size(); i++) {
            int cId = Integer.parseInt(currChecklistitems.get(i));
            S_pref.edit().remove(cId + "Value").commit();
            S_pref.edit().remove(cId + "Imp").commit();
            S_pref.edit().remove(cId + "Title").commit();
        }
        S_pref.edit().remove(mFileId + "checkids").commit();
        S_pref.edit().remove(mFileId + "T").commit();
        S_pref.edit().remove(mFileId + "C").commit();
        S_pref.edit().remove(mFileId + "type").commit();
    }

    public void removeCheckItem(View v) {
        final int mcheckId = checklist.getPositionForView((View) v.getParent());

        S_pref.edit().remove(mcheckId + "Value").commit();
        S_pref.edit().remove(mcheckId + "Imp").commit();
        S_pref.edit().remove(mcheckId + "Title").commit();
        currChecklistitems.remove(mcheckId);
        S_pref.edit().putString(fileId + "checkids", convertToString(currChecklistitems)).commit();
        mcheckitems.remove(mcheckId);
        adap.notifyDataSetChanged();
        Utility.setListViewHeightBasedOnChildren(checklist);

    }

    private void toTextNote() {

        String text = "";
        String Description;
        for (int i = 0; i < mcheckitems.size(); i++) {
            text += mcheckitems.get(i).mTitle + System.getProperty("line.separator");
            int cId = Integer.parseInt(currChecklistitems.get(i));
            S_pref.edit().remove(cId + "Value").commit();
            S_pref.edit().remove(cId + "Imp").commit();
            S_pref.edit().remove(cId + "Title").commit();
        }
        if (text.length() < 52) {
            Description = text;
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            Description = stringBuffer.append(text.substring(0, 50)).append("...").toString();
        }
        S_pref.edit().remove(fileId + "checkids").commit();
        S_pref.edit().putString(fileId + "C", Description).commit();
        S_pref.edit().putString(fileId + "type", "Note").commit();
        S_pref.edit().putString(fileId + "T", dTitle.getText().toString()).commit();
        type = "Note";
        NewFileDir = new File("/data/data/com.ihsan.notes/note/" + category);
        NewfileName = String.valueOf(fileId);
        SaveNote(text);
        showMessage("Saved");
        mcheckitems.clear();
        currChecklistitems.clear();
        setnote();
        invalidateOptionsMenu();
    }

    private void toChecklist() {
        String text = dcontent.getText().toString();
        String Culist = "";
        String delim = "";
        int cId = checkId = S_pref.getInt("checkId", 0);
        ArrayList<String> newlist = new ArrayList<String>(Arrays.asList(text.split(System.getProperty("line.separator"))));
        for (String Item : newlist) {
            if (!Item.trim().equals("")) {
                S_pref.edit().putString(cId + "Title", Item).commit();
                Culist += delim + cId;
                delim = ",";
                cId++;
            }
        }
        S_pref.edit().putInt("checkId", cId).commit();
        S_pref.edit().putString(fileId + "T", dTitle.getText().toString()).commit();
        S_pref.edit().putString(fileId + "C", "Checklist").commit();
        S_pref.edit().putString(fileId + "type", "Checklist").commit();
        S_pref.edit().putString(fileId + "checkids", Culist).commit();
        NewFileDir = new File("/data/data/com.ihsan.notes/note/" + category);
        NewfileName = String.valueOf(fileId);
        SaveNote("");
        type = "Checklist";
        invalidateOptionsMenu();
        showMessage("Saved");
        setchecklist();
        invalidateOptionsMenu();
    }

    private void addEvent() {
        String eTitle = Title;
        if (Title.equals("") || Title.equals(" ")) {
            eTitle = "New event";
        }
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        Calendar cal = Calendar.getInstance();
        long startTime = cal.getTimeInMillis();
        long endTime = cal.getTimeInMillis() + 60 * 60 * 1000;
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
        intent.putExtra(Events.TITLE, eTitle);
        intent.putExtra(Events.DESCRIPTION, text.toString());
        intent.putExtra(Reminders.MINUTES, 2);
        intent.putExtra(Reminders.EVENT_ID, "100");
        intent.putExtra(Reminders.METHOD, CalendarContract.Reminders.METHOD_ALARM);
        startActivity(intent);
    }

    private boolean SaveNote(CharSequence text) {
        final CharSequence mTextToWrite;
        boolean st = false;
        if (!NewFileDir.exists()) {
            NewFileDir.mkdirs();
        }
        NewFile = new File(NewFileDir, NewfileName + ".txt");
        if (NewFile.exists()) {
            NewFile.delete();
        }
        mTextToWrite = text;

        if (isExternalStorageWritable()) {
            try {
                BufferedWriter writer =
                        new BufferedWriter(new FileWriter(NewFile, true));
                st = true;
                writer.write(mTextToWrite.toString());
                writer.close();

            } catch (IOException e) {

            }
        }
        return st;
    }

    private void deleteList() {
        final File delcat = currentFile;

        LayoutInflater factory = LayoutInflater.from(this);
        final View DialogView = factory.inflate(R.layout.dialog_edit, null);
        final AlertDialog cDialog = new AlertDialog.Builder(this).create();
        cDialog.setView(DialogView);
        final TextView dgTitle = (TextView) DialogView.findViewById(R.id.dialog_title);
        final EditText inputCat = (EditText) DialogView.findViewById(R.id.InputText);

        dgTitle.setText("Delete");
        inputCat.setFocusable(false);
        inputCat.setBackgroundResource(R.drawable.circular_button);
        inputCat.setPadding(7, 0, 7, 0);
        inputCat.setText("Checklist will be deleted");
        DialogView.findViewById(R.id.dialog_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delcat.exists()) {

                    cDialog.dismiss();
                    showMessage("deleted");
                    Intent intent_view = new Intent();
                    intent_view.setClass(getApplicationContext(), MainActivity.class);
                    intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent_view.putExtra("State", "Open");
                    startActivity(intent_view);
                    deleteChecklist(fileId);
                    delcat.delete();
                }
            }
        });

        DialogView.findViewById(R.id.dialog_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cDialog.dismiss();
            }
        });

        cDialog.show();
    }

    private void deleteChecked() {

        LayoutInflater factory = LayoutInflater.from(this);
        final View DialogView = factory.inflate(R.layout.dialog_edit, null);
        final AlertDialog cDialog = new AlertDialog.Builder(this).create();
        cDialog.setView(DialogView);
        final TextView dgTitle = (TextView) DialogView.findViewById(R.id.dialog_title);
        final EditText inputCat = (EditText) DialogView.findViewById(R.id.InputText);

        dgTitle.setText("Delete");
        inputCat.setFocusable(false);
        inputCat.setBackgroundResource(R.drawable.circular_button);
        inputCat.setPadding(7, 0, 7, 0);
        inputCat.setText("Checked items will be deleted");
        DialogView.findViewById(R.id.dialog_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cDialog.dismiss();
                for (int i = mcheckitems.size() - 1; i > -1; i--) {
                    int val = mcheckitems.get(i).mvalue;
                    if (val == 1) {
                        int item = mcheckitems.get(i).mid;
                        S_pref.edit().remove(item + "Value").commit();
                        S_pref.edit().remove(item + "Imp").commit();
                        S_pref.edit().remove(item + "Title").commit();
                        currChecklistitems.remove(i);
                        mcheckitems.remove(i);
                    }
                }
                adap.notifyDataSetChanged();
                Utility.setListViewHeightBasedOnChildren(checklist);
                S_pref.edit().putString(fileId + "checkids", convertToString(currChecklistitems)).commit();
            }
        });

        DialogView.findViewById(R.id.dialog_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cDialog.dismiss();
            }
        });

        cDialog.show();
    }

    private void Delete() {
        final File delcat = currentFile;

        LayoutInflater factory = LayoutInflater.from(this);
        final View DialogView = factory.inflate(R.layout.dialog_edit, null);
        final AlertDialog cDialog = new AlertDialog.Builder(this).create();
        cDialog.setView(DialogView);
        final TextView dgTitle = (TextView) DialogView.findViewById(R.id.dialog_title);
        final EditText inputCat = (EditText) DialogView.findViewById(R.id.InputText);

        dgTitle.setText("Delete");
        inputCat.setFocusable(false);
        inputCat.setBackgroundResource(R.drawable.circular_button);
        inputCat.setPadding(7, 0, 7, 0);
        inputCat.setText("Note will be deleted");
        DialogView.findViewById(R.id.dialog_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delcat.exists()) {
                    delcat.delete();
                    cDialog.dismiss();
                    showMessage("deleted");
                    S_pref.edit().remove(fileId + "T").commit();
                    S_pref.edit().remove(fileId + "C").commit();
                    S_pref.edit().remove(fileId + "type").commit();
                    Intent intent_view = new Intent();
                    intent_view.setClass(getApplicationContext(), MainActivity.class);
                    intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent_view.putExtra("State", "Open");
                    startActivity(intent_view);
                }
            }
        });

        DialogView.findViewById(R.id.dialog_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cDialog.dismiss();
            }
        });

        cDialog.show();
    }

    private void showMessage(String _s) {
        Toast.makeText(context, _s, Toast.LENGTH_SHORT).show();
    }
}
