package com.kernelpanic.yorickmessenger.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.activity.fragments.ChatFragment;
import com.kernelpanic.yorickmessenger.activity.fragments.CreateProfileFragment;
import com.kernelpanic.yorickmessenger.activity.fragments.ReadyToScanFragment;
import com.kernelpanic.yorickmessenger.database.SQLiteDbHelper;
import com.kernelpanic.yorickmessenger.util.SoftInputAssist;

public class MainAppActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected SoftInputAssist softInputAssistant;
    protected FragmentManager fragmentManager;
    protected Bitmap imageBitmap;
    protected NavigationView navigationView;
    protected Toolbar toolbar;
    protected ActionBarDrawerToggle drawerToggle;
    protected DrawerLayout drawerLayout;

    private ChatFragment chatFragment;
    private ReadyToScanFragment readyToScanFragment;

    private SQLiteDbHelper dbHelper;
    private String userInfo;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.blue_indirectResort));
        //window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        dbHelper = new SQLiteDbHelper(this);
        chatFragment = new ChatFragment();
        readyToScanFragment = new ReadyToScanFragment();

        //getSupportActionBar().hide();
        if (!checkUserProfile()) {
            Toast.makeText(this, "User Profile", Toast.LENGTH_SHORT).show();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragmentContainer, CreateProfileFragment.class, null)
                    .commit();
        } else {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragmentContainer, ReadyToScanFragment.class, null)
                    .commit();
            Toast.makeText(this, "Successfully loaded user's profile information", Toast.LENGTH_SHORT).show();
            userInfo = dbHelper.getUserInfo();
        }

        toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_mainActivity_appbar_open,
                R.string.app_mainActivity_appbar_close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);
        TextView userLabel = navHeaderView.findViewById(R.id.userInfo);
        ImageView profilePic = navHeaderView.findViewById(R.id.imageView);
        userLabel.setText(userInfo);
        profilePic.setImageBitmap(imageBitmap);


        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            Toast.makeText(this, "Opened", Toast.LENGTH_SHORT).show();
            toolbar.setBackgroundResource(R.drawable.gradient_sunnymorning);
        }

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment homeFragment = getSupportFragmentManager().findFragmentById(R.id.scanFragmentContainer);

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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.scanDevicesNavItem:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, chatFragment, null)
                        .setCustomAnimations(R.anim.fragment_swipe_inleft, R.anim.fragment_swipe_outright)
                        .setReorderingAllowed(true)
                        .commit();
                break;
            case R.id.homeNavItem:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, readyToScanFragment, null)
                        .setCustomAnimations(R.anim.fragment_swipe_inleft, R.anim.fragment_swipe_outright)
                        .commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean checkUserProfile() {
        SharedPreferences preferences = getSharedPreferences(CreateProfileFragment.PREFERENCE_NAME_USER_PROFILE, MODE_PRIVATE);
        return preferences.getBoolean(CreateProfileFragment.PREFERENCE_KEY_USER_PROFILE, false);
    }
}