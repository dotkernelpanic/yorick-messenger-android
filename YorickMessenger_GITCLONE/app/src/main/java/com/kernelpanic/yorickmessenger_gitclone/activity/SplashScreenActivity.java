package com.kernelpanic.yorickmessenger_gitclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.kernelpanic.yorickmessenger_gitclone.R;

import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        startActivity(new Intent(this, NewUserSupportActivity.class));
        finish();
    }

}