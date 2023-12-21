package com.eldorado.unishare.model;

public class Device {
    private final String mac;
    private final String blueId;
    private String name;
    private String deviceName;
    private String profileImage;
    private String firstName;
    private String lastName;
    private String lastMsg;
    private String address;
    private String city;
    private String zip;
    private String status;

    public Device(String mac) {
        this.mac = mac;
        this.blueId = macToBlueID(mac);
    }

    public String getMac() {
        return mac;
    }

    public String getBlueId() {
        return blueId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void updateName() {
        if (firstName != null) {
            if (lastName != null) {
                this.name = firstName + " " + lastName;
            } else {
                this.name = firstName;
            }
        } else {
            this.name = deviceName;
        }
    }

    public static String macToBlueID(String macAddress) {
        String mac = macAddress.replace(":", "").toUpperCase();
        long decimalValue = Long.parseLong(mac, 16);
        String decimal = Long.toString(decimalValue);
        return decimal.substring(0, 8);
    }
}
