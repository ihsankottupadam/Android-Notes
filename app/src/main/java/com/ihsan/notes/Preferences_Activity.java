package com.ihsan.notes;

import android.content.Intent;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.*;
import android.content.SharedPreferences;
import android.content.SharedPreferences.*;
import android.view.View;
import android.view.*;
import android.widget.*;
import android.content.Context;
import android.graphics.*;
import android.view.MenuItem;
import android.net.*;
import android.database.*;
import android.provider.*;
import android.renderscript.*;

public class Preferences_Activity extends PreferenceActivity {
    private Intent intent_view = new Intent();
    private SharedPreferences S_pref;
    private final static int RESULT_LOAD_IMAGE = 1;
    private boolean islocked = false;
    private Context context;

    PreferenceScreen lockPrefScreen;
    PreferenceScreen about;
    Preference set_pass;
    Preference change_pass;
    Preference disable_pass;
    ListPreference lockwall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preference);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Context context = this;
        SharedPreferences.OnSharedPreferenceChangeListener prefListener;
        S_pref = PreferenceManager.getDefaultSharedPreferences(context);
        Intent IntentSetpass = new Intent();
        IntentSetpass.setClass(getApplicationContext(), Screen_activity.class);
        IntentSetpass.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        IntentSetpass.putExtra("Action", "SET");
        Intent IntentChangepass = new Intent();
        IntentChangepass.setClass(getApplicationContext(), Screen_activity.class);
        IntentChangepass.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        IntentChangepass.putExtra("Action", "CHANGE");
        Intent IntentDispass = new Intent();
        IntentDispass.setClass(getApplicationContext(), Screen_activity.class);
        IntentDispass.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        IntentDispass.putExtra("Action", "DISABLE");
        Intent IntentAbout = new Intent();
        IntentAbout.setClass(getApplicationContext(), AboutActivity.class);
        IntentDispass.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        islocked = S_pref.getBoolean("IsAppLocked", false);
        lockPrefScreen = (PreferenceScreen) findPreference("lock");
        set_pass = (Preference) findPreference("Set_password");
        change_pass = (Preference) findPreference("Change_password");
        disable_pass = (Preference) findPreference("Disable_password");
        about = (PreferenceScreen) findPreference("about");
        lockwall = (ListPreference) findPreference("lockWallpaper");
        set_pass.setIntent(IntentSetpass);
        change_pass.setIntent(IntentChangepass);
        disable_pass.setIntent(IntentDispass);
        about.setIntent(IntentAbout);
        refreshlock();
        lockwall.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String val = (String) newValue;
                if (val.equals("Pick from gallery")) {
                    Intent i = new Intent(
                            Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmapOriginal = BitmapFactory.decodeFile(picturePath);
            Bitmap pick = createBitmap_ScriptIntrinsicBlur(bitmapOriginal, 13.0f);
            new ImageSaver(this).
                    setFileName("myImage.png").
                    setDirectoryName("Wallpaper").
                    save(pick);
            showMessage("Wallpaper changed");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshlock();
    }

    private void refreshlock() {
        islocked = S_pref.getBoolean("IsAppLocked", false);
        if (!islocked) {
            change_pass.setEnabled(false);
            lockPrefScreen.removePreference(disable_pass);
            lockPrefScreen.addPreference(set_pass);
            lockwall.setEnabled(false);
        } else {
            lockPrefScreen.removePreference(set_pass);
            change_pass.setEnabled(true);
            lockPrefScreen.addPreference(disable_pass);
            lockwall.setEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            intent_view.setClass(getApplicationContext(), MainActivity.class);
            intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_view.putExtra("State", "Open");
            startActivity(intent_view);
        }

        return false;
    }

    private Bitmap createBitmap_ScriptIntrinsicBlur(Bitmap src, float r) {   //Radius range (0 < r <= 25)   
        if (r <= 0) {
            r = 0.1f;
        } else if (r > 25) {
            r = 25.0f;
        }
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript renderScript = RenderScript.create(this);
        Allocation blurInput = Allocation.createFromBitmap(renderScript, src);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(r);
        blur.forEach(blurOutput);
        blurOutput.copyTo(bitmap);
        renderScript.destroy();
        return bitmap;
    }

    private void showMessage(String _s) {
        Toast.makeText(Preferences_Activity.this, _s, Toast.LENGTH_SHORT).show();
    }
}