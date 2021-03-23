package com.kernelpanic.yorickmessenger_gitclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kernelpanic.yorickmessenger_gitclone.R;
import com.kernelpanic.yorickmessenger_gitclone.activity.fragments.EasyToUseFragment;
import com.kernelpanic.yorickmessenger_gitclone.activity.fragments.NoWifiNeededFragment;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().hide();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragmentContainer, EasyToUseFragment.class, null)
                .commit();

        Button getNewFragmentButton = findViewById(R.id.getNewFragmentButton);
        getNewFragmentButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, NoWifiNeededFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

    }
}