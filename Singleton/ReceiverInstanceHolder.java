package com.eldorado.unishare.singleton;

import com.eldorado.unishare.feature.SendReceive;

public class ReceiverInstanceHolder {
    private static SendReceive sendReceiveInstance;

    public static SendReceive getInstance() {
        return sendReceiveInstance;
    }

    public static void setInstance(SendReceive newInstance) {
        sendReceiveInstance = newInstance;
    }
}
