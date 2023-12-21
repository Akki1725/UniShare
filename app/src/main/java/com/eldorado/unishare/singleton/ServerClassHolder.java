package com.eldorado.unishare.singleton;

import android.annotation.SuppressLint;

import com.eldorado.unishare.feature.ServerClass;

@SuppressLint("StaticFieldLeak")
public class ServerClassHolder {
    private static ServerClass serverInstance;
    private static ServerClass serverInstanceListen;

    public static ServerClass getServerInstance() {
        return serverInstance;
    }

    public static ServerClass getListenServerInstance() {
        return serverInstanceListen;
    }

    public static void setServerInstance(ServerClass newInstance) {
        serverInstance = newInstance;
    }
    public static void setListenServerInstance(ServerClass newInstance) {
        serverInstanceListen = newInstance;
    }
}