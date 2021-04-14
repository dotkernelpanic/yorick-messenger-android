package com.kernelpanic.yorickmessenger.util;

public class DeviceData {

    private String BluetoothDeviceName;
    private String BluetoothDeviceHWAdress;

    public DeviceData(String bluetoothDeviceName, String bluetoothDeviceHWAdress) {
        BluetoothDeviceName = bluetoothDeviceName;
        BluetoothDeviceHWAdress = bluetoothDeviceHWAdress;
    }

    public String getBluetoothDeviceName() {
        return BluetoothDeviceName;
    }

    public void setBluetoothDeviceName(String bluetoothDeviceName) {
        BluetoothDeviceName = bluetoothDeviceName;
    }

    public String getBluetoothDeviceHWAdress() {
        return BluetoothDeviceHWAdress;
    }

    public void setBluetoothDeviceHWAdress(String bluetoothDeviceHWAdress) {
        BluetoothDeviceHWAdress = bluetoothDeviceHWAdress;
    }
}
