    package com.kernelpanic.yorickmessenger.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.kernelpanic.yorickmessenger.activity.MainAppActivity;
import com.kernelpanic.yorickmessenger.activity.fragments.ChatFragment;
import com.kernelpanic.yorickmessenger.util.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class BluetoothChatService {

    /**
     * BluetoothChatService class made to describe the overall logic of
     * Bluetooth connections interaction
     *
     * Here we have the ConnectThread, ManageThread, ServerThread
     *
     * One of the main value here is UUID - this value is necessary to work
     * without that we could not start the BluetoothSocket
     */

    private static final String app_name = "YorickMessenger";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private static final String TAG = "Y.Messenger-Logs";


    private final BluetoothAdapter bluetoothAdapter;
    private final Handler handler;
    // Thread for accepting incoming connections
    // Thread для приймання зв'язків, що надходять до пристрою
    private ServerThread serverThread;
    /*
    Thread for connecting to remote devices
    Thread для з'єднання з пристроїм
     */
    private ConnectThread connectThread;
    /*
    Thread that allows to manage with read and write methods
    Thread що дозволюэ виконувати операції відправки та отримання
     */
    private ManageThread manageThread;
    private int mState;
    private ContentResolver contentResolver;

    private Context mAndroidContext;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;


    private static String FILE_PATH;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Just a constructor
     * Конструктор
     * @param context - androidContext
     * @param handler - object of the Handler class that defined in {@link ChatFragment}
     *                  об'єкт класу Handler, що описан у фрагменті чату
     */
    public BluetoothChatService(Context context, Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mAndroidContext = context;
        this.handler = handler;
        contentResolver = context.getContentResolver();
        FILE_PATH = context.getExternalFilesDir(null).getAbsolutePath() + "/YorickCache/";
    }



    /**
     * This method created to change the state. Changing the state was made to listen to the
     * action that we performing right now. E.g: LISTEN, CONNECTING
     * Ций метод створенний для зміни стану. Стан змінюється для того, щоб слідкуватися за тим,
     * що ми робимо
     * @param state - value to what we change current state (mState); зміна, на яку ми змінемо стан
     */
    private synchronized void setState(int state) {
        Log.i(TAG, "The setState() method called, state " + mState + " is going to -> " + state);
        mState = state;
        handler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * No need to describe
     * @return - state
     */
    public synchronized int getState() { return mState; }

    /**
     * Method that starts the ServerThread (whom we need to perform bluetooth connections)
     * Also we need to cancel all previous connections and threads if they exist
     * Метод, що запускає ServerThread, який нам потрібен для підключення
     * Також нам необхідно відключити вже існуючі Thread
     */
    public synchronized void start() {
        Log.d(TAG, "Initiating the start procedure");
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (manageThread != null) {
            manageThread.cancel();
            manageThread = null;
        }

        setState(STATE_LISTEN);
        if (serverThread == null) {
            serverThread = new ServerThread();
            serverThread.start();
        }
    }

    /**
     * Method that perform connect to another remote bluetooth device
     * Метод, який виконує з'єднання до іншого пристрою
     * @param device - object of BluetoothDevice class, what we get from the {@link ChatFragment}
     */
    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "Performing the attempt to connect to the remote bluetooth device, MAC: "
                + device.getAddress() + " name: " + device.getName());

        if (mState == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Method that starts the ManageThread
     * Метод, що запускає ManageThread
     * @param socket - object of the BluetoothSocket class; об'єкт класу BluetoothSocket
     * @param device - object of the BluetoothDevice class; об'єкт класу BluetoothDevice
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.i(TAG, "Connected! BluetoothDevice: " + device);
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (manageThread != null) {
            manageThread.cancel();
            manageThread = null;
        }

        if (serverThread != null) {
            serverThread.cancel();
            serverThread = null;
        }

        manageThread = new ManageThread(socket);
        manageThread.start();

        Message message = handler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Message messageAddress = handler.obtainMessage(Constants.MESSAGE_DEVICE_ADDRESS);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        Bundle bundleAddress = new Bundle();
        bundleAddress.putString(Constants.DEVICE_ADDRESS, device.getAddress());
        message.setData(bundle);
        messageAddress.setData(bundleAddress);

        handler.sendMessage(message);
        handler.sendMessage(messageAddress);

        setState(STATE_CONNECTED);
    }

    /**
     * Method that stops all threads
     * Метод, що припиняє роботу сервісу
     */
    public synchronized void stop() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (manageThread != null) {
            manageThread.cancel();
            manageThread = null;
        }

        if (serverThread != null) {
            serverThread.cancel();
            serverThread = null;
        }

        setState(STATE_NONE);
    }

    public void sendData(String message) {
        ManageThread thread;
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            thread = manageThread;
        }
        thread.write(message);
    }

    public void sendFile(String filePath) {
        ManageThread thread;
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            thread = manageThread;
        }
        thread.writeFile(filePath);
    }

    /**
     * Connection failed handler, sends to the user an error during the connection
     * Метод, що повідомляє користувача про помилку під час підключення до пристрою
     */
    private void connectionFailed() {
        Message message = handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Unable to connect device");
        message.setData(bundle);
        handler.sendMessage(message);

        BluetoothChatService.this.start();
    }

    /**
     * Connection lost handler, sends to the user an error during the connection
     * Метод, що повідомляє користувача про втрату зв'язку з пристроєм
     */
    private void connectionLost() {
        Message msg = handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Device connection was lost");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothChatService.this.start();
    }


    /**
     * ServerThread - class that describe the logic of accepting the incoming connections
     * ServerThread - клас, який описує логіку з'єднання одного пристрою з іншим
     */
    private class ServerThread extends Thread {

        private final BluetoothServerSocket serverSocket;

        public ServerThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(app_name, MY_UUID);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            serverSocket = tmp;
        }

        public void run() {
            setName("ServerThread");
            BluetoothSocket socket;
            while (mState != STATE_CONNECTED) {

                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

                if (socket != null) {
                    synchronized (BluetoothChatService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    break;
                                }
                        }
                    }
                }

            }
        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * ConnectThread - class that describe the logic of connecting to another device
     * Mean, here we are <b>trying</b> to connect to another device, while the
     * {@param ServerThread} trying to accept that connection
     * ConnectThread - клас, який описує логіку з'єднання одного пристрою з іншим пристроєм
     * Тобто, у цьому класі ми виконуємо спробу з'єднатися з пристроєм, коли ServerThread намається
     * прийняти це з'єднання
     */
    private class ConnectThread extends Thread {

        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
            BluetoothSocket tmpSocket = null;

            try {
                //tmpSocket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{ int.class }).invoke(device, 1);
                tmpSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket = tmpSocket;
        }

        public void run() {
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            bluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                socket.connect();
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatService.this) {
                connectThread = null;
            }

            // Start the connected thread
            connected(socket, device);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * ManageThread - class that allows to read and write data
     * ManageThread - клас, який дозволяє зчитувати та відправляти дані
     */
    private class ManageThread extends Thread {

        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final DataInputStream dataInputStream;
        private final DataOutputStream dataOutputStream;
        private final OutputStream outputStream;

        public ManageThread(BluetoothSocket socket) {
            this.socket = socket;
            InputStream intputTMP = null;
            OutputStream outputTMP = null;

            try {
                intputTMP = socket.getInputStream();
                outputTMP = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = intputTMP;
            dataInputStream = new DataInputStream(inputStream);
            outputStream = outputTMP;
            dataOutputStream = new DataOutputStream(outputStream);
        }



        public void run() {

            while (true) {
                try {
                    switch (dataInputStream.readInt()) {
                        case Constants.TYPE_WRITE_DEFAULT:
                            String message = dataInputStream.readUTF();
                            sendMessageToUI(Constants.MESSAGE_READ, message);
                            break;
                        case Constants.TYPE_WRITE_FILE:
                            File destinationDirectory = new File(mAndroidContext.getExternalFilesDir(null).getAbsolutePath() + "/YorickCache/");
                            if (!destinationDirectory.exists())
                                destinationDirectory.mkdirs();
                            String filename = dataInputStream.readUTF();
                            long fileSize = dataInputStream.readLong();
                            sendMessageToUI(Constants.MESSAGE_READ_FILE_NOW, fileSize);
                            long size = 0;
                            int r;
                            byte[] bytes = new byte[4 * 1024];
                            FileOutputStream out = new FileOutputStream(FILE_PATH + filename);
                            while ((r = inputStream.read(bytes)) != -1) {
                                out.write(bytes, 0, r);
                                size += r;
                                if (size >= fileSize) {
                                    break;
                                }
                            }
                            if (Build.VERSION.SDK_INT >= 29) {
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.DISPLAY_NAME, filename + ".jpg");
                                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Yorick Pictures");
                                Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
                                out = (FileOutputStream) contentResolver.openOutputStream(imageUri);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            }
                            sendMessageToUI(Constants.MESSAGE_READ_FILE, filename);
                            break;
                    }
                } catch (IOException ioEX) {
                    ioEX.printStackTrace();
                    Log.d(TAG, "Receiving data error, exception: " + ioEX.getMessage());
                    BluetoothChatService.this.start();
                    break;
                }
            }
        }

        public void write(final String message) {
            executor.execute(new Runnable() {
               public void run() {
                   try {
                       dataOutputStream.writeInt(Constants.TYPE_WRITE_DEFAULT);
                       dataOutputStream.writeUTF(message);
                   } catch (Throwable ex) {
                       Log.e(TAG, "We have caught an exception: " + ex);
                   }
                   sendMessageToUI(Constants.MESSAGE_WRITE, message);
               }
            });
        }
        
        public void writeFile(final String filePath) {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        sendMessageToUI(Constants.MESSAGE_WRITE_FILE_NOW, "Sendind file + " + filePath);
                        FileInputStream fileInputStream = new FileInputStream(filePath);
                        File file = new File(filePath);
                        dataOutputStream.writeInt(Constants.TYPE_WRITE_FILE);
                        dataOutputStream.writeUTF(file.getName());
                        dataOutputStream.writeLong(file.length());
                        int r;
                        byte[] bytes = new byte[4 * 1024];
                        while ((r = fileInputStream.read(bytes)) != -1) {
                            dataOutputStream.write(bytes, 0, r);
                        }
                        sendMessageToUI(Constants.MESSAGE_WRITE_FILE, file.getName());
                    } catch (Throwable ex) {
                        Log.e(TAG, "We have caught the exception: ", ex);
                    }
                }
            });
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception", e);
            }
        }

    }

    private void sendMessageToUI(int what, Object object) {
        Message message = handler.obtainMessage();
        message.what = what;
        message.obj = object;
        handler.sendMessage(message);
    }
}