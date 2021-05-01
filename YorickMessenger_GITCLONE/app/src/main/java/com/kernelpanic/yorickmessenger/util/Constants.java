package com.kernelpanic.yorickmessenger.util;

public interface Constants {

    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_READ_FILE_NOW = 21;
    int MESSAGE_READ_FILE = 22;
    int MESSAGE_WRITE = 3;
    int MESSAGE_WRITE_FILE_NOW = 31;
    int MESSAGE_WRITE_FILE = 32;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_DEVICE_ADDRESS = 6;
    int MESSAGE_TOAST = 5;

    int TYPE_WRITE_DEFAULT = 0;
    int TYPE_WRITE_FILE = 1;

    //Message types: sent, received, image_sent, image_received
    int MESSAGE_TYPE_SENT = 0;
    int MESSAGE_TYPE_RECEIVED = 1;
    int MESSAGE_TYPE_FILE_SENT = 2;
    int MESSAGE_TYPE_FILE_RECEIVED = 3;

    String APP_LOG_TAG = "Y.Messenger-Logs";

    String DEVICE_NAME = "device_name";
    String DEVICE_ADDRESS = "device_address";
    String TOAST = "toast";

}
