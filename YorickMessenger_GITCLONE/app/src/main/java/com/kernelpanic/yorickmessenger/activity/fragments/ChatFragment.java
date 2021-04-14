package com.kernelpanic.yorickmessenger.activity.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.activity.ScanListActivity;
import com.kernelpanic.yorickmessenger.activity.MainAppActivity;
import com.kernelpanic.yorickmessenger.service.BluetoothChatService;
import com.kernelpanic.yorickmessenger.service.BluetoothChatServiceClass;
import com.kernelpanic.yorickmessenger.util.Constants;

public class ChatFragment extends Fragment {

    private     TextView                    status;
    private     BluetoothAdapter            mBluetoothAdapter   = null;
    private     BluetoothChatService        chatService = null;
    private     ArrayAdapter<String>        conversationsListArrayAdapter;
    private     EditText                    inputField;
    private     ImageButton                 btnSend;
    private     ScanDevicesFragment         scanDevicesFragment;
    private     Toolbar                     toolbar;
    private     ListView                    chatView;

    private     StringBuffer                outStringBuffer;
    private     String                      connectedDeviceBluetoothName = null;
    private     SharedPreferences           statusPrefs;
    private     String                      globalDeviceAddress = "";

    private final String                    TAG = "Y.Messenger-Logs";

