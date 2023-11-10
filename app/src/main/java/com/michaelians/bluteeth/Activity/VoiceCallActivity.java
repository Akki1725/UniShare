package com.michaelians.bluteeth.Activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.michaelians.bluteeth.Feature.Call;
import com.michaelians.bluteeth.R;
import com.michaelians.bluteeth.databinding.ActivityVoiceCallBinding;

public class VoiceCallActivity extends AppCompatActivity {

    ActivityVoiceCallBinding binding;
    String deviceName;
    String deviceBlueId;
    Call callController;
    boolean onSpeaker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoiceCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.md_theme_light_surface));

        deviceName = getIntent().getStringExtra("deviceName");
        deviceBlueId = getIntent().getStringExtra("blueId");
        binding.deviceName.setText(deviceName);
        binding.deviceAddress.setText(deviceBlueId);

        callController = new Call(this, deviceBlueId);
        callController.start();

        binding.keypadBtn.setOnClickListener(v -> {

        });

        binding.moreBtn.setOnClickListener(v -> {

        });

        binding.muteBtn.setOnClickListener(v -> {

        });

        binding.speakerBtn.setOnClickListener(v -> {
            if (!onSpeaker) {

            }
        });

        binding.callEnd.setOnClickListener(v -> {
            callController.stopStreaming();
            callController.playAudio();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        callController.stopStreaming();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callController.stopStreaming();
    }
}