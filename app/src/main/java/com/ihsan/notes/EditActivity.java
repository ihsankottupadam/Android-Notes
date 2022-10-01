package com.ihsan.notes;

import android.app.*;
import android.os.Bundle;
import android.os.Environment;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.*;
import android.widget.*;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.Context;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.io.*;


public class EditActivity extends Activity 
{
	private EditText edit_title;
	private EditText edit_content;

	private String Category;
    private String Content = "";
    private File CurrentFile;
    private String CurrentFileUrl;
    private String Description = "";
    private String FileName;
    private String FontSize;
    private File NewFile;
	private Context mcontext;
    private SharedPreferences S_pref;
    private String Title = "";
    private int fileId;
	private int fontSizeInt;
    private File[] folderlist;
    private String iAction = "NEW";
    private boolean isFromShare = false;
    private Boolean isfocusContent;
    private ArrayList<String> mNavItems;
    private SharedPreferences pref;
    private int sdis = 0;
    private Spinner spinner;
    private String uContent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noteedit);
        edit_title=(EditText)findViewById(R.id.note_editite);
		edit_content=(EditText)findViewById(R.id.note_edit_maintext);
		Context context=this;
		mcontext=this;
        mNavItems= new ArrayList<String>();
		S_pref= getSharedPreferences("init_pref", Activity.MODE_PRIVATE);
		pref=PreferenceManager.getDefaultSharedPreferences(context);
		Intent intent=getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		init();
		boolean islocked=pref.getBoolean("IsAppLocked",false);
		
		if (!Intent.ACTION_SEND.equals(action)) {
            iAction = getIntent().getStringExtra("Action");
            Category = getIntent().getStringExtra("Category");
            if (iAction.equals("UPDATE")) {
                Title = getIntent().getStringExtra("Title");
                fileId = Integer.parseInt(intent.getStringExtra("FileId"));
                CurrentFileUrl = getIntent().getStringExtra("cFile");
                uContent = getIntent().getStringExtra("Content");
                CurrentFile = new File(CurrentFileUrl, "");
                if (Title.equals(" ")) {
                    Title = "";
                }
                if (uContent.equals(" ")) {
                    uContent = "";
                }
                edit_title.setText(Title);
                edit_content.setText(uContent);
            } else {
                fileId = S_pref.getInt("FileId", 0);
                edit_title.setText("");
                edit_content.setText("");
            }
			isfocusContent=pref.getBoolean("isFocusContent", false);
			if (isfocusContent) {
			    edit_content.requestFocus();
			}
			String FontSize=pref.getString("textSize","Medium (Default)");
			switch (FontSize){
				case "Small":fontSizeInt= R.style.FontSmall;break;
				case "Medium (Default)":fontSizeInt= R.style.FontMedium;break;
				case "Large":fontSizeInt= R.style.FontLarge;break;
				case "Extra large":fontSizeInt= R.style.FontExtraLarge;break;
				default:break;
			}
			if (Build .VERSION.SDK_INT <
				Build .VERSION_CODES.M) {
				edit_content.setTextAppearance
				(context, fontSizeInt);
			} else {
				edit_content.setTextAppearance(fontSizeInt);
			}
        }
        if (Intent.ACTION_SEND.equals(action) && type != null && "text/plain".equals(type)) {
            handleSendText(intent);
            isFromShare = true;
			if(islocked){
				Intent intent_view=new Intent();
				intent_view.setClass(getApplicationContext(),Screen_activity.class);
				intent_view.putExtra("Action","FromEdit");
				intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent_view);
			}
        }
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit, menu);
		int res=R.drawable.ic_cat;
		setTitle(Category);
        MenuItem item = menu.findItem(R.id.spinner);
	    spinner = (Spinner) MenuItemCompat.getActionView(item);
		spinner.setBackgroundResource(R.drawable.button_box_dark);
	    mSpinnerAdapter adapter=new mSpinnerAdapter(this,res, mNavItems);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView _parent, View _view, final int _position, long _id) { 
					if(_position==0){
						addCategory();
						spinner.setSelection(mNavItems.indexOf(Category));
					}
					else{
						spinner.setSelection(_position);
						Category=mNavItems.get(_position);
						setTitle(Category);
					}
				}
				@Override
				public void onNothingSelected(AdapterView _parent) { 
				}
			});
		return true;
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_CancelEdit:
                finish();
                return true;
            case R.id.action_SaveEdit:
                SaveFile();
                return true;
			case android.R.id.home:
				finish();
				return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
	private void addCategory() {
		if(sdis==0){
        sdis = 1;return;
		}
        LayoutInflater factory = LayoutInflater.from(this);
		final View DialogView = factory.inflate(R.layout.dialog_edit, null);
		final AlertDialog cDialog = new AlertDialog.Builder(this).create();
		cDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		cDialog.setView(DialogView);
		final TextView dgTitle = (TextView) DialogView.findViewById(R.id.dialog_title);
		final EditText inputCat = (EditText) DialogView.findViewById(R.id.InputText);
		inputCat.setSelectAllOnFocus(true);
		dgTitle.setText("Add category");
		inputCat.setText("");
		DialogView.findViewById(R.id.dialog_ok).setOnClickListener(new OnClickListener() {    
				@Override
				public void onClick(View v) {
					String Newcat=inputCat.getText().toString();
					File newdcat= new File("/data/data/com.ihsan.notes/note/"+Newcat,"");
					if(mNavItems.contains(Newcat)){
						Toast.makeText(mcontext,"Category name already in use", Toast.LENGTH_SHORT).show();
					}
					else{
							if(!newdcat.exists()){
								newdcat.mkdirs();
							}
							cDialog.dismiss();
							init();
						     if (mNavItems.contains(Newcat)) {
							spinner.setSelection(mNavItems.indexOf(Newcat));
							setTitle(Newcat);
							Category = Newcat;
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
	private void init(){
		if(mNavItems.size()!=0){mNavItems.clear();}
		mNavItems.add("Add category");
		File dir = new File("/data/data/com.ihsan.notes/note","");
		folderlist = dir.listFiles();
		String[] theNamesOfFiles = new String[folderlist.length];
		for (int i = 0; i < theNamesOfFiles.length; i++) {
			if(folderlist[i].isDirectory()){
				String Cname=folderlist[i].getName();
				mNavItems.add(Cname);
			}
		}
		
	}
	private void SaveFile() {
        File file;
        File file2;
        Title = edit_title.getText().toString();
        Content = edit_content.getText().toString();
        if (Content.length() < 52) {
            Description = Content;
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            Description = stringBuffer.append(Content.substring(0, 50)).append("...").toString();
        }
        if (this.iAction.equals("NEW") && this.Content.isEmpty() && this.Title.isEmpty()) {
            ShowMessage("No content to save, Notes discarded");
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),MainActivity.class);
            intent.putExtra("State", "Open");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
            return;
        }
        if (this.Title.equals((Object)"")) {
            this.Title = " ";
        }
        if (this.Description.equals((Object)"")) {
            this.Description = " ";
        }
        FileName = String.valueOf((int)fileId);
        StringBuffer stringBuffer = new StringBuffer();
        File file3 =NewFile = (file2 = new File(stringBuffer.append("/data/data/com.ihsan.notes/note/").append(this.Category).toString(), ""));
        StringBuffer stringBuffer2 = new StringBuffer();
        NewFile = file = new File(file3, stringBuffer2.append(this.FileName).append(".txt").toString());
        if (this.iAction.equals((Object)"UPDATE")) {
            this.CurrentFile.delete();
        }
        ShowMessage("Saved");
        SaveNote(Content);
        Intent intent = new Intent();
        Context context = getApplicationContext();
        intent.setClass(context,MainActivity.class);
		if(isFromShare){intent.putExtra("State", "fromShare");}
        else{intent.putExtra("State", "fromEdit");}
        intent.putExtra("cCategory",Category);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }
	private void SaveNote(CharSequence charSequence) {
        if (!this.isExternalStorageWritable()) return;
        try {
            FileWriter fileWriter = new FileWriter(this.NewFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter((Writer)fileWriter);
            bufferedWriter.write(charSequence.toString());
            bufferedWriter.close();
            SharedPreferences.Editor editor = this.S_pref.edit();
            StringBuffer stringBuffer = new StringBuffer();
            editor.putString(stringBuffer.append(this.fileId).append("T").toString(), this.Title).commit();
            SharedPreferences.Editor editor2 = this.S_pref.edit();
            StringBuffer stringBuffer2 = new StringBuffer();
            editor2.putString(stringBuffer2.append(this.fileId).append("C").toString(), this.Content).commit();
			S_pref.edit().putString(fileId+"type","Note").commit();
            if (!this.iAction.equals((Object)"NEW")) return;
            this.fileId = 1 + this.fileId;
            this.S_pref.edit().putInt("FileId", this.fileId).commit();
            return;
        }
        catch (Exception e) {

        }
    }

	private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
		}
		return false;
	}
	private void handleSendText(Intent intent) {
        uContent = intent.getStringExtra("android.intent.extra.TEXT");
        Title = intent.getStringExtra("android.intent.extra.SUBJECT");
        if(uContent==null&&Title!=null){
			uContent=Title;Title="";
		}
		if (Title == null) {
            Title= "";
        }
        if (uContent== null) {
			uContent = "";
        }
        edit_title.setText(Title);
        edit_content.setText(uContent);
        fileId = S_pref.getInt("FileId", 0);
        iAction = "NEW";
        Category = "Notes";
    }
	private void ShowMessage(String _s){
		Toast.makeText(mcontext,_s, Toast.LENGTH_SHORT).show();
	}
}