package com.kernelpanic.yorickmessenger.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.activity.MainAppActivity;
import com.kernelpanic.yorickmessenger.activity.fragments.ChatFragment;
import com.kernelpanic.yorickmessenger.adapters.DevicesListAdapter;
import com.kernelpanic.yorickmessenger.service.BluetoothChatService;
import com.kernelpanic.yorickmessenger.service.BluetoothChatServiceClass;
import com.kernelpanic.yorickmessenger.util.Device;

import java.util.ArrayList;
import java.util.Set;

public class ScanListActivity extends AppCompatActivity {

    protected AppCompatButton   testOpenChatButton;
    protected FragmentManager   fragmentManager;
    protected ChatFragment      chatFragment;
    protected MainAppActivity   mainAppActivity;
    protected AppCompatButton   stopScanButton;
    protected ProgressBar       progressBar;

    protected final int PERMISSION_REQUEST_LOCATION_KEY = 1;

    protected boolean isAlreadyAskedForPermission = false;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private String bluetoothDeviceAddress = " ";
    public static final int BLUETOOTH_ENADLE_REQUEST_CODE = 2;

    protected   ListView deviceList;
    protected   ListView devicePairedList;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatServiceClass bluetoothChatService;

    private ArrayList<Device> devices = new ArrayList<Device>();
    private DevicesListAdapter newDevicesListArrayAdapter;
    private     ArrayList<Device>       newDevicesListArray;
    private     DevicesListAdapter      pairedDevicesListArrayAdapter;

    private final int PERMISSIONS_REQUEST_CODE = 3;
    private final String[]  PERMISSIONS     = new String[] {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_scan_list);

        setResult(Activity.RESULT_CANCELED);

        pairedDevicesListArrayAdapter   = new DevicesListAdapter(this, R.layout.list_single_item);
        newDevicesListArray             = new ArrayList<Device>();
        newDevicesListArrayAdapter      = new DevicesListAdapter(this, R.layout.list_single_item);

        deviceList                      = findViewById(R.id.deviceList);
        devicePairedList                = findViewById(R.id.devicePairedList);

        deviceList.setAdapter(newDevicesListArrayAdapter);
        deviceList.setOnItemClickListener(onDeviceTapListener);

        devicePairedList.setAdapter(pairedDevicesListArrayAdapter);
        devicePairedList.setOnItemClickListener(onDeviceTapListener);

        IntentFilter bluetoothActionIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver, bluetoothActionIntent);

        bluetoothActionIntent = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(broadcastReceiver, bluetoothActionIntent);

        progressBar = findViewById(R.id.progressBar);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        findSomeRemoteDevices();

        stopScanButton = findViewById(R.id.stopScanButton);
        stopScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

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
    protected void onStart() {
        super.onStart();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, BLUETOOTH_ENADLE_REQUEST_CODE);
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

    private boolean isAllPermissionsGranted() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    private void findSomeRemoteDevices() {
        checkPermissions();
    }

    private boolean isBluetoothSupported() {
        if (mBluetoothAdapter == null) {
            return false;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                return true;
            }
        }
        return true;
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String receivedAction = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(receivedAction)) {
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (btDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                    Device mBtDevice = new Device(btDevice.getName(), btDevice.getAddress());
                    newDevicesListArrayAdapter.add(mBtDevice);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(receivedAction)) {
                if (newDevicesListArray.size() == 0) {
                    newDevicesListArrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {
                startDiscovery();
                progressBar.setVisibility(View.VISIBLE);
            }
        } else {
            startDiscovery();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void startDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    private AdapterView.OnItemClickListener onDeviceTapListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mBluetoothAdapter.cancelDiscovery();
            chatFragment = new ChatFragment();

            mBluetoothAdapter.cancelDiscovery();

            TextView btDeviceName       = view.findViewById(R.id.deviceNameListViewLabel);
            TextView btDeviceAddress    = view.findViewById(R.id.deviceAddressListViewLabel);

            Intent intent               = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, btDeviceAddress.getText().toString());

            Toast.makeText(ScanListActivity.this, "BT DEVICE: " + btDeviceAddress.getText().toString(), Toast.LENGTH_SHORT).show();

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    private void makeDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                mBluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 500);
            startActivity(discoverable);
        }
    }

    private void setDeviceAddres(String deviceAddress) {
        bluetoothDeviceAddress = deviceAddress;
    }

    public String getDeviceAddress() {
        return bluetoothDeviceAddress;
    }
}