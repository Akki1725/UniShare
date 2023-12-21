package com.eldorado.unishare.activity;

import static com.eldorado.unishare.activity.SettingsActivity.updateSystemUiVisibility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.platform.MaterialSharedAxis;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final int STATE_LISTENING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int CONNECTION_FAILED = 4;
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_CODE_BT = 7;

    public static final String APP_NAME = "UniShare";
    public static final UUID BLUE_UUID = UUID.fromString("0a9ff177-e7fe-49d8-918a-f52016a43a08");

    ActivityMainBinding binding;
    BluetoothAdapter bluetoothAdapter;
    Intent btEnable;
    ActivityResultLauncher<Intent> btActivityResult;
    Handler handler;
    boolean bluetoothOn = false;

    @SuppressLint({"SetTextI18n", "HardwareIds"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateSystemUiVisibility(getWindow());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);

        getWindow().setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.Z, true));

        View view = getWindow().getDecorView().findViewById(R.id.mainToolbar);

        MaterialSharedAxis exitTransition = new MaterialSharedAxis(MaterialSharedAxis.X, true);
        exitTransition.excludeTarget(com.google.android.material.R.id.action_bar_container, true);
        exitTransition.excludeTarget(R.id.mainToolbar, true);
        exitTransition.excludeTarget(view.getId(), true);
        exitTransition.excludeTarget(android.R.id.statusBarBackground, true);
        exitTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(exitTransition);

        MaterialSharedAxis reenterTransition = new MaterialSharedAxis(MaterialSharedAxis.X, false);
        reenterTransition.excludeTarget(com.google.android.material.R.id.action_bar_container, true);
        reenterTransition.excludeTarget(R.id.mainToolbar, true);
        reenterTransition.excludeTarget(android.R.id.statusBarBackground, true);
        reenterTransition.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setReenterTransition(reenterTransition);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        DevicesFragment devicesFragment = new DevicesFragment();
        ScansFragment scansFragment = new ScansFragment();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ThisDevice.setDevice(new Device(bluetoothAdapter.getAddress()));
        bluetoothOn = bluetoothAdapter.isEnabled();
        btEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        IntentFilter scanFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        BroadcastReceiver scanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                assert action != null;
                if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) { }
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
                        SnackbarUtils.showSnackbar(this, binding.mainLayout, "Bluetooth enabled successfully!", Toast.LENGTH_SHORT);
                    } else if (resultCode == RESULT_CANCELED) {
                        SnackbarUtils.showSnackbar(this, binding.mainLayout, "Bluetooth enabling was cancelled", Toast.LENGTH_SHORT);
                    }
                }
        );

        if (bluetoothAdapter != null) {
            String deviceStatus = getDeviceStatus(bluetoothAdapter.getScanMode());
            binding.debugWindow.setText(deviceStatus);
        }

        BadgeDrawable devicesBadge = binding.navBar.getOrCreateBadge(R.id.devices_menu);
        BadgeDrawable scansBadge = binding.navBar.getOrCreateBadge(R.id.scan_menu);

        devicesBadge.setVisible(true);
        scansBadge.setVisible(true);
        scansBadge.setNumber(3);

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
                ServerClass serverClass = new ServerClass(getApplicationContext(), handler, bluetoothAdapter);
                serverClass.setFunctionToExecute(ServerClass.Function.LISTEN);
                ServerClassHolder.setServerInstance(serverClass);
                ServerClassHolder.getServerInstance().start();
            }

            else if (id == R.id.listenForCallsBtn) {
                ServerClass serverClassListen = new ServerClass(getApplicationContext(), handler, bluetoothAdapter);
                serverClassListen.setFunctionToExecute(ServerClass.Function.LISTEN_FOR_CALLS);
                ServerClassHolder.setListenServerInstance(serverClassListen);
                ServerClassHolder.getListenServerInstance().start();
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

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
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
}
