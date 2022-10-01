package com.ihsan.notes;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.Toast;
import android.widget.*;
import android.preference.PreferenceManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.animation.*;
import android.text.method.*;

import java.util.Timer;
import java.util.TimerTask;

import android.graphics.*;
import android.graphics.drawable.*;

public class Screen_activity extends Activity {
    private String password = "";
    private String Act = "";
    private String waltype;
    private String CurrentPass;
    private String NewPass;
    private boolean isFromEdit;


    private ImageView dot1;
    private ImageView dot2;
    private ImageView wal;
    private ImageView dot3;
    private ImageView dot4;
    private RelativeLayout bgl;
    private TextView txtact;
    private RelativeLayout animview;
    private Vibrator vibrate;
    private Timer _timer = new Timer();
    private TimerTask timer;
    private SharedPreferences S_pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_screen);
        Act = getIntent().getStringExtra("Action");
        dot1 = (ImageView) findViewById(R.id.dot_one);
        dot2 = (ImageView) findViewById(R.id.dot_two);
        dot3 = (ImageView) findViewById(R.id.dot_three);
        dot4 = (ImageView) findViewById(R.id.dot_four);
        wal = (ImageView) findViewById(R.id.Wallpaper);
        bgl = (RelativeLayout) findViewById(R.id.layout_password);
        txtact = (TextView) findViewById(R.id.lock_action);
        animview = (RelativeLayout) findViewById(R.id.layout_dot);
        Context context = this;
        S_pref = PreferenceManager.getDefaultSharedPreferences(context);
        waltype = S_pref.getString("lockWallpaper", "Default");
        if (waltype.equals("Pick from gallery")) {
            Bitmap bitmap = new ImageSaver(context).
                    setFileName("myImage.png").
                    setDirectoryName("Wallpaper").
                    load();
            wal.setImageBitmap(bitmap);
            bgl.setBackgroundColor(0x88000000);
        }
        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Act.equals("FromEdit")) {
            Act = "UNLOCK";
            isFromEdit = true;
        }
        if (Act.equals("UNLOCK") || Act.equals("Disable")) {
            txtact.setText("Enter passcode");
        } else if (Act.equals("SET")) {
            txtact.setText("New passcode");
        } else if (Act.equals("CHANGE")) {
            txtact.setText("Enter current passcode");
        } else if (Act.equals("CONFORM")) {
            txtact.setText("Conform passcode");
        }

    }

    @Override
    public void onBackPressed() {
        if (Act.equals("UNLOCK")) {
            finishAffinity();
        } else {
            super.onBackPressed();
        }
    }

    public void BtnClick(View v) {
        switch (v.getId()) {
            case R.id.lb_0:
                password += "0";
                break;
            case R.id.lb_1:
                password += "1";
                break;
            case R.id.lb_2:
                password += "2";
                break;
            case R.id.lb_3:
                password += "3";
                break;
            case R.id.lb_4:
                password += "4";
                break;
            case R.id.lb_5:
                password += "5";
                break;
            case R.id.lb_6:
                password += "6";
                break;
            case R.id.lb_7:
                password += "7";
                break;
            case R.id.lb_8:
                password += "8";
                break;
            case R.id.lb_9:
                password += "9";
                break;
            default:
                break;
        }
        setdot();
    }

    private void error() {

        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        animview.startAnimation(animation1);
        vibrate.vibrate((long) (500));
        password = "";
        timer = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setdot();
                    }
                });
            }
        };
        _timer.schedule(timer, (int) (300));

    }

    private void setdot() {
        int l = password.length();
        if (l == 0) {
            dot1.setBackgroundResource(R.drawable.dot);
            dot2.setBackgroundResource(R.drawable.dot);
            dot3.setBackgroundResource(R.drawable.dot);
            dot4.setBackgroundResource(R.drawable.dot);
        }
        if (l == 1) {
            dot1.setBackgroundResource(R.drawable.dot_filled);
            dot2.setBackgroundResource(R.drawable.dot);
            dot3.setBackgroundResource(R.drawable.dot);
            dot4.setBackgroundResource(R.drawable.dot);
        }
        if (l == 2) {
            dot1.setBackgroundResource(R.drawable.dot_filled);
            dot2.setBackgroundResource(R.drawable.dot_filled);
            dot3.setBackgroundResource(R.drawable.dot);
            dot4.setBackgroundResource(R.drawable.dot);
        }
        if (l == 3) {
            dot1.setBackgroundResource(R.drawable.dot_filled);
            dot2.setBackgroundResource(R.drawable.dot_filled);
            dot3.setBackgroundResource(R.drawable.dot_filled);
            dot4.setBackgroundResource(R.drawable.dot);
        }
        if (l == 4) {
            dot1.setBackgroundResource(R.drawable.dot_filled);
            dot2.setBackgroundResource(R.drawable.dot_filled);
            dot3.setBackgroundResource(R.drawable.dot_filled);
            dot4.setBackgroundResource(R.drawable.dot_filled);
        }
        if (password.length() == 4) {
            Act();
        }
    }

    private void Act() {
        if (Act.equals("UNLOCK")) {
            boolean istoapp = false;
            CurrentPass = S_pref.getString("CurrentPass", "");
            if (password.equals("7864")) {
                try {
                    startActivity(new Intent("com.Ihsan.TallyCounter.Main"));
                    istoapp = true;
                    finishAffinity();
                } catch (Exception e) {

                }
            }
            if (password.equals("7860")) {
                try {
                    Intent intent = new Intent();
                    intent.setAction("com.Ihsan.TallyCounter.Start");
                    sendBroadcast(intent);
                    istoapp = true;
                    finishAffinity();
                } catch (Exception e) {

                }
            }
            if (password.equals("0783")) {
                try {
                    startActivity(new Intent("com.Ihsan.MyTools.Main"));
                    istoapp = true;
                    finishAffinity();
                } catch (Exception e) {

                }
            }
            if (istoapp) {
                return;
            }
            if (CurrentPass.equals(password)) {
                if (isFromEdit) {
                    finish();
                    return;
                } else {
                    Intent intent_view = new Intent();
                    intent_view.setClass(getApplicationContext(), MainActivity.class);
                    intent_view.putExtra("State", "Open");
                    intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent_view);
                }
            } else {
                error();
            }
        } else if (Act.equals("SET")) {
            S_pref.edit().putString("NewPass", password).commit();
            S_pref.edit().putBoolean("IsAppLocked", true).commit();
            Intent intent_view = new Intent();
            intent_view.setClass(getApplicationContext(), Screen_activity.class);
            intent_view.putExtra("Action", "CONFORM");
            intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent_view);
        } else if (Act.equals("CONFORM")) {
            NewPass = S_pref.getString("NewPass", "");
            if (NewPass.equals(password)) {
                S_pref.edit().putString("CurrentPass", password).commit();
                finish();
            } else {
                error();
            }
        } else if (Act.equals("CHANGE")) {
            CurrentPass = S_pref.getString("CurrentPass", "");
            if (CurrentPass.equals(password)) {
                Intent intent_view = new Intent();
                intent_view.setClass(getApplicationContext(), Screen_activity.class);
                intent_view.putExtra("Action", "SET");
                intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_view);
            } else {
                error();
            }
        } else if (Act.equals("DISABLE")) {
            CurrentPass = S_pref.getString("CurrentPass", "");
            if (CurrentPass.equals(password)) {
                S_pref.edit().putBoolean("IsAppLocked", false).commit();
                finish();
            } else {
                error();
            }
        }
    }

    private void ShowMesssage(String _s) {
        Toast.makeText(this, _s, Toast.LENGTH_SHORT).show();
    }
}