package com.eldorado.unishare.singleton;

import android.annotation.SuppressLint;

import com.eldorado.unishare.feature.ClientClass;

@SuppressLint("StaticFieldLeak")
public class ClientClassHolder {
    private static ClientClass clientInstance;
    private static ClientClass clientReceiverInstance;

    public static ClientClass getInstance() {
        return clientInstance;
    }
    public static ClientClass getReceiverInstance() {
        return clientReceiverInstance;
    }
    public static void setClientInstance(ClientClass newInstance) {
        clientInstance = newInstance;
    }
    public static void setReceiverInstance(ClientClass newInstance) {
        clientReceiverInstance = newInstance;
    }
}
