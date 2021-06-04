package com.kernelpanic.yorickmessenger.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.util.Prefs;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        schedule();
    }

    private void schedule() {
        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(2500);

                    int nightModeFlag =
                            getResources().getConfiguration().uiMode &
                                    Configuration.UI_MODE_NIGHT_MASK;

                    SharedPreferences preferencesNightMode = getSharedPreferences(Prefs.PREFERENCE_NAME_NIGHTMODE, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferencesNightMode.edit();

                    switch (nightModeFlag) {
                        case Configuration.UI_MODE_NIGHT_YES:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            editor.putBoolean(Prefs.PREFERENCE_KEY_NIGHTMODE, true);
                            break;
                        case Configuration.UI_MODE_NIGHT_NO:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            editor.putBoolean(Prefs.PREFERENCE_KEY_NIGHTMODE, false);
                            break;
                    }

                    Intent intent = new Intent(SplashScreenActivity.this, NewUserSupportActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

}