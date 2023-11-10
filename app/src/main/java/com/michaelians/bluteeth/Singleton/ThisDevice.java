package com.michaelians.bluteeth.Singleton;

import com.michaelians.bluteeth.Model.Device;

public class ThisDevice {

    private static Device device;

    public static Device getDevice() {
        return device;
    }

    public static void setDevice(Device thisDevice) {
        device = thisDevice;
    }
}
