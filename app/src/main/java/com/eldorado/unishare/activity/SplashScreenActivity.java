package com.eldorado.unishare.activity;

import static com.eldorado.unishare.activity.SettingsActivity.setNightMode;
import static com.eldorado.unishare.activity.SettingsActivity.updateSystemUiVisibility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.eldorado.unishare.R;
import com.google.android.material.transition.platform.MaterialSharedAxis;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateSystemUiVisibility(getWindow());
        setContentView(R.layout.activity_splash_screen);
        getWindow().setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.Z, false));

        String prefs = PreferenceManager.getDefaultSharedPreferences(this).getString("theme_preference", "1");
        int val = Integer.parseInt(prefs);
        setNightMode(val, getWindow());

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 1000);
    }
}