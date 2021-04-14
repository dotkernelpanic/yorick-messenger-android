package com.kernelpanic.yorickmessenger.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public interface IBluetoothChatService {
    void setState(int state);
    int getState();
    void start();
    void connect(BluetoothDevice device);
    void connected(BluetoothSocket socket, BluetoothDevice device);
    void stop();
    void write(byte[] out);
    void connectionFailedErrorHandler();
    void connectionLostErrorHandler();
}
