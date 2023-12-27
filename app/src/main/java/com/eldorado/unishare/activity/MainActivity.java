package com.eldorado.unishare.activity;

import static android.widget.Toast.LENGTH_SHORT;
import static com.eldorado.unishare.activity.SettingsActivity.updateSystemUiVisibility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.eldorado.unishare.R;
import com.eldorado.unishare.databinding.ActivityMainBinding;
import com.eldorado.unishare.feature.ServerClass;
import com.eldorado.unishare.feature.SnackbarUtils;
import com.eldorado.unishare.fragment.DevicesFragment;
import com.eldorado.unishare.fragment.ScansFragment;
import com.eldorado.unishare.model.Device;
import com.eldorado.unishare.singleton.ServerClassHolder;
import com.eldorado.unishare.singleton.ThisDevice;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.platform.MaterialSharedAxis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    BluetoothAdapter bluetoothAdapter;
    Intent btEnable;
    ActivityResultLauncher<Intent> btActivityResult;
    Handler handler;
    BottomSheetBehavior<View> dialer;
    DevicesFragment devicesFragment;
    ToneGenerator toneGenerator;
    boolean bluetoothOn = false;

    public static final int STATE_LISTENING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int CONNECTION_FAILED = 4;
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_CODE_BT = 7;
    public static final int REQUEST_BLUETOOTH_PERMISSION = 2524;
    public static final String APP_NAME = "UniShare";
    public static final UUID BLUE_UUID = UUID.fromString("0a9ff177-e7fe-49d8-918a-f52016a43a08");

    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint({"SetTextI18n", "HardwareIds"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateSystemUiVisibility(getWindow());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);

//        View view = getWindow().getDecorView().findViewById(R.id.mainToolbar);
//
//        MaterialSharedAxis exitTransition = new MaterialSharedAxis(MaterialSharedAxis.X, true);
//        exitTransition.excludeTarget(com.google.android.material.R.id.action_bar_container, true);
//        exitTransition.excludeTarget(R.id.mainToolbar, true);
//        exitTransition.excludeTarget(view.getId(), true);
//        exitTransition.excludeTarget(android.R.id.statusBarBackground, true);
//        exitTransition.excludeTarget(android.R.id.navigationBarBackground, true);
//        getWindow().setExitTransition(exitTransition);
//
//        MaterialSharedAxis reenterTransition = new MaterialSharedAxis(MaterialSharedAxis.X, false);
//        reenterTransition.excludeTarget(com.google.android.material.R.id.action_bar_container, true);
//        reenterTransition.excludeTarget(R.id.mainToolbar, true);
//        reenterTransition.excludeTarget(android.R.id.statusBarBackground, true);
//        reenterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
//        getWindow().setReenterTransition(reenterTransition);

        toneGenerator = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, 100);
        devicesFragment = new DevicesFragment();
        ScansFragment scansFragment = new ScansFragment();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothOn = bluetoothAdapter.isEnabled();
        requestBluetoothPermissions();
        onClick(binding.getRoot());
        btEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_CODE_BT);
        }

        Device thisDevice = new Device(bluetoothAdapter.getAddress());
        thisDevice.setDeviceName(bluetoothAdapter.getName());
        thisDevice.updateName();
        ThisDevice.setDevice(thisDevice);

        IntentFilter scanFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        BroadcastReceiver scanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                assert action != null;
                if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_CODE_BT);
                    }
                    String deviceStatus = getDeviceStatus(bluetoothAdapter.getScanMode());
                    binding.debugWindow.setText(deviceStatus);
                }
            }
        };

        registerReceiver(scanReceiver, scanFilter);
        switchFragment(devicesFragment);

        handler = new Handler(msg -> {
            switch (msg.what) {
                case STATE_LISTENING:
                    binding.statusBar.setText("Listening...");
                    break;
                case STATE_CONNECTING:
                    binding.statusBar.setText("Connecting...");
                    break;
                case STATE_CONNECTED:
                    binding.statusBar.setText("Connected!");
                    break;
                case CONNECTION_FAILED:
                    binding.statusBar.setText("Connection Failed!");
                    break;
            }
            return true;
        });

        btActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int resultCode = result.getResultCode();
                    if (resultCode == RESULT_OK) {
                        SnackbarUtils.showSnackbar(this, binding.mainLayout, "Bluetooth enabled successfully!", LENGTH_SHORT);
                    } else if (resultCode == RESULT_CANCELED) {
                        SnackbarUtils.showSnackbar(this, binding.mainLayout, "Bluetooth enabling was cancelled", LENGTH_SHORT);
                    }
                }
        );

        if (bluetoothAdapter != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_CODE_BT);
            }
        }
        String deviceStatus = getDeviceStatus(bluetoothAdapter.getScanMode());
        binding.debugWindow.setText(deviceStatus);

        BadgeDrawable devicesBadge = binding.navBar.getOrCreateBadge(R.id.devices_menu);
        BadgeDrawable scansBadge = binding.navBar.getOrCreateBadge(R.id.scan_menu);

        devicesBadge.setVisible(true);
        scansBadge.setVisible(true);
        scansBadge.setNumber(3);

        dialer = BottomSheetBehavior.from(binding.dialerLayout);
        dialer.setPeekHeight(0);
        dialer.setState(BottomSheetBehavior.STATE_COLLAPSED);

        dialer.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) { }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float targetElevation = 12 * getResources().getDisplayMetrics().density * slideOffset;
                binding.dialerLayout.setElevation(targetElevation);
            }
        });

        binding.dialer.setOnClickListener(v -> dialer.setState(BottomSheetBehavior.STATE_EXPANDED));

        binding.mainToolbar.setNavigationOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        binding.navBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.devices_menu) {
                switchFragment(devicesFragment);
                return true;
            } else if (item.getItemId() == R.id.scan_menu) {
                switchFragment(scansFragment);
                return true;
            } else {
                return false;
            }
        });

        binding.navigationView.setCheckedItem(R.id.home);

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.bluetoothBtn) {
                if (!bluetoothOn) {
                    startBluetooth();
                } else {
                    stopBluetooth();
                }

                bluetoothOn = !bluetoothOn;
            }

            else if (id == R.id.enableDiscoveryBtn) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) { }
                startActivity(intent);
            }

            else if (id == R.id.listenBtn) {
                if (!bluetoothAdapter.isEnabled()) {
                    Toast.makeText(this, "Can't listen, Bluetooth is not connected", LENGTH_SHORT).show();
                } else {
                    ServerClass serverClass = new ServerClass(getApplicationContext(), handler, bluetoothAdapter);
                    serverClass.setFunctionToExecute(ServerClass.Function.LISTEN);
                    ServerClassHolder.setServerInstance(serverClass);
                    ServerClassHolder.getServerInstance().start();
                }
            }

            else if (id == R.id.listenForCallsBtn) {
                if (!bluetoothAdapter.isEnabled()) {
                    Toast.makeText(this, "Can't listen, Bluetooth is not connected", LENGTH_SHORT).show();
                } else {
                    ServerClass serverClassListen = new ServerClass(getApplicationContext(), handler, bluetoothAdapter);
                    serverClassListen.setFunctionToExecute(ServerClass.Function.LISTEN_FOR_CALLS);
                    ServerClassHolder.setListenServerInstance(serverClassListen);
                    ServerClassHolder.getListenServerInstance().start();
                }
            }

            else if (id == R.id.settings) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }

            else if (id == R.id.about) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestBluetoothPermissions() {
        String[] bluetoothPermissions = {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
        };

        ActivityCompat.requestPermissions(this, bluetoothPermissions, REQUEST_BLUETOOTH_PERMISSION);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (dialer.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            dialer.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        else {
            super.onBackPressed();
        }
    }

    private String getDeviceStatus(int scanMode) {
        String deviceStatus;
        if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {
            deviceStatus = "Connected but not discoverable";
        } else if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            deviceStatus = "Connected and discoverable";
        } else if (scanMode == BluetoothAdapter.SCAN_MODE_NONE) {
            if (bluetoothOn) {
                deviceStatus = "Connected but not discoverable";
            } else {
                deviceStatus = "Not connected";
            }
        } else {
            deviceStatus = "Error retrieving info";
        }

        return deviceStatus;
    }

    public Handler getHandler() {
        return handler;
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framer, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    private void startBluetooth() {
        if (bluetoothAdapter == null) {
            SnackbarUtils.showSnackbar(MainActivity.this, binding.mainLayout, "Sorry! Your device doesn't support bluetooth", Snackbar.LENGTH_LONG);
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                btActivityResult.launch(btEnable);
            }
        }
    }

    private void stopBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
            bluetoothAdapter.disable();
            SnackbarUtils.showSnackbar(MainActivity.this, binding.mainLayout, "Bluetooth disabled successfully!", Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BT) {
            if (resultCode == RESULT_OK) {
                SnackbarUtils.showSnackbar(MainActivity.this, binding.mainLayout, "Bluetooth enabled successfully!", Snackbar.LENGTH_SHORT);
            } else if (resultCode == RESULT_CANCELED) {
                SnackbarUtils.showSnackbar(MainActivity.this, binding.mainLayout, "Bluetooth enabling was cancelled", Snackbar.LENGTH_SHORT);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint({"ResourceType", "SetTextI18n"})
    public void onClick (View v) {

        binding.numZero.setOnLongClickListener(v13 -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
                ClipData.Item item = Objects.requireNonNull(clipboardManager.getPrimaryClip()).getItemAt(0);
                CharSequence pasteText = item.getText();

                if (pasteText != null) {
                    binding.blueIdContainer.setText(pasteText);
                }
            }
            return true;
        });

        for (int i = 0; i <= 9; i++) {
            int resourceId = getResources().getIdentifier("num_" + number(i), "id", getPackageName());

            if (v.getId() == resourceId) {
                binding.blueIdContainer.setText(binding.blueIdContainer.getText() + String.valueOf(i));
                playDTMFTone(String.valueOf(i));
                break;
            }
        }

        if (v.getId() == R.id.num_star) {
            binding.blueIdContainer.setText(binding.blueIdContainer.getText() + "*");
            playDTMFTone("*");
        }
        else if (v.getId() == R.id.num_hash) {
            binding.blueIdContainer.setText(binding.blueIdContainer.getText() + "#");
            playDTMFTone("#");
        }
        else if (v.getId() == R.id.backspace) {
            String text = binding.blueIdContainer.getText().toString();
            text = text.substring(0, text.length() - 1);
            binding.blueIdContainer.setText(text);
            if (text.length() == 0) {
                toggleBackspace(false);
            }
        }

        binding.backspace.setOnLongClickListener(v1 -> {
            binding.blueIdContainer.setText("");
            toggleBackspace(false);
            return true;
        });

        binding.numHash.setOnLongClickListener(v12 -> {
            dialer.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return true;
        });

        if (binding.blueIdContainer.length() > 0) {
            toggleBackspace(true);
        }
        else {
            toggleBackspace(false);
        }

        if (v.getId() == R.id.call_btn) {
            String blueId = String.valueOf(binding.blueIdContainer.getText());
            if (!blueId.isEmpty()) {
                ArrayList<Device> devices = devicesFragment.getDevices();

                Optional<Device> foundDevice = devices.stream().filter(device -> device.getBlueId().equals(blueId)).findFirst();
                if (foundDevice.isPresent()) {
                    Device contact = foundDevice.get();
                    Intent callIntent = new Intent(MainActivity.this, VoiceCallActivity.class);
                    callIntent.putExtra("deviceName", contact.getName());
                    callIntent.putExtra("blueId", contact.getBlueId());
                    callIntent.putExtra("profileImage", contact.getProfileImage());
                    callIntent.putExtra("city", contact.getCity());
                    callIntent.putExtra("isUnknown", true);
                    startActivity(callIntent);
                }
                else {
                    Intent callIntent = new Intent(MainActivity.this, VoiceCallActivity.class);
                    callIntent.putExtra("deviceName", "Unknown");
                    callIntent.putExtra("blueId", blueId);
                    callIntent.putExtra("isUnknown", true);
                    startActivity(callIntent);
                }
            }
        }
    }

    void playDTMFTone(String digit) {
        String dtmfDigits = "0123456789*#";
        int index = dtmfDigits.indexOf(digit);

        if (index != -1) {
            int toneType;
            switch (digit) {
                case "0":
                    toneType = ToneGenerator.TONE_DTMF_0;
                    break;
                case "1":
                    toneType = ToneGenerator.TONE_DTMF_1;
                    break;
                case "2":
                    toneType = ToneGenerator.TONE_DTMF_2;
                    break;
                case "3":
                    toneType = ToneGenerator.TONE_DTMF_3;
                    break;
                case "4":
                    toneType = ToneGenerator.TONE_DTMF_4;
                    break;
                case "5":
                    toneType = ToneGenerator.TONE_DTMF_5;
                    break;
                case "6":
                    toneType = ToneGenerator.TONE_DTMF_6;
                    break;
                case "7":
                    toneType = ToneGenerator.TONE_DTMF_7;
                    break;
                case "8":
                    toneType = ToneGenerator.TONE_DTMF_8;
                    break;
                case "9":
                    toneType = ToneGenerator.TONE_DTMF_9;
                    break;
                case "*":
                    toneType = 10;
                    break;
                case "#":
                    toneType = 11;
                    break;
                default:
                    return;
            }
            toneGenerator.startTone(toneType, 150);
        }
    }

    private void toggleBackspace(boolean enabled) {
        binding.backspace.setEnabled(enabled);
        int colorFilter = enabled ? MaterialColors.getColor(binding.getRoot(), com.google.android.material.R.attr.colorOnSurface) : MaterialColors.getColor(binding.getRoot(), com.google.android.material.R.attr.colorOutline);
        binding.backspace.setColorFilter(colorFilter);
    }

    public static String number(int number) {
        switch (number) {
            case 0:
                return "zero";
            case 1:
                return "one";
            case 2:
                return "two";
            case 3:
                return "three";
            case 4:
                return "four";
            case 5:
                return "five";
            case 6:
                return "six";
            case 7:
                return "seven";
            case 8:
                return "eight";
            case 9:
                return "nine";
            default:
                return "";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toneGenerator != null) {
            toneGenerator.release();
        }
    }
}
