package com.kernelpanic.yorickmessenger.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.activity.fragments.CreatePINCodeFragment;

public class CustomCreatePINCodeAlertDialog extends Dialog implements View.OnClickListener {

    private Activity activity;
    private AppCompatButton yesBtn, noBtn;
    private TextView textView;
    private Intent intent;
    private Fragment fragment;
    private String EXTRA_IS_USER_WANT_PINCODE;
    private FragmentManager fragmentManager;
    private Context context;

    public CustomCreatePINCodeAlertDialog(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yesBtn:
                CreatePINCodeFragment createPINCodeFragment = new CreatePINCodeFragment();
                activity.setResult(Activity.RESULT_OK, intent);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fragment_swipe_inleft, R.anim.fragment_swipe_outright)
                        .setReorderingAllowed(true)
                        .add(R.id.fragmentContainer, CreatePINCodeFragment.class, null)
                        .commit();
                break;
            case R.id.noBtn:
                SharedPreferences preferences = context.getSharedPreferences(
                        Prefs.PREFERENCE_NAME_CREATEPINCODE,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Prefs.PREFERENCE_KEY_PIN_WANT, false);
                editor.apply();
                dismiss();
                break;
        }
        dismiss();
    }

    public void initData(Intent intent, FragmentManager fragmentManager,
                         Context androidContext) {
        this.intent = intent;
        this.fragmentManager = fragmentManager;
        this.context = androidContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_layout);
        yesBtn = findViewById(R.id.yesBtn);
        noBtn = findViewById(R.id.noBtn);
        textView = findViewById(R.id.text);
        yesBtn.setOnClickListener(this);
        noBtn.setOnClickListener(this);
        textView.setText("Do you want to create PIN code?");
    }
}
