package com.kernelpanic.yorickmessenger_gitclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.kernelpanic.yorickmessenger_gitclone.R;
import com.kernelpanic.yorickmessenger_gitclone.activity.fragments.ChatFragment;
import com.kernelpanic.yorickmessenger_gitclone.activity.fragments.ReadyToScanFragment;
import com.kernelpanic.yorickmessenger_gitclone.util.SoftInputAssist;

public class MainAppActivity extends AppCompatActivity {

    protected   SoftInputAssist     softInputAssistant;
    protected   AppCompatButton     launchScanDevicesFragmentButton;
    protected   FragmentManager     fragmentManager;
    protected   ImageView           goBackToScanFragment;
    protected   TextView status;
    protected   NavigationView    navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.blue_indirectResort));

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragmentContainer, ReadyToScanFragment.class, null)
                .commit();

        Fragment homeFragment = getSupportFragmentManager().findFragmentById(R.id.scanFragmentContainer);
        navigationView = findViewById(R.id.navMenuView);

        /*
        goBackToScanFragment = findViewById(R.id.goHomeButton);
        goBackToScanFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(homeFragment instanceof ReadyToScanFragment)) {
                    fragmentManager.beginTransaction()
                            .setReorderingAllowed(true)
                            .setCustomAnimations(R.anim.fragment_swipe_inleft, R.anim.fragment_swipe_outright)
                            .replace(R.id.fragmentContainer, ReadyToScanFragment.class, null)
                            .commit();
                }
            }
        });
         */

        softInputAssistant = new SoftInputAssist(this);
        status = findViewById(R.id.statusLabel);
    }

    @Override
    protected void onPause() {
        softInputAssistant.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        softInputAssistant.onResume();
        super.onResume();
    }




    @Override
    protected void onDestroy() {
        softInputAssistant.onDestroy();
        super.onDestroy();
    }



    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

}