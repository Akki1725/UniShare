package com.eldorado.unishare.activity;

import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static com.eldorado.unishare.activity.MainActivity.REQUEST_BLUETOOTH_PERMISSION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.eldorado.unishare.R;
import com.eldorado.unishare.databinding.SettingsActivityBinding;
import com.eldorado.unishare.model.Device;
import com.eldorado.unishare.singleton.ThisDevice;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    SettingsActivityBinding binding;
    static BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateSystemUiVisibility(getWindow());
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setContentView(binding.getRoot());
        setSupportActionBar(binding.topBar);
        binding.topBar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (Objects.equals(key, "theme_preference")) {
            String prefs = sharedPreferences.getString(key, "1");
            int val = Integer.parseInt(prefs);
            setNightMode(val, getWindow());
        }

        if (Objects.equals(key, "device_signature")) {
            String newName = sharedPreferences.getString(key, ThisDevice.getDevice().getDeviceName());
            if (ActivityCompat.checkSelfPermission(this, BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
            }
            bluetoothAdapter.setName(newName);
            ThisDevice.getDevice().setDeviceName(bluetoothAdapter.getName());
            ThisDevice.getDevice().updateName();
        }
    }

    public static void setNightMode(int val, Window window) {
        if (val == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (val == 2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (val == 3) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (val == 4) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        }
        updateSystemUiVisibility(window);
    }

    public static void updateSystemUiVisibility(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            int nightMode = AppCompatDelegate.getDefaultNightMode();


            if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                systemUiVisibility &= ~(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }

            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            EditTextPreference deviceSignaturePreference = findPreference("device_signature");

            if (deviceSignaturePreference != null) {
                if (ThisDevice.getDevice() == null) {
                    @SuppressLint("HardwareIds") Device thisDevice = new Device(bluetoothAdapter.getAddress());
                    if (ActivityCompat.checkSelfPermission(requireContext(), BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
                    }
                    thisDevice.setDeviceName(bluetoothAdapter.getName());
                    thisDevice.updateName();
                    ThisDevice.setDevice(thisDevice);
                    deviceSignaturePreference.setDefaultValue(ThisDevice.getDevice().getDeviceName());
                } else {
                    deviceSignaturePreference.setDefaultValue(ThisDevice.getDevice().getDeviceName());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}