package com.kernelpanic.yorickmessenger_gitclone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kernelpanic.yorickmessenger_gitclone.activity.MainAppActivity;
import com.kernelpanic.yorickmessenger_gitclone.activity.fragments.ChatFragment;
import com.kernelpanic.yorickmessenger_gitclone.adapters.DevicesListAdapter;
import com.kernelpanic.yorickmessenger_gitclone.service.BluetoothChatService;
import com.kernelpanic.yorickmessenger_gitclone.util.Device;

import java.util.ArrayList;
import java.util.Set;

public class ScanListActivity extends AppCompatActivity {

    protected AppCompatButton testOpenChatButton;
    protected FragmentManager fragmentManager;
    protected ChatFragment chatFragment;
    protected MainAppActivity mainAppActivity;

    protected final String PERMISSION_REQUEST_LOCATION_KEY = "PERMISSION_REQUEST_LOCATION";

    protected boolean isAlreadyAskedForPermission = false;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private String bluetoothDeviceAddress = " ";
    public static final int SCANLIST_CLOSED_REQUEST_CODE = 1;

    protected   ListView deviceList;
    protected   ListView devicePairedList;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService bluetoothChatService;

    private ArrayList<Device> devices = new ArrayList<Device>();
    private DevicesListAdapter newDevicesListArrayAdapter;
    private     ArrayList<Device>       newDevicesListArray;
    private     DevicesListAdapter      pairedDevicesListArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_scan_list);

        setResult(Activity.RESULT_CANCELED);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        scanDevices();

        pairedDevicesListArrayAdapter   = new DevicesListAdapter(this, R.layout.list_single_item);
        newDevicesListArray             = new ArrayList<Device>();
        newDevicesListArrayAdapter      = new DevicesListAdapter(this, R.layout.list_single_item);

        deviceList                      = findViewById(R.id.deviceList);
        devicePairedList                = findViewById(R.id.devicePairedList);

        deviceList.setAdapter(newDevicesListArrayAdapter);
        deviceList.setOnItemClickListener(onDeviceTapListener);

        devicePairedList.setAdapter(pairedDevicesListArrayAdapter);
        devicePairedList.setOnItemClickListener(onDeviceTapListener);

        IntentFilter actionFoundIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver, actionFoundIntentFilter);

        IntentFilter actionDiscovertFinishedIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(broadcastReceiver, actionDiscovertFinishedIntentFilter);

        Set<BluetoothDevice> pairedDevicesSet = mBluetoothAdapter.getBondedDevices();

        if (pairedDevicesSet.size() > 0) {
            for (BluetoothDevice btDevice : pairedDevicesSet) {
                Device mBtDevice = new Device(btDevice.getName(), btDevice.getAddress());
                pairedDevicesListArrayAdapter.add(mBtDevice);
            }
        } else {
            String noDevices = "Wow, such empty!";
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Failed to initialize bluetooth", Toast.LENGTH_LONG).show();
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.startDiscovery();
            }
        }
    }

    private void scanDevices() {
        if (mBluetoothAdapter.isDiscovering()) mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String receivedAction = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(receivedAction)) {
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (btDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Device mBtDevice = new Device(btDevice.getName(), btDevice.getAddress());
                    newDevicesListArray.add(mBtDevice);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(receivedAction)) {
                if (newDevicesListArray.size() == 0) {
                    newDevicesListArrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permission = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permission += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permission != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 2268);

            }
        } else {
            Log.d("Y.Messenger-PERMISSIONS", "Checking permissions: no need to do this, SDK < LOLLIPOP");
        }
    }

    private AdapterView.OnItemClickListener onDeviceTapListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mBluetoothAdapter.cancelDiscovery();
            chatFragment = new ChatFragment();

            TextView btDeviceName       = view.findViewById(R.id.deviceNameListViewLabel);
            TextView btDeviceAddress    = view.findViewById(R.id.deviceAddressListViewLabel);

            BluetoothDevice btDevice    = mBluetoothAdapter.getRemoteDevice(btDeviceAddress.getText().toString());

            Intent intent               = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, btDeviceAddress.getText().toString());

            Bundle bundle = new Bundle();
            String macAdress = btDeviceAddress.getText().toString();
            bundle.putString("bluetooth_device_mac_address", macAdress);
            chatFragment.setArguments(bundle);

            setResult(Activity.RESULT_OK, intent);
            setDeviceAddres(btDeviceAddress.getText().toString());
            setResult(Activity.RESULT_OK);
            finish();
        }
    };

    private void setDeviceAddres(String deviceAddress) {
        bluetoothDeviceAddress = deviceAddress;
    }

    public String getDeviceAddress() {
        return bluetoothDeviceAddress;
    }
}