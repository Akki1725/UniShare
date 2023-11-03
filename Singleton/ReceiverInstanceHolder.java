package com.michaelians.bluteeth.Singleton;

import com.michaelians.bluteeth.Feature.SendReceive;

public class ReceiverInstanceHolder {
    private static SendReceive sendReceiveInstance;

    public static SendReceive getInstance() {
        return sendReceiveInstance;
    }

    public static void setInstance(SendReceive newInstance) {
        sendReceiveInstance = newInstance;
    }
}
