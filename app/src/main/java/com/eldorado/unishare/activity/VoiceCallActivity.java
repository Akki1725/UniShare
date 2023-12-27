package com.eldorado.unishare.activity;

import static com.eldorado.unishare.activity.SettingsActivity.updateSystemUiVisibility;

import android.content.Context;
import android.content.res.ColorStateList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.eldorado.unishare.R;
import com.eldorado.unishare.databinding.ActivityVoiceCallBinding;
import com.eldorado.unishare.feature.Call;
import com.google.android.material.color.MaterialColors;

public class VoiceCallActivity extends AppCompatActivity implements SensorEventListener {

    ActivityVoiceCallBinding binding;
    String deviceName;
    String deviceBlueId;
    String profileImage;
    String city;
    Call callControllerClient;
    Call callControllerServer;
    SensorManager sensorManager;
    Sensor proximitySensor;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    AudioManager audioManager;
    boolean unknown = true;
    boolean onSpeaker, muted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateSystemUiVisibility(getWindow());
        binding = ActivityVoiceCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            audioManager.setSpeakerphoneOn(false);
            onSpeaker = audioManager.isSpeakerphoneOn();
        }

        deviceName = getIntent().getStringExtra("deviceName");
        deviceBlueId = getIntent().getStringExtra("blueId");
        profileImage = getIntent().getStringExtra("profileImage");
        city = getIntent().getStringExtra("city");
        unknown = getIntent().getBooleanExtra("isUnknown", true);

        binding.deviceName.setText(deviceName);
        binding.deviceAddress.setText(deviceBlueId);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "CallActivity:ProximityWakeLock");

        if (wakeLock != null && !onSpeaker) {
            wakeLock.acquire(5 * 60 * 1000L);
        }

        if (city != null) {
            binding.address.setText(city);
        }

        if (profileImage != null) {
            Glide.with(getApplicationContext())
                    .load(Uri.parse(profileImage))
                    .placeholder(R.drawable.avatar)
                    .into(binding.profileImage);
        }

        if (!unknown) {
            callControllerClient = new Call(this, deviceBlueId, 0);
            callControllerServer = new Call(this, deviceBlueId, 1);

            callControllerClient.start();
            callControllerServer.start();
        }

        binding.keypadBtn.setOnClickListener(v -> {

        });

        binding.moreBtn.setOnClickListener(v -> {

        });

        binding.muteBtn.setOnClickListener(v -> {
            muted = !muted;
            int bgTint = muted ? MaterialColors.getColor(binding.getRoot(), com.google.android.material.R.attr.colorOnSecondaryContainer) : MaterialColors.getColor(binding.getRoot(), com.google.android.material.R.attr.colorSecondaryContainer);
            int tint = muted ? MaterialColors.getColor(binding.getRoot(), com.google.android.material.R.attr.colorSecondaryContainer) : MaterialColors.getColor(binding.getRoot(), com.google.android.material.R.attr.colorOnSecondaryContainer);

            binding.muteBtn.setBackgroundTintList(ColorStateList.valueOf(bgTint));
            binding.muteBtn.setImageTintList(ColorStateList.valueOf(tint));
        });

        binding.speakerBtn.setOnClickListener(v -> {
            onSpeaker = !onSpeaker;
            audioManager.setSpeakerphoneOn(onSpeaker);
            int bgTint = onSpeaker ? MaterialColors.getColor(binding.getRoot(), com.google.android.material.R.attr.colorOnSecondaryContainer) : MaterialColors.getColor(binding.getRoot(), com.google.android.material.R.attr.colorSecondaryContainer);
            int tint = onSpeaker ? MaterialColors.getColor(binding.getRoot(), com.google.android.material.R.attr.colorSecondaryContainer) : MaterialColors.getColor(binding.getRoot(), com.google.android.material.R.attr.colorOnSecondaryContainer);

            binding.speakerBtn.setBackgroundTintList(ColorStateList.valueOf(bgTint));
            binding.speakerBtn.setImageTintList(ColorStateList.valueOf(tint));

        });

        binding.callEnd.setOnClickListener(v -> {
            if (!unknown) {
                callControllerClient.stopStreaming();
                callControllerServer.stopStreaming();
            }
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!unknown) {
            callControllerClient.stopStreaming();
            callControllerServer.stopStreaming();
        }
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!unknown) {
            callControllerClient.stopStreaming();
            callControllerServer.stopStreaming();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!onSpeaker) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                float proximityValue = event.values[0];
                if (proximityValue < proximitySensor.getMaximumRange()) {
                    if (wakeLock != null && !wakeLock.isHeld()) {
                        wakeLock.acquire(5 * 60 * 1000L);
                    }
                } else {
                    if (wakeLock != null && wakeLock.isHeld()) {
                        wakeLock.release();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nothing for now
    }
}