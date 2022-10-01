package com.ihsan.notes;

import android.annotation.SuppressLint;
import android.app.*;
import android.os.*;
import android.widget.RelativeLayout;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import android.content.Context;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.*;
import android.view.ActionMode;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.ArrayList;
import java.io.File;
import java.text.SimpleDateFormat;

import android.widget.*;
import android.net.*;

public class MainActivity extends Activity {
    private static String TAG = MainActivity.class.getSimpleName();
    ListView mDrawerList;
    ListView mprefList;
    private LinearLayout emtytext;
    private ListView notelist;
    private TextView txtN;
    RelativeLayout mDrawerPane;
    private SharedPreferences S_pref;
    private SharedPreferences pref;
    private Context context;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    ArrayList<NavItem> mPrefItems = new ArrayList<NavItem>();
    ArrayList<NavItem> movelist = new ArrayList<NavItem>();
    ArrayList<NoteItem> mNoteItems = new ArrayList<NoteItem>();
    ArrayList<Integer> selected_positions = new ArrayList<Integer>();
    boolean isinit = false;
    boolean islocked;
    boolean isFromShare;
    boolean isfromEdit;
    private boolean hasCheckedItems;
    private String lastcat;
    private String IntentState;
    private int currentPosition;
    private File[] folderlist;
    private String currentCategory;

    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        S_pref = getSharedPreferences("init_pref", Activity.MODE_PRIVATE);
        context = this;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        islocked = pref.getBoolean("IsAppLocked", false);
        if (getIntent().getExtras() != null && getIntent().hasExtra("State")) {
            IntentState = getIntent().getStringExtra("State");
            if (IntentState.equals("Open")) {
                islocked = false;
            } else if (IntentState.equals("fromEdit")) {
                islocked = false;
                isfromEdit = true;
                lastcat = getIntent().getStringExtra("cCategory");
            } else if (IntentState.equals("fromShare")) {
                isFromShare = true;
                islocked = false;
                lastcat = getIntent().getStringExtra("cCategory");
            }
        }
        if (islocked) {
            Intent intent_view = new Intent();
            intent_view.setClass(getApplicationContext(), Screen_activity.class);
            intent_view.putExtra("Action", "UNLOCK");
            intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent_view);
        }
// DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
// Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        notelist = (ListView) findViewById(R.id.list1);
        txtN = (TextView) findViewById(R.id.devN);
        emtytext = (LinearLayout) findViewById(R.id.fragment_emtytxtlayout);
        mprefList = (ListView) findViewById(R.id.prefList);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        init();
        txtN.setText("Developed by Ihsan kottupadam");
