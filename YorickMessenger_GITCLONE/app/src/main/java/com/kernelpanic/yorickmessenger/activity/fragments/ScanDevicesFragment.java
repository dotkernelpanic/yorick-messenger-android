package com.kernelpanic.yorickmessenger.activity.fragments;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.activity.MainAppActivity;
import com.kernelpanic.yorickmessenger.adapters.DevicesListAdapter;
import com.kernelpanic.yorickmessenger.service.BluetoothChatService;
import com.kernelpanic.yorickmessenger.util.Device;

import java.util.ArrayList;
import java.util.Set;

public class ScanDevicesFragment extends Fragment {

    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    protected final String PERMISSION_REQUEST_LOCATION_KEY = "PERMISSION_REQUEST_LOCATION";
    protected AppCompatButton testOpenChatButton;
    protected FragmentManager fragmentManager;
    protected ChatFragment chatFragment;
    protected MainAppActivity mainAppActivity;
    protected boolean isAlreadyAskedForPermission = false;
    protected ListView deviceList;
    protected ListView devicePairedList;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService bluetoothChatService;

    private ArrayList<Device> devices = new ArrayList<Device>();
    private DevicesListAdapter newDevicesListArrayAdapter;
    private ArrayList<Device> newDevicesListArray;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedAction = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(receivedAction)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Device tempdevice = new Device(device.getName(), device.getAddress());
                    //newDevicesListArray.add(tempdevice);
                    newDevicesListArrayAdapter.add(tempdevice);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(receivedAction)) {
                if (newDevicesListArray.size() == 0) {
                    //newDevicesListArray.add("Wow! Such empty...");
                    newDevicesListArrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };
    private DevicesListAdapter pairedDevicesListArrayAdapter;
    private AdapterView.OnItemClickListener onDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mBluetoothAdapter.cancelDiscovery();

            TextView deviceName = view.findViewById(R.id.deviceNameListViewLabel);
            TextView deviceAddress = view.findViewById(R.id.deviceAddressListViewLabel);

            //String info     = ((TextView) view).getText().toString();
            //String address   = info.substring(info.length() - 17);

            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress.getText().toString());

            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, deviceAddress.getText().toString());
            getActivity().setResult(Activity.RESULT_OK, intent);

            //TextView status = getActivity().findViewById(R.id.statusLabel);
            //status.setText(getString(R.string.app_mainActivity_connected, device.getName()));

            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.scanFragmentContainer, chatFragment, "chatFragment")
                    .commit();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_devices, container, false);

        testOpenChatButton = view.findViewById(R.id.searchDevicesButton);
        chatFragment = new ChatFragment();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkPermission();
        discoverDevices(view);

        if (savedInstanceState != null) {
            isAlreadyAskedForPermission =
                    savedInstanceState.getBoolean(PERMISSION_REQUEST_LOCATION_KEY, false);
        }

        pairedDevicesListArrayAdapter = new DevicesListAdapter(view.getContext(), R.layout.list_single_item);
        newDevicesListArray = new ArrayList<Device>();
        newDevicesListArrayAdapter = new DevicesListAdapter(view.getContext(), R.layout.list_single_item);

        deviceList = view.findViewById(R.id.deviceList);
        deviceList.setAdapter(newDevicesListArrayAdapter);
        deviceList.setOnItemClickListener(onDeviceClickListener);

        devicePairedList = view.findViewById(R.id.devicePairedList);
        devicePairedList.setAdapter(pairedDevicesListArrayAdapter);
        devicePairedList.setOnItemClickListener(onDeviceClickListener);

        IntentFilter actionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(broadcastReceiver, actionFoundFilter);

        IntentFilter actionDiscoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(broadcastReceiver, actionDiscoveryFinishedFilter);

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();


        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                Device tempdevice = new Device(device.getName(), device.getAddress());
                pairedDevicesListArrayAdapter.add(tempdevice);
            }
        } else {
            String noDevices = "No devices"; //TODO: Add string to strings.xml
        }

        testOpenChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(R.anim.fragment_swipe_inleft, R.anim.fragment_swipe_outright)
                        .replace(R.id.scanFragmentContainer, chatFragment, "chatFragment")
                        .commit();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Failed to initialize bluetooth, stop", Toast.LENGTH_LONG);
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.startDiscovery();
            }
        }
    }

    private void discoverDevices(View view) {
        Toast.makeText(view.getContext(), "hello there", Toast.LENGTH_LONG).show();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        mBluetoothAdapter.startDiscovery();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permission = getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permission += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permission != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2268);
            }
        } else {
            Log.d("PERMISSION", "Checking permissions: no need to do this - SDK version < LOLLIPOP");
        }
    }
}