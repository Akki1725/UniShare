package com.eldorado.unishare.feature;

import static com.eldorado.unishare.activity.MainActivity.BLUE_UUID;
import static com.eldorado.unishare.activity.MainActivity.CONNECTION_FAILED;
import static com.eldorado.unishare.activity.MainActivity.STATE_CONNECTED;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import androidx.core.app.ActivityCompat;

import com.eldorado.unishare.singleton.BluetoothSocketHolder;

import java.io.IOException;

import io.reactivex.rxjava3.internal.operators.flowable.FlowableDistinctUntilChanged;

public class ClientClass extends Thread {

    Context context;
    BluetoothDevice device;
    Handler handler;
    BluetoothSocket socket;
    BluetoothAdapter bluetoothAdapter;
    Function function;
    int devicePosition;

    public enum Function {
        CONNECT,
        CALL
    }

    public ClientClass(BluetoothDevice device, Context context, Handler handler, int devicePosition, BluetoothAdapter bluetoothAdapter) {

        this.context = context;
        this.device = device;
        this.handler = handler;
        this.devicePosition = devicePosition;
        this.bluetoothAdapter = bluetoothAdapter;
        this.function = Function.CONNECT;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        }
        try {
            socket = device.createRfcommSocketToServiceRecord(BLUE_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFunctionToExecute(Function function) {
        this.function = function;
    }

    public void run() {
        switch (function) {
            case CONNECT:
                connect();
                break;
            case CALL:
                call();
                break;
            default:
                break;
        }
    }

    void connect() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
        bluetoothAdapter.cancelDiscovery();

        try {
            socket.connect();
            Message message = Message.obtain();
            message.what = STATE_CONNECTED;
            handler.sendMessage(message);
            BluetoothSocketHolder.setSocket(socket);
            Intent connectedIntent = new Intent("status.connected");
            connectedIntent.putExtra("devicePosition", devicePosition);
            context.sendBroadcast(connectedIntent);
        } catch (IOException e) {
            Message message = Message.obtain();
            message.what = CONNECTION_FAILED;
            handler.sendMessage(message);
            e.printStackTrace();
        }
    }

    void call() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
        bluetoothAdapter.cancelDiscovery();

        try {
            socket.connect();
            Message message = Message.obtain();
            message.what = STATE_CONNECTED;
            handler.sendMessage(message);
            BluetoothSocketHolder.setSocket(socket);
            BluetoothSocketHolder.setClient(true);
            Intent connectedIntent = new Intent("call.send");
            connectedIntent.putExtra("devicePosition", devicePosition);
            context.sendBroadcast(connectedIntent);
        } catch (IOException e) {
            Message message = Message.obtain();
            message.what = CONNECTION_FAILED;
            handler.sendMessage(message);
            e.printStackTrace();
        }
    }
}
