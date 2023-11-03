package com.michaelians.bluteeth.Singleton;

import com.michaelians.bluteeth.Feature.ClientClass;

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