// Drawer Item click listeners
        int amId = getResources().getIdentifier("action_context_bar",
                "id", "android");
        View view = findViewById(amId);
        view.setBackgroundColor(0xffef6c00);
        mPrefItems.add(new NavItem("Manage categories", "", R.drawable.ic_manage_list));
        mPrefItems.add(new NavItem("Settings", "", R.drawable.ic_settings));
        mPrefItems.add(new NavItem("About", "", R.drawable.ic_info));
        DrawerListAdapter adap = new DrawerListAdapter(this, mPrefItems);
        mprefList.setAdapter(adap);
        Utility.setListViewHeightBasedOnChildren(mDrawerList);
        Utility.setListViewHeightBasedOnChildren(mprefList);
        mprefList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawer(mDrawerPane);
                switch (position) {
                    case 0:
                        Intent intent_view = new Intent();
                        intent_view.setClass(getApplicationContext(), ManageCat_Activity.class);
                        intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_view);
                        break;
                    case 1:
                        Intent intent_view1 = new Intent();
                        intent_view1.setClass(getApplicationContext(), Preferences_Activity.class);
                        intent_view1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_view1);
                        break;
                    case 2:
                        Intent intent_view2 = new Intent();
                        intent_view2.setClass(getApplicationContext(), AboutActivity.class);
                        intent_view2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_view2);
                        break;
                    default:
                        break;
                }
            }
        });
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }

        });
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new
                ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                        setTitle("  Notes");
                        invalidateOptionsMenu();
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                        setTitle("  " + currentCategory);
                        invalidateOptionsMenu();
                    }
                };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (!S_pref.getString("inited", "").equals("done")) {
            S_pref.edit().putInt("FileId", 600).commit();
            S_pref.edit().putInt("cId", 100).commit();
            S_pref.edit().putString("inited", "done").commit();
            isinit = true;
            selectItemFromDrawer(0);
        } else {
            int po = S_pref.getInt("lastPos", 0);
            String pos = S_pref.getString("lastCat", "");
            if (mNavItems.size() - 1 < po) {
                selectItemFromDrawer(0);
            } else if (mNavItems.get(po).mTitle.equals(pos)) {
                selectItemFromDrawer(po);
            } else {
                selectItemFromDrawer(0);
            }
        }
        if ((isfromEdit || isFromShare) && navItemContains(lastcat)) {
            selectItemFromDrawer(navItemindexOf(lastcat));
        }
        if ((isFromShare || isfromEdit) && mNavItems.contains(lastcat)) {
            int position = 0;
            mDrawerList.setItemChecked(position, true);
            setTitle("  " + mNavItems.get(position).mTitle);
            currentCategory = mNavItems.get(position).mTitle;
            SetnotesList(position);
            mDrawerLayout.closeDrawer(mDrawerPane);
        }
    }

    @Override
    protected void onResume() {
        init();
        mDrawerList.setItemChecked(currentPosition, true);
        if (selected_positions.size() == 0) {
            NotelistRemoveSelection();
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerPane);
        menu.findItem(R.id.ACTION_ADD_MEMMO).setVisible(!drawerOpen);
        menu.findItem(R.id.ACTION_MORE).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
// Handle your other action bar items...
        switch (item.getItemId()) {
            case R.id.ACTION_ADD_MEMMO:
                Intent intent_view = new Intent();
                intent_view.setClass(getApplicationContext(), EditActivity.class);
                intent_view.putExtra("Action", "NEW");
                intent_view.putExtra("Category", currentCategory);
                intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_view);
                return true;
            case R.id.ACTION_NEWCHECKLIST:
                Intent intent_vie = new Intent();
                intent_vie.setClass(getApplicationContext(), NoteDisplayActivity.class);
                intent_vie.putExtra("Action", "NewList");
                intent_vie.putExtra("Category", currentCategory);
                intent_vie.putExtra("fType", "Checklist");
                intent_vie.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_vie);
                return true;
            case R.id.ACTION_SETTINGS:
                Intent intent_view1 = new Intent();
                intent_view1.setClass(getApplicationContext(), Preferences_Activity.class);
                intent_view1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_view1);
                return true;
            case R.id.ACTION_ABOUT:
                Intent intent_view2 = new Intent();
                intent_view2.setClass(getApplicationContext(), AboutActivity.class);
                intent_view2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_view2);
                return true;
            case R.id.ACTION_EXIT:
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        if (isinit) {
            mDrawerLayout.openDrawer(mDrawerPane);
        }
    }

    private void onNoteListItemSelect(int position, boolean isclick) {
        if (!isclick) {
            NotetoggleSelection(position);
        }
        if (notelist.isItemChecked(position)) {
            selected_positions.add(position);
        } else if (selected_positions.contains(position)) {
            selected_positions.remove(selected_positions.indexOf(position));
        }
        hasCheckedItems = selected_positions.size() > 0;
        if (hasCheckedItems && mActionMode == null) {
// there are some selected items, start the actionMode 
            mActionMode = startActionMode(new ActionModeCallback());
        } else if (!hasCheckedItems && mActionMode != null)
// there no selected items, finish the actionMode 
            mActionMode.finish();
        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(selected_positions.size()) + " selected");
    }

    private void NotetoggleSelection(int position) {
        if (notelist.isItemChecked(position)) {
            notelist.setItemChecked(position, false);
        } else {
            notelist.setItemChecked(position, true);
        }
    }

    private void NotelistRemoveSelection() {
        int l = notelist.getCount();
        int i;
        for (i = 0; i < l; i++) {
            notelist.setItemChecked(i, false);
        }
    }

    private void NotelistSelectall() {
        int l = notelist.getCount();
        int i;
        for (i = 0; i < l; i++) {
            notelist.setItemChecked(i, true);
            if (!selected_positions.contains(i)) {
                selected_positions.add(i);
            }
        }
        mActionMode.setTitle(String.valueOf(selected_positions.size()) + " selected");
    }

    private boolean navItemContains(String _n) {
        for (int i = 0; i < mNavItems.size(); i++) {
            if (mNavItems.get(i).mTitle.equals(_n)) {
                return true;
            }
        }
        return false;
    }

    private int navItemindexOf(String _n) {
        for (int i = 0; i < mNavItems.size(); i++) {
            if (mNavItems.get(i).mTitle.equals(_n)) {
                return i;
            }
        }
        return -1;
    }

    private void navlistSetselection() {
        int po = S_pref.getInt("lastPos", 0);
        String pos = S_pref.getString("lastCat", "");
        if (mNavItems.size() - 1 < po) {
            selectItemFromDrawer(0);
        } else if (mNavItems.get(po).mTitle.equals(pos)) {
            selectItemFromDrawer(po);
        } else {
            selectItemFromDrawer(0);
        }
    }

    private void Delete() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View DialogView = factory.inflate(R.layout.dialog_edit, null);
        final AlertDialog cDialog = new AlertDialog.Builder(this).create();
        cDialog.setView(DialogView);
        final TextView dgTitle = (TextView) DialogView.findViewById(R.id.dialog_title);
        final EditText inputCat = (EditText) DialogView.findViewById(R.id.InputText);

        dgTitle.setText("Delete");
        inputCat.setFocusable(false);
        inputCat.setBackgroundResource(R.drawable.circular_button);
        String dtval = selected_positions.size() + " Notes will be deleted";
        if (selected_positions.size() == 1) {
            dtval = "This note will be deleted";
        }
        inputCat.setPadding(18, 0, 15, 0);
        inputCat.setText(dtval);
        inputCat.setSingleLine(false);
        DialogView.findViewById(R.id.dialog_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int dc = 0;
                for (int i = 0; i < mNoteItems.size(); i++) {
                    if (selected_positions.contains(i)) {
                        String Url = mNoteItems.get(i).nUrl;
                        int id = mNoteItems.get(i).nId;
                        File df = new File(Url, "");
                        if (df.delete()) {
                            dc++;
                            S_pref.edit().remove(id + "T").commit();
                            S_pref.edit().remove(id + "C").commit();
                        }

                    }

                }
                showMessage(dc + " Items deleted");
                mActionMode.finish();
                selected_positions.clear();
                SetnotesList(currentPosition);
                init();
                selectItemFromDrawer(currentPosition);
                cDialog.dismiss();
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

    private void moveNotes() {
        movelist.clear();
        File dir = new File("/data/data/com.ihsan.notes/note", "");
        folderlist = dir.listFiles();
        String[] theNamesOfFiles = new String[folderlist.length];
        for (int i = 0; i < theNamesOfFiles.length; i++) {
            if (folderlist[i].isDirectory()) {
                String Cname = folderlist[i].getName();
                movelist.add(new NavItem(Cname, "", R.drawable.ic_cat_selector));
            }
        }
        LayoutInflater factory = LayoutInflater.from(this);
        final View DialogView = factory.inflate(R.layout.list_dialog, null);
        final AlertDialog cDialog = new AlertDialog.Builder(this).create();
        cDialog.setView(DialogView);
        final TextView dgTitle = (TextView) DialogView.findViewById(R.id.dialog_title);
        ListView list = (ListView) DialogView.findViewById(R.id.list);
        DrawerListAdapter adapter = new DrawerListAdapter(this, movelist);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String toCat = mNavItems.get(position).mTitle;
                File moveTo = new File("/data/data/com.ihsan.notes/note/" + toCat, "");
                int dc = 0;
                for (int i = 0; i < mNoteItems.size(); i++) {
                    if (selected_positions.contains(i)) {
                        String Url = mNoteItems.get(i).nUrl;
                        File fromcat = new File(Url, "");
                        File moveTodir = new File(moveTo, mNoteItems.get(i).nId + ".txt");
                        if (fromcat.exists()) {
                            fromcat.renameTo(moveTodir);
                            dc++;
                        }
                    }
                }
                showMessage(dc + " notes moved");
                mActionMode.finish();
                selected_positions.clear();
                SetnotesList(currentPosition);
                init();
                selectItemFromDrawer(currentPosition);
                cDialog.dismiss();
            }

        });
        DialogView.findViewById(R.id.dialog_ok).setVisibility(View.GONE);
        DialogView.findViewById(R.id.dialog_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cDialog.dismiss();
            }
        });

        cDialog.show();
    }

    private void init() {
        mNavItems.clear();
        File dir = new File("/data/data/com.ihsan.notes/note", "");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File defdir = new File(dir, "Notes");
        if (!defdir.exists()) {
            defdir.mkdirs();
        }
        folderlist = dir.listFiles();
        String[] theNamesOfFiles = new String[folderlist.length];
        for (int i = 0; i < theNamesOfFiles.length; i++) {
            if (folderlist[i].isDirectory()) {
                String Cname = folderlist[i].getName();
                int fcount = folderlist[i].listFiles().length;
                mNavItems.add(new NavItem(Cname, String.valueOf(fcount), R.drawable.ic_cat_selector));
            }
        }
        Utility.setListViewHeightBasedOnChildren(mDrawerList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);
    }

    private void selectItemFromDrawer(int position) {
        mDrawerLayout.closeDrawer(mDrawerPane);
        SetnotesList(position);
        mDrawerList.setItemChecked(position, true);
        setTitle("  " + mNavItems.get(position).mTitle);
        currentCategory = mNavItems.get(position).mTitle;
        if (mActionMode != null) {
            mActionMode.finish();
        }
        currentPosition = position;
        selected_positions.clear();
        S_pref.edit().putInt("lastPos", position).commit();
        S_pref.edit().putString("lastCat", currentCategory).commit();
    }

    private void SetnotesList(int position) {
        currentCategory = mNavItems.get(position).mTitle;
        mNoteItems.clear();
        File currf = new File("/data/data/com.ihsan.notes/note/" + currentCategory, "");
        File[] filelist = currf.listFiles();
        String[] theNamesOfFiles = new String[filelist.length];
        for (int i = 0; i < theNamesOfFiles.length; i++) {
            if (filelist[i].getName().toLowerCase().endsWith(".txt")) {
                String fname = filelist[i].getName();
                int l = fname.length();
                fname = fname.substring(0, l - 4);
                int id = Integer.parseInt(fname);
                String title = S_pref.getString(id + "T", "");
                String description = S_pref.getString(id + "C", "");
                String Type = S_pref.getString(id + "type", "");
                long lm = filelist[i].lastModified();

                String url = filelist[i].toString();
                mNoteItems.add(new NoteItem(id, Type, title, description, lm, url));
            }
        }
        Notelist_Adapter adap = new Notelist_Adapter(this, mNoteItems);
        notelist.setAdapter(adap);
        notelist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        notelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode == null) {
                    Intent intent_view = new Intent();
                    intent_view.setClass(getApplicationContext(), NoteDisplayActivity.class);
                    intent_view.putExtra("Action", "View");
                    intent_view.putExtra("Title", mNoteItems.get(position).nTitle);
                    intent_view.putExtra("FileId", String.valueOf(mNoteItems.get(position).nId));
                    intent_view.putExtra("cFile", mNoteItems.get(position).nUrl);
                    intent_view.putExtra("Category", currentCategory);
                    intent_view.putExtra("fType", mNoteItems.get(position).nType);
                    intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent_view);
                    notelist.setItemChecked(position, false);
                } else {
                    onNoteListItemSelect(position, true);
                }
            }

        });
        notelist.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onNoteListItemSelect(position, false);

                return true;
            }
        });
        if (mNoteItems.size() == 0) {
            emtytext.setVisibility(View.VISIBLE);
        } else {
            emtytext.setVisibility(View.GONE);
        }
    }

    public void fragmentClickListerner(View v) {
        switch (v.getId()) {


            default:
                break;
        }
    }

    public class PreferencesFragment extends Fragment {
        public static final String ARG_FILE = "folder";
        ArrayList<String> inFiles = new ArrayList<String>();
        private ArrayList<String> Datelist = new ArrayList<String>();

        public PreferencesFragment() {
// Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_preferences, container, false);
            String f = getArguments().getString(ARG_FILE);
            File currf = new File(f, "");
            File[] filelist = currf.listFiles();
            String[] theNamesOfFiles = new String[filelist.length];
            for (int i = 0; i < theNamesOfFiles.length; i++) {
                if (filelist[i].getName().toLowerCase().endsWith(".txt")) {
                    String name = filelist[i].getName();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String dat = sdf.format(filelist[i].lastModified());
                    int l = name.length();
                    name = name.substring(0, l - 4);
                    Datelist.add(String.valueOf(dat));
                    inFiles.add(name);
                }
            }
            ListView list = (ListView) rootView.findViewById(R.id.list1);
            Toast.makeText(getApplicationContext(), f, Toast.LENGTH_SHORT).show();
            return inflater.inflate(R.layout.fragment_preferences, container, false);
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
// inflate contextual menu
            mode.getMenuInflater().inflate(R.menu.context_main, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.ACTION_DELETE:
                    Delete();
                    return true;
                case R.id.ACTION_SELECTALL:
                    NotelistSelectall();
                    return true;
                case R.id.ACTION_MOVE:
                    moveNotes();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
// remove selection 
            NotelistRemoveSelection();
            mActionMode = null;
            selected_positions.clear();
        }
    }

    private void showMessage(String _s) {
        Toast.makeText(context, _s, Toast.LENGTH_SHORT).show();
    }
}
