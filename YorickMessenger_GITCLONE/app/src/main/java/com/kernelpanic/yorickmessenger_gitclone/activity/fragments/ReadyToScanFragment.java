package com.kernelpanic.yorickmessenger_gitclone.activity.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;

import com.kernelpanic.yorickmessenger_gitclone.R;
import com.kernelpanic.yorickmessenger_gitclone.ScanDevicesFragment;

public class ReadyToScanFragment extends Fragment {

    protected AppCompatButton       launchScanDevicesFragmentButton;
    protected FragmentManager       fragmentManager;
    protected ChatFragment          chatFragment;
    protected ScanDevicesFragment   scanDevicesFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ready_to_scan, container, false);
        launchScanDevicesFragmentButton = view.findViewById(R.id.readyToScanButton);
        scanDevicesFragment = new ScanDevicesFragment();
        chatFragment        = new ChatFragment();

        launchScanDevicesFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fragment_swipe_inleft, R.anim.fragment_swipe_outright)
                        .replace(R.id.homeFragmentContainer, scanDevicesFragment, "scanFragment")
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        return view;
    }
}