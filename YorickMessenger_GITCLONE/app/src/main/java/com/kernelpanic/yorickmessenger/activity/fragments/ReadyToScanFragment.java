package com.kernelpanic.yorickmessenger.activity.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.kernelpanic.yorickmessenger.R;

public class ReadyToScanFragment extends Fragment {

    private final int PERMISSION_REQUEST_CONNECT_DEVICE_SECURE = 3;
    protected AppCompatButton launchScanDevicesFragmentButton;
    protected FragmentManager fragmentManager;
    protected ChatFragment chatFragment;
    protected ScanDevicesFragment scanDevicesFragment;

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
        chatFragment = new ChatFragment();

        launchScanDevicesFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fragment_swipe_inleft, R.anim.fragment_swipe_outright)
                        .replace(R.id.homeFragmentContainer, chatFragment, "chatFragment")
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        //AppCompatButton testbtn = getActivity().findViewById(R.id.tstbtn);

        return view;
    }

}