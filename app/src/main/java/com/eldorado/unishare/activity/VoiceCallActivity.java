package com.eldorado.unishare.activity;

import static com.eldorado.unishare.activity.SettingsActivity.updateSystemUiVisibility;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.eldorado.unishare.R;
import com.eldorado.unishare.databinding.ActivityVoiceCallBinding;
import com.eldorado.unishare.feature.Call;

public class VoiceCallActivity extends AppCompatActivity {

    ActivityVoiceCallBinding binding;
    String deviceName;
    String deviceBlueId;
    Call callControllerClient;
    Call callControllerServer;
    AudioManager audioManager;
    boolean onSpeaker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateSystemUiVisibility(getWindow());
        binding = ActivityVoiceCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        onSpeaker = audioManager.isSpeakerphoneOn();

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.md_theme_light_surface));

        deviceName = getIntent().getStringExtra("deviceName");
        deviceBlueId = getIntent().getStringExtra("blueId");

        binding.deviceName.setText(deviceName);
        binding.deviceAddress.setText(deviceBlueId);

        callControllerClient = new Call(this, deviceBlueId, 0);
        callControllerServer = new Call(this, deviceBlueId, 1);

        callControllerClient.start();
        callControllerServer.start();

        binding.keypadBtn.setOnClickListener(v -> {

        });

        binding.moreBtn.setOnClickListener(v -> {

        });

        binding.muteBtn.setOnClickListener(v -> {

        });

        binding.speakerBtn.setOnClickListener(v -> {
            onSpeaker = !onSpeaker;
            audioManager.setSpeakerphoneOn(onSpeaker);
        });

        binding.callEnd.setOnClickListener(v -> {
            callControllerClient.stopStreaming();
            callControllerServer.stopStreaming();
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        callControllerClient.stopStreaming();
        callControllerServer.stopStreaming();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callControllerClient.stopStreaming();
        callControllerServer.stopStreaming();
    }
}