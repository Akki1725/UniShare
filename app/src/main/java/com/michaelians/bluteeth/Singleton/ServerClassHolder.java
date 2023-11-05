package com.michaelians.bluteeth.Singleton;

import com.michaelians.bluteeth.Feature.ServerClass;

public class ServerClassHolder {
    private static ServerClass serverInstance;
    private static ServerClass serverInstanceListen;

    public static ServerClass getServerInstance() {
        return serverInstance;
    }

    public static ServerClass getListenServer() {
        return serverInstanceListen;
    }

    public static void setServerInstance(ServerClass newInstance) {
        serverInstance = newInstance;
    }
    public static void setListenServerInstance(ServerClass newInstance) {
        serverInstanceListen = newInstance;
    }
}
