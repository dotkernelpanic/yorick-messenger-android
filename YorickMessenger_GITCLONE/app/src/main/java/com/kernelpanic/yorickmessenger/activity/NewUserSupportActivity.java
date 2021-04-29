package com.kernelpanic.yorickmessenger.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.adapters.UserSupportAdapter;

import java.util.Locale;

public class NewUserSupportActivity extends AppCompatActivity {

    private TabLayout               tabIndicator;
    private AppCompatButton         buttonNext;
    private AppCompatButton         buttonGetStarted;
    private AppCompatButton         buttonSkip;
    private TextView                buttonChangeAppLocale;
    private int                     tabPosition = 0;
    private Animation               buttonGetStartedAnimation;

    private final int       TABS_COUNT = 3;
    private final String    PREFERENCES_NAME                    = "yorickmessenger_preferences";
    private final String    PREFERENCE_BOOL_KEY                 = "isUserWatchedIntroActivity";
    private final String    PREFERENCE_NAME_APPLICATION_LOCALE  = "yorickmessenger_locale_default";
    private final String    PREFERENCE_KEY_APPLICATION_LOCALE   = "locale";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().hide();

        String localeCode = getSharedPreferences(PREFERENCE_NAME_APPLICATION_LOCALE, MODE_PRIVATE)
                .getString(PREFERENCE_KEY_APPLICATION_LOCALE, "en");
        if (localeCode == null) localeCode = "en";
        setLocale(this, localeCode);

        if (checkUserSharedPreferences()) {
            Log.i("YMSNGR_USER_ACT", "User has watched the intro before, launching ChatActivity");
            Intent chatActivity = new Intent(getApplicationContext(), MainAppActivity.class);
            startActivity(chatActivity);
            finish();
        }

        setContentView(R.layout.activity_new_user_support);

        buttonChangeAppLocale   = findViewById(R.id.continueAs);
        buttonSkip              = findViewById(R.id.buttonSkip);

        Toast.makeText(this, localeCode, Toast.LENGTH_SHORT).show();

        if (checkLocaleSharedPreferences().equals("en")) {
            buttonChangeAppLocale.setText("Продовжити українською");
            buttonChangeAppLocale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLocale(NewUserSupportActivity.this, "uk");
                    Toast.makeText(NewUserSupportActivity.this, "Мову змінено, перезапустіть додаток", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (checkLocaleSharedPreferences().equals("uk")) {
            buttonChangeAppLocale.setText("Continue as English");
            buttonChangeAppLocale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLocale(NewUserSupportActivity.this, "en");
                    Toast.makeText(NewUserSupportActivity.this, "The language has been changed, restart the app", Toast.LENGTH_SHORT).show();
                }
            });
        }

        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserSharedPreferences();
                Intent chatActivity = new Intent(getApplicationContext(), MainAppActivity.class);
                startActivity(chatActivity);
                finish();
            }
        });

        ViewPager2 pager = findViewById(R.id.viewpager);
        pager.setAdapter(new UserSupportAdapter(this));

        tabIndicator = findViewById(R.id.tabIndicator);
        buttonNext = findViewById(R.id.buttonNext);
        buttonGetStarted = findViewById(R.id.buttonGetStarted);
        buttonGetStartedAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_get_started_animation);


        new TabLayoutMediator(tabIndicator, pager, ((tab, position) -> tab.setIcon(R.drawable.tab_indicator_selector))).attach();

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabPosition = pager.getCurrentItem();
                if (tabPosition < TABS_COUNT) {
                    tabPosition++;
                    pager.setCurrentItem(tabPosition);
                }

                if (tabPosition == TABS_COUNT - 1) {
                    getStarted();
                }
            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == TABS_COUNT - 1) {
                    getStarted();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        buttonGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainAppActivity = new Intent(getApplicationContext(), MainAppActivity.class);
                startActivity(mainAppActivity);
                saveUserSharedPreferences();
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private boolean checkUserSharedPreferences() {

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        Boolean isUserWatchedIntroActivity = preferences.getBoolean(PREFERENCE_BOOL_KEY, false);
        return isUserWatchedIntroActivity;

    }

    private String checkLocaleSharedPreferences() {
        String locale = "en";
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(PREFERENCE_NAME_APPLICATION_LOCALE, MODE_PRIVATE);
        locale = preferences.getString(PREFERENCE_KEY_APPLICATION_LOCALE, "en");
        return locale;
    }

    private void getStarted() {

        buttonNext.setVisibility(View.INVISIBLE);
        buttonSkip.setVisibility(View.INVISIBLE);
        buttonGetStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        buttonGetStarted.setAnimation(buttonGetStartedAnimation);

    }

    private void saveUserSharedPreferences() {

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREFERENCE_BOOL_KEY, true);
        editor.commit();

    }

    private void setLocale(Activity activity, String localeCode) {
        Locale locale = new Locale(localeCode);
        locale.setDefault(locale);

        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());


        SharedPreferences preferences = getApplicationContext().getSharedPreferences(PREFERENCE_NAME_APPLICATION_LOCALE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCE_KEY_APPLICATION_LOCALE, localeCode);
        editor.commit();
    }
}