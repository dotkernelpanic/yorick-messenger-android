package com.kernelpanic.yorickmessenger.util;

public interface Requests {
    /***
     * {@link com.kernelpanic.yorickmessenger.activity.fragments.ChatFragment}
     * Permission requests for ChatFragment
     */
    int REQUEST_PERMISSION_ENABLE_BLUETOOTH = 0x01;
    int REQUEST_PERMISSION_CONNECT_DEVICE_SECURE = 0x02;
    int REQUEST_PERMISSIONCONNECT_DEVICE_INSECURE = 0x03;
    // Request for file picker intent
    int REQUEST_SELECT_FILE = 0x04;

    /***
     * {@link com.kernelpanic.yorickmessenger.activity.ScanListActivity}
     * Requests for ScanListActivity
     */
    int REQUEST_PERMISSIONS = 0x01;
}
