package com.kernelpanic.yorickmessenger.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.service.BluetoothChatService;
import com.kernelpanic.yorickmessenger.util.Constants;
import com.kernelpanic.yorickmessenger.util.RealPathHelper;

import java.io.File;
import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    final int FILE_SELECT_CODE =1;

    Button btn;
    TextView pathText;
    TextView uriText;
    ImageView imageView;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btn = findViewById(R.id.btn);
        pathText = findViewById(R.id.path);
        uriText = findViewById(R.id.uri);
        imageView = findViewById(R.id.image);

        Intent filePickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        filePickIntent.setType("*/*");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, FILE_SELECT_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                Uri uri = data.getData();
                // DocumentProvider
                if (isKitKat && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
                    System.out.println("getPath() uri: " + uri.toString());
                    System.out.println("getPath() uri authority: " + uri.getAuthority());
                    System.out.println("getPath() uri path: " + uri.getPath());

                    uriText.setText(uri.getAuthority());

                    // ExternalStorageProvider
                    if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];
                        System.out.println("getPath() docId: " + docId + ", split: " + split.length + ", type: " + type);

                        // This is for checking Main Memory
                        if ("image".equalsIgnoreCase(type)) {
                            if (split.length > 1) {
                                path = Environment.getExternalStorageDirectory() + "/" + split[1] + "/";
                            } else {
                                path = Environment.getExternalStorageDirectory() + "/";
                            }
                            // This is for checking SD Card
                        } else {
                            path = "storage" + "/" + docId.replace(":", "/");
                        }
                    }
                }
        }
    }
}