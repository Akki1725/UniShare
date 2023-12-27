package com.eldorado.unishare.feature;

import static com.eldorado.unishare.activity.MainActivity.APP_NAME;
import static com.eldorado.unishare.activity.MainActivity.BLUE_UUID;
import static com.eldorado.unishare.activity.MainActivity.CONNECTION_FAILED;
import static com.eldorado.unishare.activity.MainActivity.STATE_CONNECTED;
import static com.eldorado.unishare.activity.MainActivity.STATE_CONNECTING;
import static com.eldorado.unishare.activity.MainActivity.STATE_LISTENING;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import androidx.core.app.ActivityCompat;

import com.eldorado.unishare.activity.ChatActivity;
import com.eldorado.unishare.activity.VoiceCallActivity;
import com.eldorado.unishare.model.Device;
import com.eldorado.unishare.singleton.BluetoothSocketHolder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ServerClass extends Thread {
    Context context;
    Handler handler;
    Function selectedFunction;
    BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket serverSocket;

    public enum Function {
        LISTEN,
        LISTEN_FOR_CALLS
    }

    public ServerClass(Context context, Handler handler, BluetoothAdapter adapter) {

        this.context = context;
        this.handler = handler;
        this.selectedFunction = Function.LISTEN;

        this.bluetoothAdapter = adapter;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        }
        try {
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, BLUE_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFunctionToExecute(Function function) {
        this.selectedFunction = function;
    }

    public void run() {
        switch (selectedFunction) {
            case LISTEN:
                listen();
                startPingPong();
                break;
            case LISTEN_FOR_CALLS:
                listenForCalls();
                break;
            default:
                break;
        }
    }

    void listen() {
        BluetoothSocket socket;
        while (true)
            try {
                Message message = Message.obtain();
                message.what = STATE_LISTENING;
                handler.sendMessage(message);

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
                socket = serverSocket.accept();
                BluetoothDevice remoteDevice = socket.getRemoteDevice();
                Device connectedDevice = new Device(remoteDevice.getAddress());
                connectedDevice.setDeviceName(remoteDevice.getName());
                connectedDevice.setStatus("Connected!");
                connectedDevice.updateName();

                if (socket != null) {
                    Message connectedMessage = Message.obtain();
                    connectedMessage.what = STATE_CONNECTED;
                    handler.sendMessage(connectedMessage);
                    BluetoothSocketHolder.setSocket(socket);
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra( "name", connectedDevice.getName());
                    intent.putExtra("blueId", connectedDevice.getBlueId());
                    intent.putExtra("profileImage", connectedDevice.getProfileImage());
                    intent.putExtra("connected", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();

                Message failedMessage = Message.obtain();
                failedMessage.what = CONNECTION_FAILED;
                handler.sendMessage(failedMessage);
            }
    }

    void startPingPong() {
        new Thread(() -> {
            try {
                while (true) {
                    BluetoothSocket clientSocket = serverSocket.accept();
                    respondToPing(clientSocket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    void respondToPing(BluetoothSocket clientSocket) {
        new Thread(() -> {
            try {
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                while (true) {
                    byte[] buffer = new byte[1024];
                    int bytes = inputStream.read(buffer);

                    if (bytes != -1) {
                        String receivedMessage = new String(buffer, 0, bytes);
                        if (receivedMessage.startsWith("PING")) {
                            String timestamp = receivedMessage.substring(4);
                            String pongMessage = "PONG" + timestamp;
                            byte[] pongBytes = pongMessage.getBytes();
                            outputStream.write(pongBytes);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    void listenForCalls() {
        BluetoothSocket socket;
        while (true)
            try {
                Message message = Message.obtain();
                message.what = STATE_CONNECTING;
                handler.sendMessage(message);

                ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT);
                socket = serverSocket.accept();
                BluetoothDevice remoteDevice = socket.getRemoteDevice();
                Device connectedDevice = new Device(remoteDevice.getAddress());
                connectedDevice.setDeviceName(remoteDevice.getName());
                connectedDevice.setStatus("Connected!");
                connectedDevice.updateName();

                if (socket != null) {
                    Message connectedMessage = Message.obtain();
                    connectedMessage.what = STATE_CONNECTED;
                    handler.sendMessage(connectedMessage);
                    Intent intent = new Intent(context, VoiceCallActivity.class);
                    intent.putExtra("deviceName", connectedDevice.getName());
                    intent.putExtra("blueId", connectedDevice.getBlueId());
                    intent.putExtra("profileImage", connectedDevice.getProfileImage());
                    intent.putExtra("city", connectedDevice.getCity());
                    intent.putExtra("isUnknown", false);
                    BluetoothSocketHolder.setSocket(socket);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();

                Message failedMessage = Message.obtain();
                failedMessage.what = CONNECTION_FAILED;
                handler.sendMessage(failedMessage);
            }
    }
}