package com.kernelpanic.yorickmessenger_gitclone.util;

public class Device {
    private String deviceName;
    private String deviceAdress;

    public Device(String deviceName, String deviceAdress) {
        this.deviceName = deviceName;
        this.deviceAdress = deviceAdress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAdress() {
        return deviceAdress;
    }

    public void setDeviceAdress(String deviceAdress) {
        this.deviceAdress = deviceAdress;
    }
}
