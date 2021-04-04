package com.kernelpanic.yorickmessenger_gitclone.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kernelpanic.yorickmessenger_gitclone.activity.fragments.ConfidentialFragment;
import com.kernelpanic.yorickmessenger_gitclone.activity.fragments.EasyToUseFragment;
import com.kernelpanic.yorickmessenger_gitclone.activity.fragments.NoWifiNeededFragment;

public class UserSupportAdapter extends FragmentStateAdapter {

    public UserSupportAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NoWifiNeededFragment();
            case 1:
                return new EasyToUseFragment();
            default:
                return new ConfidentialFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
