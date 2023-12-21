package com.eldorado.unishare.singleton;

import com.eldorado.unishare.model.Device;

public class ThisDevice {

    private static Device device;

    public static Device getDevice() {
        return device;
    }

    public static void setDevice(Device thisDevice) {
        device = thisDevice;
    }
}