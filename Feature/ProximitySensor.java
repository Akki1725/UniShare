package com.michaelians.bluteeth.Feature;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.view.WindowManager;

public class ProximitySensor {
    private Context context;
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener proximitySensorListener;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    @SuppressLint("InvalidWakeLockTag")
    public ProximitySensor(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, "ProximitySensorWakeLock");
    }

    public void registerProximitySensor() {
        proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float proximityValue = event.values[0];
                if (proximityValue < 3.0) {
                    // Phone is near the ear, turn off the screen
                    wakeLock.acquire();
                } else {
                    // Phone is moved away from the ear, turn on the screen
                    wakeLock.release();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        sensorManager.registerListener(proximitySensorListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterProximitySensor() {
        sensorManager.unregisterListener(proximitySensorListener);
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
}
