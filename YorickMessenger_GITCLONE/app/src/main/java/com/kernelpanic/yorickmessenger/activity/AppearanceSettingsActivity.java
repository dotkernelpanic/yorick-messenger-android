package com.kernelpanic.yorickmessenger.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreferenceCompat;

import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.util.Prefs;

import org.w3c.dom.Text;

import java.util.prefs.Preferences;

public class AppearanceSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

       // window.setStatusBarColor(this.getResources().getColor(R.color.appBackground));

        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            SwitchPreferenceCompat nightModeCompat = findPreference("nightMode");
            nightModeCompat.setWidgetLayoutResource(R.layout.custom_switch);
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());

            boolean currentNightModeState = sharedPreferences.getBoolean("nightMode", false);
            boolean prefsNightModeState = getSharedNightModePreference();

            if (currentNightModeState != prefsNightModeState)
                nightModeCompat.setChecked(prefsNightModeState);
        }

        private void savenNightModePrefs(String key, boolean b) {
            SharedPreferences preferences = getActivity().getSharedPreferences(Prefs.PREFERENCE_NAME_NIGHTMODE, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Prefs.PREFERENCE_KEY_NIGHTMODE, b);
            editor.commit();
        }

        private boolean getSharedNightModePreference() {
            SharedPreferences preferences = getActivity().getSharedPreferences(Prefs.PREFERENCE_NAME_NIGHTMODE, Context.MODE_PRIVATE);
            return preferences.getBoolean(Prefs.PREFERENCE_KEY_NIGHTMODE, false);
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference prefs = findPreference("nightMode");
            switch (key) {
                case "nightMode":
                    Toast.makeText(getActivity(), "Night mode state changed", Toast.LENGTH_SHORT).show();
                    //savenNightModePrefs(key, true);
            }
        }
    }


}