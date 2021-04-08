package com.kernelpanic.yorickmessenger_gitclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kernelpanic.yorickmessenger_gitclone.R;
import com.kernelpanic.yorickmessenger_gitclone.adapters.UserSupportAdapter;

public class NewUserSupportActivity extends AppCompatActivity {

    private TabLayout               tabIndicator;
    private AppCompatButton         buttonNext;
    private AppCompatButton         buttonGetStarted;
    private int                     tabPosition = 0;
    private Animation               buttonGetStartedAnimation;

    private final int       TABS_COUNT = 3;
    private final String    PREFERENCES_NAME = "yorickmessenger_preferences";
    private final String    PREFERENCE_BOOL_KEY = "isUserWatchedIntroActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().hide();


        if (checkUserSharedPreferences()) {
            Log.i("YMSNGR_USER_ACT", "User has watched the intro before, launching ChatActivity");
            Intent chatActivity = new Intent(getApplicationContext(), MainAppActivity.class);
            startActivity(chatActivity);
            finish();
        }

        setContentView(R.layout.activity_new_user_support);

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

    private boolean checkUserSharedPreferences() {

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        Boolean isUserWatchedIntroActivity = preferences.getBoolean(PREFERENCE_BOOL_KEY, false);
        return isUserWatchedIntroActivity;

    }

    private void getStarted() {

        buttonNext.setVisibility(View.INVISIBLE);
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
}