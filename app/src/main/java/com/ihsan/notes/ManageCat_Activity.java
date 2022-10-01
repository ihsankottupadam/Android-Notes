package com.ihsan.notes;

import android.app.*;
import android.os.*;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.*;
import android.widget.*;
import android.content.*;
import android.view.LayoutInflater;
import android.view.WindowManager.LayoutParams;

import java.io.File;
import java.util.ArrayList;

import android.view.*;

public class ManageCat_Activity extends Activity {
    private ListView catlist;
    private Context context;
    private SharedPreferences S_pref;
    private final ArrayList<String> categorises = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_cat);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        S_pref = getSharedPreferences("init_pref", Activity.MODE_PRIVATE);
        catlist = (ListView) findViewById(R.id.mCatList);
        init();
        context = this;
    }

    @Override
    public void onBackPressed() {
        Intent intent_view = new Intent();
        intent_view.setClass(getApplicationContext(), MainActivity.class);
        intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent_view.putExtra("State", "Open");
        startActivity(intent_view);
        // TODO: Implement this method
        super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                openDialog("ADD", 0);
                return true;
            case android.R.id.home:
                Intent intent_view = new Intent();
                intent_view.setClass(getApplicationContext(), MainActivity.class);
                intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent_view.putExtra("State", "Open");
                startActivity(intent_view);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void DeleteCat(View v) {
        final int position = catlist.getPositionForView((View) v.getParent());
        final File delcat = new File("/data/data/com.ihsan.notes/note/" + categorises.get(position), "");

        LayoutInflater factory = LayoutInflater.from(this);
        final View DialogView = factory.inflate(R.layout.dialog_edit, null);
        final AlertDialog cDialog = new AlertDialog.Builder(this).create();
        cDialog.setView(DialogView);
        final TextView dgTitle = (TextView) DialogView.findViewById(R.id.dialog_title);
        final EditText inputCat = (EditText) DialogView.findViewById(R.id.InputText);

        dgTitle.setText("Delete");
        inputCat.setFocusable(false);
        inputCat.setBackgroundResource(R.drawable.circular_button);
        inputCat.setGravity(Gravity.CENTER);
        inputCat.setText("This category will be deleted");
        DialogView.findViewById(R.id.dialog_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delcat.exists()) {
                    deleteRecursive(delcat);
                    Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
                    cDialog.dismiss();
                    init();
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

    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
        if (fileOrDirectory.getName().endsWith(".txt")) {
            String fname = fileOrDirectory.getName();
            int l = fname.length();
            fname = fname.substring(0, l - 4);
            S_pref.edit().remove(fname + "T").commit();
            S_pref.edit().remove(fname + "C").commit();
        }
    }

    public void RenameCat(View v) {
        final int position = catlist.getPositionForView((View) v.getParent());
        openDialog("RENAME", position);
    }

    private void openDialog(final String D_ACTION, final int pos) {
        String dtitle;
        String dval;
        int i, z = 0;
        LayoutInflater factory = LayoutInflater.from(this);
        final View DialogView = factory.inflate(R.layout.dialog_edit, null);
        final AlertDialog cDialog = new AlertDialog.Builder(this).create();
        cDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        cDialog.setView(DialogView);
        final TextView dgTitle = (TextView) DialogView.findViewById(R.id.dialog_title);
        final EditText inputCat = (EditText) DialogView.findViewById(R.id.InputText);
        inputCat.setSelectAllOnFocus(true);
        if (D_ACTION.equals("RENAME")) {
            dtitle = "Rename category";
            dval = categorises.get(pos);
        } else {
            dtitle = "Add category";
            dval = "Category";
            if (categorises.contains(dval)) {
                for (i = 1; z == 1; i++) {
                    dval = "Category=" + i;
                    if (!categorises.contains(dval)) {
                        return;
                    }
                }
            }
        }
        dgTitle.setText(dtitle);
        inputCat.setText(dval);
        DialogView.findViewById(R.id.dialog_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String Newcat = inputCat.getText().toString();
                File newdcat = new File("/data/data/com.ihsan.notes/note/" + Newcat, "");
                if (categorises.contains(Newcat)) {
                    Toast.makeText(context, "Category name already in use", Toast.LENGTH_SHORT).show();
                } else {
                    if (D_ACTION.equals("ADD")) {
                        if (!newdcat.exists()) {
                            newdcat.mkdirs();
                        }
                        cDialog.dismiss();
                        init();
                    } else {
                        File oldcat = new File("/data/data/com.ihsan.notes/note/" + categorises.get(pos), "");
                        if (oldcat.exists()) {
                            oldcat.renameTo(newdcat);
                        }
                        cDialog.dismiss();
                        init();
                    }
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

    private void init() {
        categorises.clear();
        File dir = new File("/data/data/com.ihsan.notes/note", "");
        File[] folderlist = dir.listFiles();
        String[] theNamesOfFiles = new String[folderlist.length];
        for (int i = 0; i < theNamesOfFiles.length; i++) {
            if (folderlist[i].isDirectory()) {
                String Cname = folderlist[i].getName();
                categorises.add(Cname);
            }
        }
        Mcatlist_adapter adapter = new Mcatlist_adapter(this, categorises);
        catlist.setAdapter(adapter);
    }

    private void showMessage(String _s) {
        Toast.makeText(context, _s, Toast.LENGTH_SHORT).show();
    }
}