    protected final int PERMISSION_REQUEST_LOCATION_ID = 1;
    protected final int PERMISSION_REQUEST_ENABLE_BLUETOOTH = 2;
    protected final int PERMISSION_REQUEST_CONNECT_DEVICE_SECURE = 3;
    protected final int PERMISSION_REQUEST_CONNECT_DEVICE_INSECURE = 4;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            try {
                                Toast.makeText(getActivity(), "Connected", Toast.LENGTH_LONG).show();
                                conversationsListArrayAdapter.clear();
                                toolbar = getActivity().findViewById(R.id.appBar);
                                toolbar.setSubtitle(getString(R.string.app_mainActivity_connected));
                            } catch (IllegalStateException ex) {
                                Log.d(TAG, ex.getMessage());
                            }
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Toast.makeText(getActivity(), "Connecting", Toast.LENGTH_LONG).show();
                            toolbar = getActivity().findViewById(R.id.appBar);
                            toolbar.setSubtitle(getString(R.string.app_mainActivity_connecting));
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_LONG).show();
                            toolbar = getActivity().findViewById(R.id.appBar);
                            toolbar.setSubtitle(getString(R.string.app_mainActivity_notConnected));
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuffer = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuffer);
                    conversationsListArrayAdapter.add("You: " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuffer = (byte[]) msg.obj;
                    String readMessage = new String(readBuffer, 0, msg.arg1);
                    conversationsListArrayAdapter.add(connectedDeviceBluetoothName + ": " + readMessage);

                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(getActivity())
                                    .setAutoCancel(true)
                                    .setContentTitle(connectedDeviceBluetoothName)
                                    .setContentText(readMessage);

                    try {
                        Intent resultIntent = new Intent(getActivity(), MainAppActivity.class);
                        resultIntent.putExtra("chatFragment", "bluetoothChatFragment");
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
                        stackBuilder.addParentStack(MainAppActivity.class);
                        stackBuilder.addNextIntent(resultIntent);

                        int requestID = (int) System.currentTimeMillis();

                        PendingIntent resultPendingIntent = PendingIntent.getActivity(getActivity(), requestID, resultIntent, 0);
                        notificationBuilder.setContentIntent(resultPendingIntent);
                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        int id = 0;
                        //notificationManager.notify(id + 1, notificationBuilder.build());
                    } catch (NullPointerException ex) {
                        Log.d("Y.Messenger.handler()", ex.getMessage());
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                        connectedDeviceBluetoothName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), "Connected to: " + connectedDeviceBluetoothName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        //chatService = new BluetoothChatService(getActivity(), handler
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //String address = this.getArguments().getString("bluetooth_device_mac_address");
        //Toast.makeText(getActivity(), address, Toast.LENGTH_SHORT).show();
        try {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, PERMISSION_REQUEST_ENABLE_BLUETOOTH);
                mBluetoothAdapter.enable();
                Toast.makeText(getActivity(), "Enabling BT adapter", Toast.LENGTH_SHORT).show();
            } else if (chatService == null) {
                setupChat();
                Toast.makeText(getActivity(), "Initiating the scan devices procedure", Toast.LENGTH_SHORT).show();
                Intent server = new Intent(getActivity(), ScanListActivity.class);
                /*
                 Just for 'difficult tech process' imitating.
                 Joke. Made this to wait until the fragment change animation will done
                 */
                Handler postDelayedHandler = new Handler();
                postDelayedHandler.postDelayed(new Runnable()                     {
                    @Override
                    public void run() {
                        startActivityForResult(server, PERMISSION_REQUEST_CONNECT_DEVICE_SECURE);
                    }
                }, 1000);
            }
        } catch (NullPointerException ex) {
            Log.d(TAG, "We have caught an exception: " + ex.getMessage());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputField = view.findViewById(R.id.chatFieldInput);
        btnSend = view.findViewById(R.id.btnSend);
        chatView = view.findViewById(R.id.messagesContainer);

        try {
            String address = this.getArguments().getString("bluetooth_device_mac_address");
            Toast.makeText(getActivity(), address, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException ex) {
            Log.d(TAG, "We have caught an exception: " + ex.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (chatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (chatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                chatService.start();
            }
        }
    }

    private void setupChat() {
        conversationsListArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        chatView.setAdapter(conversationsListArrayAdapter);

        inputField.setOnEditorActionListener(writeListener);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "beep boop bop", Toast.LENGTH_LONG).show();
                View view = getView();
                if (null != view) {
                    //EditText textField = view.findViewById(R.id.chatFieldInput);
                    String message = inputField.getText().toString();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    sendMessage(message);
                }
            }
        });

        chatService = new BluetoothChatService(getActivity(), handler);
        //chatService = new BluetoothChatServiceClass(getActivity(), handler);
        outStringBuffer = new StringBuffer(" ");
    }

    private TextView.OnEditorActionListener writeListener
            = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String msg = v.getText().toString();
                sendMessage(msg);
            }
            return true;
        }
    };

    private void sendMessage(String msg) {
        if (chatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_SHORT).show(); //TODO: add string to strings.xml
            return;
        }

        if (msg.length() > 0) {
            byte[] sendData = msg.getBytes();
            Toast.makeText(getActivity(), sendData.toString(), Toast.LENGTH_SHORT).show();
            chatService.write(sendData);

            outStringBuffer.setLength(0);
            inputField.setText(outStringBuffer);
        }
    }

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        switch (reqCode) {
            case PERMISSION_REQUEST_CONNECT_DEVICE_SECURE:
                if (resCode == Activity.RESULT_OK && chatService.getState() != BluetoothChatService.STATE_CONNECTED) {
                    connectToDevice(data);
                    Toast.makeText(getActivity(), "The reqCode + " + reqCode + " The resCode: " + resCode,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_CONNECT_DEVICE_INSECURE:
                if (resCode == Activity.RESULT_OK && chatService.getState() != BluetoothChatService.STATE_CONNECTED) {
                    connectToDevice(data);
                }
            case PERMISSION_REQUEST_ENABLE_BLUETOOTH:
                if (resCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Log.d("YorickMessenger.onActivityResult()", "Bluetooth is not enabled");
                }
        }
    }

    private void connectToDevice(Intent data) {
        String deviceAddress = data.getExtras()
                .getString(ScanListActivity.EXTRA_DEVICE_ADDRESS);
        Toast.makeText(getActivity(), deviceAddress, Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), deviceAddress, Toast.LENGTH_LONG).show();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        chatService.connect(device);
    }

    private void makeDiscovarable() {
        if (mBluetoothAdapter.getScanMode() !=
                                mBluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 500);
            startActivity(discoverable);
        }
    }

    private void saveDeviceAddress(Intent data) {
        globalDeviceAddress = data.getExtras()
                .getString(ScanListActivity.EXTRA_DEVICE_ADDRESS);
    }

    private String loadDeviceAddress() {
        return globalDeviceAddress;
    }


}