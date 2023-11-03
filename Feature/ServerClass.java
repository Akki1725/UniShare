package com.michaelians.bluteeth.Feature;

import static com.michaelians.bluteeth.Activity.MainActivity.APP_NAME;
import static com.michaelians.bluteeth.Activity.MainActivity.BLUE_UUID;
import static com.michaelians.bluteeth.Activity.MainActivity.CONNECTION_FAILED;
import static com.michaelians.bluteeth.Activity.MainActivity.STATE_CONNECTED;
import static com.michaelians.bluteeth.Activity.MainActivity.STATE_CONNECTING;
import static com.michaelians.bluteeth.Activity.MainActivity.STATE_LISTENING;

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

import com.michaelians.bluteeth.Activity.ChatActivity;
import com.michaelians.bluteeth.Activity.VoiceCallActivity;
import com.michaelians.bluteeth.Model.Device;
import com.michaelians.bluteeth.Singleton.BluetoothSocketHolder;

import java.io.IOException;

public class ServerClass extends Thread {
    Context context;
    Handler handler;
    Function selectedFunction;
    private BluetoothServerSocket serverSocket;

    public enum Function {
        LISTEN,
        LISTEN_FOR_CALLS
    }

    public ServerClass(Context context, Handler handler, BluetoothAdapter bluetoothAdapter) {

        this.context = context;
        this.handler = handler;
        this.selectedFunction = Function.LISTEN;

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
                connectedDevice.setName(remoteDevice.getName());
                connectedDevice.setStatus("Connected!");

                if (socket != null) {
                    Message connectedMessage = Message.obtain();
                    connectedMessage.what = STATE_CONNECTED;
                    handler.sendMessage(connectedMessage);
                    BluetoothSocketHolder.setSocket(socket);
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("name", connectedDevice.getName());
                    intent.putExtra("blueId", connectedDevice.getBlueId());
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

    void listenForCalls() {
        BluetoothSocket socket;
        while (true)
            try {
                Message message = Message.obtain();
                message.what = STATE_CONNECTING;
                handler.sendMessage(message);

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
                socket = serverSocket.accept();
                BluetoothDevice remoteDevice = socket.getRemoteDevice();
                Device connectedDevice = new Device(remoteDevice.getAddress());
                connectedDevice.setName(remoteDevice.getName());
                connectedDevice.setStatus("Connected!");

                if (socket != null) {
                    Message connectedMessage = Message.obtain();
                    connectedMessage.what = STATE_CONNECTED;
                    handler.sendMessage(connectedMessage);
                    Intent intent = new Intent(context, VoiceCallActivity.class);
                    intent.putExtra("deviceName", connectedDevice.getName());
                    intent.putExtra("blueId", connectedDevice.getBlueId());
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
