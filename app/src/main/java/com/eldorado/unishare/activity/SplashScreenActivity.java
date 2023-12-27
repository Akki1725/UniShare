package com.eldorado.unishare.activity;

import static com.eldorado.unishare.activity.SettingsActivity.setNightMode;
import static com.eldorado.unishare.activity.SettingsActivity.updateSystemUiVisibility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.model.LottieCompositionCache;
import com.eldorado.unishare.R;
import com.eldorado.unishare.databinding.ActivitySplashScreenBinding;
import com.google.android.material.transition.platform.MaterialSharedAxis;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateSystemUiVisibility(getWindow());
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.loadingAnimation.playAnimation();

        String prefs = PreferenceManager.getDefaultSharedPreferences(this).getString("theme_preference", "1");
        int val = Integer.parseInt(prefs);
        setNightMode(val, getWindow());

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}