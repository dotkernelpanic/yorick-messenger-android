package com.kernelpanic.yorickmessenger.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kernelpanic.yorickmessenger.R;
import com.kernelpanic.yorickmessenger.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothChatServiceClass {

    private static final UUID   MY_UUID_SERIAL_SERVICE_PORT = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final String TAG = Constants.APP_LOG_TAG;

    private final BluetoothAdapter    mBluetoothAdapter;
    private final Handler mHandler;
    private ConnectThread             mConnectThread;
    private ConnectedThread           mConnectedThread;
    private int                       mState;

    private boolean isInsecureConnectionsAllowed = false;

    private Context mAndroidContext;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public BluetoothChatServiceClass(Context context, Handler handler) {
        mHandler = handler;
        mAndroidContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // Getting the bluetooth adapter
        mState = STATE_NONE; // We're doing nothing
        isInsecureConnectionsAllowed = true;
    }

    private synchronized void setState(int state) {
        Log.d(TAG, "setState() called; state " + mState + " going to -> " + state);
        mState = state;

        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() { return mState; }

    public synchronized void start() {
        Log.d(TAG, "Starting");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectedThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_LISTEN);
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "performing attempt to connect to: " + device);

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) { mConnectThread.cancel(); mConnectThread = null; }
        }

        if (mConnectedThread != null) { mConnectedThread.cancel(); mConnectedThread = null; }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(TAG, "Connected! BluetoothDevice -> " + device + " Socket -> " + socket);

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        Message message = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        message.setData(bundle);
        mHandler.sendMessage(message);

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        Log.d(TAG, "Stop all threads called");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }

    public void write(byte[] out_buffer) {
        ConnectedThread connectedThread;
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            connectedThread = mConnectedThread;
        }

        connectedThread.write(out_buffer);
    }

    private void connectionFailedHandler() {
        setState(STATE_NONE);

        Message message = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, mAndroidContext.getString(R.string.app_bluetoothservice_unableToConnect));
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private void connectionLostHandler() {
        setState(STATE_NONE);

        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, mAndroidContext.getString(R.string.app_bluetoothservice_connectionLost));
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        public ConnectThread(BluetoothDevice device) {
            bluetoothDevice = device;

            BluetoothSocket tempSocket = null;

            try {
                if (isInsecureConnectionsAllowed) {
                    Method method;
                    method = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
                    tempSocket = (BluetoothSocket) method.invoke(device, 1);
                }
                else {
                    tempSocket = device.createRfcommSocketToServiceRecord(MY_UUID_SERIAL_SERVICE_PORT);
                }
            } catch (Exception e) {
                Log.e(TAG, "We have failed to create socket", e);
            }
            bluetoothSocket = tempSocket;
        }

        public void run() {
            Log.i(TAG, "Starting the ConnectThread");
            setName("ConnectThread");

            mBluetoothAdapter.cancelDiscovery();

            try {
                bluetoothSocket.connect();
            } catch (IOException ioEX) {
                connectionFailedHandler();
                try {
                    bluetoothSocket.close();
                } catch (IOException ioEX2) {
                    Log.e(TAG, "We are unable to close bluetooth socket due to connection failure", ioEX2);
                }
                return;
            }

            synchronized (BluetoothChatServiceClass.this) {
                mConnectThread = null;
            }

            connected(bluetoothSocket, bluetoothDevice);
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException ioEX) {
                Log.e(TAG, "Failure during the closing of connect socket", ioEX);
            }
        }

    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket   bluetoothSocket;
        private final InputStream       inputStream;
        private final OutputStream      outputStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "Creating the ConnectedThread");
            bluetoothSocket = socket;
            InputStream tempInputStream = null;
            OutputStream tempOutputStream = null;

            try {
                tempInputStream = socket.getInputStream();
                tempOutputStream = socket.getOutputStream();
            } catch (IOException ioEX) {
                Log.e(TAG, "Temp sockets have not been created", ioEX);
            }

            inputStream = tempInputStream;
            outputStream = tempOutputStream;
        }

        public void run() {
            Log.i(TAG, "Starting the ConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);

                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException ioEX) {
                    Log.e(TAG, "Disconnected", ioEX);
                    connectionLostHandler();
                    BluetoothChatServiceClass.this.start();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);

                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException ioEX) {
                Log.e(TAG, "We have catch the exception", ioEX);
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException ioEX) {
                Log.e(TAG, "We have catch the exception during the attempt to close connect socket", ioEX);
            }
        }
    }
}
