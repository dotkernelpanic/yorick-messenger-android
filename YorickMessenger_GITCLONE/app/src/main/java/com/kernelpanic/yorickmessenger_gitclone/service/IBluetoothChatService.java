package com.kernelpanic.yorickmessenger_gitclone.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public interface IBluetoothChatService {
    void setState(int state);
    int getState();
    void start();
    void connect(BluetoothDevice device, boolean isSecure);
    void connected(BluetoothSocket socket, BluetoothDevice device, final String SOCKET_TYPE);
    void stop();
    void write(byte[] out);
    void connectionFailedErrorHandler();
    void connectionLostErrorHandler();
}
