package com.kernelpanic.yorickmessenger.activity.fragments;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.activity.MainAppActivity;
import com.kernelpanic.yorickmessenger.activity.ScanListActivity;
import com.kernelpanic.yorickmessenger.adapters.ChatRecyclerAdapter;
import com.kernelpanic.yorickmessenger.database.ChatMessage;
import com.kernelpanic.yorickmessenger.database.SQLiteDbHelper;
import com.kernelpanic.yorickmessenger.service.BluetoothChatService;
import com.kernelpanic.yorickmessenger.util.Constants;
import com.kernelpanic.yorickmessenger.util.FileWizardTestImplementation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.app.NotificationCompat.CATEGORY_MESSAGE;
import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class ChatFragment extends Fragment {


    protected final int PERMISSION_REQUEST_ENABLE_BLUETOOTH = 2;
    protected final int PERMISSION_REQUEST_CONNECT_DEVICE_SECURE = 3;
    protected final int PERMISSION_REQUEST_CONNECT_DEVICE_INSECURE = 4;
    private final String TAG = "Y.Messenger-Logs";
    private final int NOTIFY_ID = 1;
    private final String CHANNEL_ID = "YorickMessenger_Notify_Channel";
    private final int FILE_SELECT_CODE = 1;
    private final String FILE_BROWSER_CACHE_DIR = "YorickCache";
    private final boolean isImageWrite = false;
    private String sentUsername = null;
    private String receivedUsername = null;
    private String databaseUsername;
    private StringBuffer outStringBuffer;
    private String connectedDeviceBluetoothName = null;
    private String connectedDeviceBluetoothAddress = "";
    private SQLiteDbHelper dbHelper;
    private NotificationManager notificationManager;
    private ArrayList<com.kernelpanic.yorickmessenger.util.Message> messageList;
    private boolean isOnConnectExchangeDone = false;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService chatService = null;
    private ChatRecyclerAdapter chatListArrayAdapter;
    private EditText inputField;
    private final TextView.OnEditorActionListener writeListener
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
    private ImageButton btnSend;
    private ImageButton btnAttach;
    private Toolbar toolbar;
    private RecyclerView chatView;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            try {
                                btnAttach.setEnabled(false);
                                btnSend.setEnabled(false);
                                inputField.setEnabled(false);
                                Snackbar.make(getView(), "Wait until we done with transferring the user data", Snackbar.LENGTH_SHORT).show();
                                toolbar = getActivity().findViewById(R.id.appBar);
                                toolbar.setSubtitle(getString(R.string.app_mainActivity_connected, connectedDeviceBluetoothName));
                                onConnectDataExchange();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        isOnConnectExchangeDone = true;
                                        btnSend.setEnabled(true);
                                        btnAttach.setEnabled(true);
                                        inputField.setEnabled(true);
                                        Snackbar.make(getView(), "Done", Snackbar.LENGTH_SHORT).show();
                                        Toast.makeText(getActivity(), "globalDeviceAddress: " + connectedDeviceBluetoothAddress, Toast.LENGTH_SHORT).show();
                                        List<ChatMessage> chatList = dbHelper.querySelect(connectedDeviceBluetoothAddress);
                                        for (ChatMessage chatMessage : chatList)
                                            messageList.add(new com.kernelpanic.yorickmessenger.util.Message(chatMessage.getContent(), chatMessage.getCurrentTime(),
                                                    chatMessage.getType(), chatMessage.getUsername(), false));
                                        chatListArrayAdapter.notifyDataSetChanged();
                                        chatView.smoothScrollToPosition(messageList.size());
                                    }
                                }, 1500);
                            } catch (IllegalStateException ex) {
                                Log.d(TAG, ex.getMessage());
                            }
                            notificationManager = (NotificationManager) getActivity()
                                    .getApplicationContext()
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                            Intent intent = new Intent(getActivity().getApplicationContext(), MainAppActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            NotificationCompat.Builder notificationBuilder =
                                    new NotificationCompat.Builder(getActivity().getApplicationContext(), CHANNEL_ID)
                                            .setAutoCancel(false)
                                            .setWhen(System.currentTimeMillis())
                                            .setSmallIcon(R.drawable.ic_bluetooth_scan)
                                            .setContentIntent(pendingIntent)
                                            .setContentTitle(getString(R.string.app_name))
                                            .setContentText(getString(R.string.app_mainActivity_connected, connectedDeviceBluetoothName))
                                            .setPriority(PRIORITY_HIGH)
                                            .setCategory(CATEGORY_MESSAGE)
                                            .setOnlyAlertOnce(true);
                            createChannel(notificationManager);
                            notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
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
                            notificationManager = (NotificationManager) getActivity()
                                    .getApplicationContext()
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                            intent = new Intent(getActivity().getApplicationContext(), MainAppActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            pendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            Intent scanListIntent = new Intent(getActivity().getApplicationContext(), ScanListActivity.class);
                            PendingIntent pendingScanListIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0,
                                    scanListIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            notificationBuilder =
                                    new NotificationCompat.Builder(getActivity().getApplicationContext(), CHANNEL_ID)
                                            .setAutoCancel(false)
                                            .setWhen(System.currentTimeMillis())
                                            .setContentIntent(pendingIntent)
                                            .setSmallIcon(R.drawable.ic_bluetooth_scan)
                                            .setContentTitle(getString(R.string.app_name))
                                            .setContentText("Ready to connect")
                                            .setPriority(PRIORITY_HIGH)
                                            .setCategory(CATEGORY_MESSAGE)
                                            .setOnlyAlertOnce(true)
                                            .addAction(R.drawable.ic_bluetooth_scan, "SCAN", pendingScanListIntent);
                            createChannel(notificationManager);
                            notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
                            break;
                    }
                    break;
                case Constants.MESSAGE_READ:
                    Toast.makeText(getActivity(), "default read", Toast.LENGTH_SHORT).show();
                    String readMessage = (String) msg.obj;
                    if (isOnConnectExchangeDone) {
                        Long currentTimeR = System.currentTimeMillis();
                        messageList.add(new com.kernelpanic.yorickmessenger.util.Message(readMessage,
                                currentTimeR, Constants.MESSAGE_TYPE_RECEIVED, receivedUsername, false));
                        chatListArrayAdapter.notifyDataSetChanged();
                        chatView.smoothScrollToPosition(messageList.size());
                        Toast.makeText(getActivity(), receivedUsername + " received user", Toast.LENGTH_SHORT).show();
                        dbHelper.addMessageRecord(new ChatMessage(connectedDeviceBluetoothAddress, Constants.MESSAGE_TYPE_RECEIVED, readMessage,
                                receivedUsername, currentTimeR));
                        break;
                    } else {
                        receivedUsername = (String) msg.obj;
                        break;
                    }
                    // dbHeelper.addMess(device_id, sent/received, devicename, messgae);
                case Constants.MESSAGE_WRITE:
                    Toast.makeText(getActivity(), "default write", Toast.LENGTH_SHORT).show();
                    Long currentTimeMillisW = System.currentTimeMillis();
                    String writeMessage = (String) msg.obj;
                    if (isOnConnectExchangeDone) {
                        Toast.makeText(getActivity(), sentUsername + " sent user", Toast.LENGTH_SHORT).show();
                        Long currentTime = System.currentTimeMillis();
                        messageList.add(new com.kernelpanic.yorickmessenger.util.Message(writeMessage,
                                currentTimeMillisW, Constants.MESSAGE_TYPE_SENT, sentUsername, false));
                        chatListArrayAdapter.notifyDataSetChanged();
                        chatView.smoothScrollToPosition(messageList.size());
                        dbHelper.addMessageRecord(new ChatMessage(connectedDeviceBluetoothAddress, Constants.MESSAGE_TYPE_SENT, writeMessage,
                                sentUsername, currentTime));
                        break;
                    } else {
                        sentUsername = (String) msg.obj;
                        break;
                    }
                case Constants.MESSAGE_READ_FILE_NOW:
                    //Snackbar.make(getView(), (String) msg.obj, Snackbar.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_READ_FILE:
                    Toast.makeText(getActivity(), "file read", Toast.LENGTH_SHORT).show();
                    Long currentTime = System.currentTimeMillis();
                    messageList.add(new com.kernelpanic.yorickmessenger.util.Message(msg.obj + "",
                            currentTime, Constants.MESSAGE_TYPE_FILE_RECEIVED,
                            receivedUsername, true));
                    chatListArrayAdapter.notifyDataSetChanged();
                    chatView.smoothScrollToPosition(messageList.size());
                    break;
                case Constants.MESSAGE_WRITE_FILE:
                    Toast.makeText(getActivity(), "file write", Toast.LENGTH_SHORT).show();
                    currentTime = System.currentTimeMillis();
                    messageList.add(new com.kernelpanic.yorickmessenger.util.Message(
                            msg.obj + "", currentTime,
                            Constants.MESSAGE_TYPE_FILE_SENT,
                            sentUsername, true));
                    chatListArrayAdapter.notifyDataSetChanged();
                    chatView.smoothScrollToPosition(messageList.size());
                case Constants.MESSAGE_DEVICE_NAME:
                    connectedDeviceBluetoothName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), "Connected to: " + connectedDeviceBluetoothName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_DEVICE_ADDRESS:
                    connectedDeviceBluetoothAddress = msg.getData().getString(Constants.DEVICE_ADDRESS);
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != getActivity()) {
                        Toast.makeText(getActivity(), msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private void createChannel(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }

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
        dbHelper = new SQLiteDbHelper(getActivity().getApplicationContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        messageList = new ArrayList<com.kernelpanic.yorickmessenger.util.Message>();

        chatView = view.findViewById(R.id.messagesContainer);
        chatView.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

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
                postDelayedHandler.postDelayed(new Runnable() {
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
        btnAttach = view.findViewById(R.id.btnAttach);

        Long currentTime = System.currentTimeMillis();
        //messageList.add(new com.kernelpanic.yorickmessenger.util.Message("Test Received",
        //, Constants.MESSAGE_TYPE_RECEIVED, username, profilePic));

        //messageList.add(new com.kernelpanic.yorickmessenger.util.Message("Test Sent",
        //currentTime, Constants.MESSAGE_TYPE_SENT, username, profilePic));

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

        chatListArrayAdapter = new ChatRecyclerAdapter(messageList, getActivity());
        chatView.setAdapter(chatListArrayAdapter);

        inputField.setOnEditorActionListener(writeListener);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getView();
                if (null != view) {
                    //EditText textField = view.findViewById(R.id.chatFieldInput);
                    String message = inputField.getText().toString();
                    sendMessage(message);
                }
            }
        });

        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getView();
                if (null != view) {
                    //Implementation for <=10 versions of Android
                    Intent filePickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    filePickIntent.setType("*/*");
                    filePickIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    filePickIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    // May have some problems on Xiaomi
                    if (filePickIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(Intent.createChooser(filePickIntent, "Select a File to Upload"), FILE_SELECT_CODE);
                    }
                }
            }
        });

        chatService = new BluetoothChatService(getActivity(), handler);

        //chatService = new BluetoothChatServiceClass(getActivity(), handler);
        outStringBuffer = new StringBuffer(" ");
    }

    private void sendMessage(String msg) {
        if (chatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_SHORT).show(); //TODO: add string to strings.xml
            return;
        }

        if (msg.length() > 0) {
            byte[] sendData = msg.getBytes();
            chatService.sendData(msg);

            outStringBuffer.setLength(0);
            inputField.setText(outStringBuffer);
        }
    }


    @Override
    public void onActivityResult(int reqCode, int resCode, @Nullable Intent data) {
        Bitmap bitmap = null;
        switch (reqCode) {
            case PERMISSION_REQUEST_CONNECT_DEVICE_SECURE:
                if (resCode == Activity.RESULT_OK && chatService.getState() != BluetoothChatService.STATE_CONNECTED) {
                    connectToDevice(data);
                    onConnectDataExchange();
                }
                break;
            case PERMISSION_REQUEST_CONNECT_DEVICE_INSECURE:
                if (resCode == Activity.RESULT_OK && chatService.getState() != BluetoothChatService.STATE_CONNECTED) {
                    connectToDevice(data);
                    onConnectDataExchange();
                }
            case PERMISSION_REQUEST_ENABLE_BLUETOOTH:
                if (resCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Log.d("YMessenger.Result()", "Bluetooth is not enabled");
                }
            case FILE_SELECT_CODE:
                if (resCode == Activity.RESULT_OK && data != null
                        && chatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                    Uri uri = data.getData();
                    Context androidContext = getActivity();
                    final String filePath = FileWizardTestImplementation.getInstance(androidContext).getChooseFileResultPath(uri);
                    if (filePath != null) {
                        chatService.sendFile(filePath);
                    }
                }
        }
    }


    private String writeFileContent(Uri uri) throws IOException {
        InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
        if (inputStream != null) {
            final File yorickCacheDir = new File(getContext().getFilesDir(), FILE_BROWSER_CACHE_DIR);
            Log.d(TAG, yorickCacheDir.getAbsolutePath());
            boolean isCacheDirExists = yorickCacheDir.exists();
            if (!isCacheDirExists) {
                isCacheDirExists = yorickCacheDir.mkdirs();
            }
            if (isCacheDirExists) {
                String filePath = yorickCacheDir.getAbsolutePath() + "/" + getFileName(uri);
                OutputStream outputStream = new FileOutputStream(filePath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                return filePath;
            }
            inputStream.close();
        }
        return null;
    }

    private String getFileName(Uri uri) {
        String displayName = null;
        try (Cursor cursor = getContext().getContentResolver()
                .query(uri, null, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        return displayName;
    }

    private void connectToDevice(Intent data) {
        String deviceAddress = data.getExtras()
                .getString(ScanListActivity.EXTRA_DEVICE_ADDRESS);
        Toast.makeText(getActivity(), deviceAddress, Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), deviceAddress, Toast.LENGTH_LONG).show();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        chatService.connect(device);
    }

    private void onConnectDataExchange() {
        databaseUsername = dbHelper.getUserInfo();

        byte[] bytes = databaseUsername.getBytes();

        chatService.sendData(databaseUsername);
    }

}