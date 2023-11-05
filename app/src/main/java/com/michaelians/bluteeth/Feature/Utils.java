package com.michaelians.bluteeth.Feature;

import static android.content.Context.AUDIO_SERVICE;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

public class Utils {
    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void setCommunicationDevice(Context context, Integer targetDeviceType) {
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        List<AudioDeviceInfo> devices = audioManager.getAvailableCommunicationDevices();
        for (AudioDeviceInfo device: devices) {
            if (device.getType() == targetDeviceType) {
                boolean result = audioManager.setCommunicationDevice(device);
            }
        }
    }
}
