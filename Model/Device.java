package com.michaelians.bluteeth.Model;

public class Device {

    String blueId, name, mac, status;

    public Device(String mac) {
        this.mac = mac;
        blueId = macToBlueID(mac);
    }

    public String getBlueId() {
        return blueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static String macToBlueID(String macAddress) {
        String mac = macAddress.replace(":", "").toUpperCase();
        long decimalValue = Long.parseLong(mac, 16);
        String decimal = Long.toString(decimalValue);
        String blueId = decimal.substring(0, 8);

        return blueId;
    }
}
