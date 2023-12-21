package com.eldorado.unishare.singleton;

import com.eldorado.unishare.feature.SendReceive;

public class ReceiverInstanceHolder {
    private static SendReceive instance;

    public static SendReceive getInstance() {
        return instance;
    }

    public static void setInstance(SendReceive newInstance) {
        instance = newInstance;
    }
}