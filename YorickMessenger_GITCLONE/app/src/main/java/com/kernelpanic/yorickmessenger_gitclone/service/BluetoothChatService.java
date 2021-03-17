package com.kernelpanic.yorickmessenger_gitclone.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kernelpanic.yorickmessenger_gitclone.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothChatService implements IBluetoothChatService {

    private static final String TAG = "BluetoothChatService";

    //Names for bluetooth sockets
    private static final String BT_NAME_SECURE = "BluetoothChatSecure";
    private static final String BT_NAME_INSECURE = "BluetoothChatInsecure";

    //Generate my UUID on https://www.uuidgenerator.net/
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("ce23a97a-16ea-4726-ba00-876b148ca919");
    private  static final  UUID MY_UUID_INSECURE =
            UUID.fromString("cbdb2e87-2d42-42f8-a3ef-d1509a3b6fd3");

    private final BluetoothAdapter mBtAdapter;
    private final Handler mHandler;
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    public static final int STATE_ID_NONE = 0; // State that indicates we are doing nothing
    public static final int STATE_ID_LISTEN = 1; // State that indicates we are listening for incoming connections
    public static final int STATE_ID_CONNECTING = 2; // State that indicates we are trying to connect
    public static final int STATE_ID_CONNECTED = 3; // State that indicated we are successfully connected to an another remote device

    /**
     * Here we are preparing a Constructor for a new BluetoothChat session,
     * Params:
     * @param context - UI Activity context
     * @param handler - A Handler that sending messages back to context
     */
    public BluetoothChatService(Context context, Handler handler) {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_ID_NONE; // We are doing nothing
        mHandler = handler;
    }

    /**
     * Set the current state
     * @param state  - An integer param that indicates current connection sate
     */
    @Override
    public synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " --- " + state);
        mState = state;

        mHandler.obtainMessage(Constants.MESSAGE_STATE_ID_CHANGE, state, -1).sendToTarget();
    }

    // Returns the current state
    @Override
    public int getState() { return mState; }

    /**
     * Function that starts the chat service.
     */
    @Override
    public void start() {
        Log.d(TAG, "start");

        // Cancel all connect threads (attempts to connect)
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel all connected threads (currently running connections)
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_ID_LISTEN);

        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(true);
            mSecureAcceptThread.start();
        }

        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }
    }

    /**
     * Function that starts ConnectThread, initiating the connection to an another remote device
     * @param device -- Remote BluetoothDevice to connect
     * @param isSecure -- Indicates the socket security type (true - secure, false - insecure)
     */
    @Override
    public void connect(BluetoothDevice device, boolean isSecure) {
        Log.d(TAG, "attempt to connect to remote device: " + device);

        if (mState == STATE_ID_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(device, isSecure);
        mConnectThread.start();
        setState(STATE_ID_CONNECTING);
    }

    /**
     * Function that starts the ConnectedThread, managing bluetooth connection
     * @param socket - Socket on which has been connected
     * @param device - Remote Bluetooth device
     */
    @Override
    public void connected(BluetoothSocket socket, BluetoothDevice device, final String SOCKET_TYPE) {
        Log.d(TAG, "successfully connected, socket type: " + SOCKET_TYPE);

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        mConnectedThread = new ConnectedThread(socket, SOCKET_TYPE);
        mConnectedThread.start();

        Message message = mHandler.obtainMessage(Constants.MESSAGE_ID_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        message.setData(bundle);
        mHandler.sendMessage(message);

        setState(STATE_ID_CONNECTED);
    }

    // Not need to describe i think
    @Override
    public void stop() {
        Log.d(TAG, "initiate stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        setState(STATE_ID_NONE);
    }

    /**
     * Write to a ConnectedThread
     * @param out - Array of bytes to write
     * @see ConnectedThread#write(byte[])
     */
    @Override
    public void write(byte[] out) {
        ConnectedThread temp;

        synchronized (this) {
            if (mState != STATE_ID_CONNECTED) return;
            temp = mConnectedThread;
        }

        temp.write(out);
    }

    // Indicates that current connection is failed
    @Override
    public void connectionFailedErrorHandler() {
        Message message = mHandler.obtainMessage(Constants.MESSAGE_ID_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "We are unnable to connect to a remote device");
        message.setData(bundle);
        mHandler.sendMessage(message);

        BluetoothChatService.this.start();
    }

    // Indicates that connection is lost
    @Override
    public void connectionLostErrorHandler() {
        Message message = mHandler.obtainMessage(Constants.MESSAGE_ID_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "We are unnable to connect to a remote device");
        message.setData(bundle);
        mHandler.sendMessage(message);

        BluetoothChatService.this.start();
    }

    private class AcceptThread extends Thread {

        private final BluetoothServerSocket btServerSocket;
        private String mSocketType;

        public AcceptThread(boolean isSecure) {
            BluetoothServerSocket temp = null;
            mSocketType = isSecure ? "Secure" : "Insecure";

            try {
                if (isSecure) {
                    temp = mBtAdapter.listenUsingRfcommWithServiceRecord(BT_NAME_SECURE, MY_UUID_SECURE);
                } else {
                    temp = mBtAdapter.listenUsingInsecureRfcommWithServiceRecord(BT_NAME_INSECURE, MY_UUID_INSECURE);
                }
            } catch (IOException ex) {
                Log.e(TAG, " Socket Type: " + mSocketType + " listen() failed", ex);
            }
            btServerSocket = temp;
        }

        public void run() {
            Log.d(TAG, "Socket Type: " + mSocketType + " BEGIN *AcceptThread* " + this);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            while (mState != STATE_ID_CONNECTED) {
                try {
                    socket = btServerSocket.accept();
                } catch (IOException ex) {
                    Log.e(TAG, "Socket Type: " + mSocketType + " accept() failed", ex);
                    break;
                }

                if (socket != null) {
                    synchronized (BluetoothChatService.this) {
                        switch (mState) {
                            case STATE_ID_LISTEN:
                            case STATE_ID_CONNECTING:
                                connected(socket, socket.getRemoteDevice(), mSocketType);
                                break;
                            case STATE_ID_NONE:
                            case STATE_ID_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException ex) {
                                    Log.e(TAG, "We are unable to close unwanted socket", ex);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END *AcceptThread*, sockettype: " + mSocketType);
        }

        public void cancel() {
            Log.d(TAG, "Socket Type: " + mSocketType + " cancel " + this);
            try {
                btServerSocket.close();
            } catch (IOException ex) {
                Log.e(TAG, "Socket Type" + mSocketType + " close() failed :C", ex);
            }
        }
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket btSocket;
        private final BluetoothDevice btDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean isSecure) {
            btDevice = device;
            BluetoothSocket temp = null;
            mSocketType = isSecure ? "Secure" : "Insecure";

            try {
                if (isSecure) {
                    temp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                } else { temp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                }
            } catch (IOException ex) {
                Log.e(TAG, "Socket Type: " +  mSocketType + " create() failed ", ex);
            }
            btSocket = temp;
        }

        public void run() {
            Log.i(TAG, "BEGIN *ConnectThread* SocketType: " + mSocketType);
            setName("ConnectThread" + mSocketType);

            mBtAdapter.cancelDiscovery();

            try {
                btSocket.connect();
            } catch (IOException ex) {
                try {
                    btSocket.close();
                } catch (IOException exX) {
                    Log.e(TAG, "we are unable to close() " + mSocketType + " socket during conection failure", exX);
                }
                connectionFailedErrorHandler();
                return;
            }

            synchronized (BluetoothChatService.this) {
                mConnectedThread = null;
            }

            connected(btSocket, btDevice, mSocketType);
        }

        public void cancel() {
            try {
                btSocket.close();
            } catch (IOException ex) {
                Log.e(TAG, " close() connect " + mSocketType + " socket failed", ex);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket btSocket;
        private final InputStream btInputStream;
        private final OutputStream btOutputStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create *ConnectedThread*, socketType: " + socketType);
            btSocket = socket;
            InputStream tmpIN = null;
            OutputStream tmoOUT = null;

            try {
                tmpIN = socket.getInputStream();
                tmoOUT = socket.getOutputStream();
            } catch (IOException ex) {
                Log.e(TAG, " temp sockets not created", ex);
            }

            btInputStream = tmpIN;
            btOutputStream = tmoOUT;
        }

        public void run() {
            Log.i(TAG, " BEGIN *ConnectThread*");
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = btInputStream.read(buffer);

                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException ex) {
                    Log.e(TAG, "disconnected", ex);
                    connectionLostErrorHandler();
                    BluetoothChatService.this.start();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                btOutputStream.write(buffer);

                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException ex) {
                Log.e(TAG, " We are catched the exception during write bytes", ex);
            }
        }

        public void cancel() {
            try {
                btSocket.close();
            } catch (IOException ex) {
                Log.e(TAG, " close() of connect socket failed", ex);
            }
        }
    }

}
