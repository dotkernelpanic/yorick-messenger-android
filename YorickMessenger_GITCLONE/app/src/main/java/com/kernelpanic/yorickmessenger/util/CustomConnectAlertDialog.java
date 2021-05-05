package com.kernelpanic.yorickmessenger.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.kernelpanic.yorickmessenger.R;

public class CustomConnectAlertDialog extends Dialog implements View.OnClickListener {

    private static String EXTRA_DEVICE_ADDRESS;
    private Activity activity;
    private Dialog dialog;
    private AppCompatButton yesBtn, noBtn;
    private TextView text;
    private String deviceName;
    private String deviceAddress;
    private Intent intent;

    public CustomConnectAlertDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void initData(Intent intent,
                         String extras,
                         String deviceName,
                         String deviceAddress) {
        this.intent = intent;
        EXTRA_DEVICE_ADDRESS = extras;
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_layout);
        yesBtn = findViewById(R.id.yesBtn);
        noBtn = findViewById(R.id.noBtn);
        text = findViewById(R.id.text);
        yesBtn.setOnClickListener(this);
        noBtn.setOnClickListener(this);
        text.setText("Do you want to connect to: \n" + deviceName + "?");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yesBtn:
                intent.putExtra(EXTRA_DEVICE_ADDRESS, deviceAddress);
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
                break;
            case R.id.noBtn:
                dismiss();
                break;
        }
        dismiss();
    }
}
