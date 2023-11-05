package com.michaelians.bluteeth.Singleton;

import android.bluetooth.BluetoothSocket;

public class BluetoothSocketHolder {
    private static BluetoothSocket socket;
    static boolean isClient = false;

    public static BluetoothSocket getSocket() {
        return socket;
    }

    public static boolean isClient() {
        return isClient;
    }

    public static void setSocket(BluetoothSocket newSocket) {
        socket = newSocket;
    }

    public static void setClient(boolean client) {
        isClient = client;
    }
}
