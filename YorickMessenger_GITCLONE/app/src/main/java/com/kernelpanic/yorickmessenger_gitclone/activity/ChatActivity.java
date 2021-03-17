package com.kernelpanic.yorickmessenger_gitclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kernelpanic.yorickmessenger_gitclone.R;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().hide();
    }
}