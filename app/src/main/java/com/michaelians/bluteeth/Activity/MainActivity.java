package com.michaelians.bluteeth.Activity;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.michaelians.bluteeth.Feature.ServerClass;
import com.michaelians.bluteeth.Fragment.DevicesFragment;
import com.michaelians.bluteeth.Fragment.ScansFragment;
import com.michaelians.bluteeth.Model.Device;
import com.michaelians.bluteeth.R;
import com.michaelians.bluteeth.Singleton.ServerClassHolder;
import com.michaelians.bluteeth.Singleton.ThisDevice;
import com.michaelians.bluteeth.databinding.ActivityMainBinding;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    BluetoothAdapter bluetoothAdapter;
    Intent btEnable;
    ActivityResultLauncher<Intent> btActivityResult;
    Handler handler;
    Animation rotateOpen, rotateClose, fromBottom, toBottom;
    int REQUEST_CODE_BT = 7;
    public static final int STATE_LISTENING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int CONNECTION_FAILED = 4;
    boolean bluetoothOn = false;

    public static final String APP_NAME = "BlueTeeth";
    public static final UUID BLUE_UUID = UUID.fromString("0a9ff177-e7fe-49d8-918a-f52016a43a08");
    private boolean clicked = false;

    @SuppressLint({"SetTextI18n", "HardwareIds"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        IntentFilter scanFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        BroadcastReceiver scanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    }
                    String deviceStatus = getDeviceStatus(bluetoothAdapter.getScanMode());
                    binding.debugWindow.setText(deviceStatus);
                }
            }
        };

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        DevicesFragment devicesFragment = new DevicesFragment();
        ScansFragment scansFragment = new ScansFragment();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ThisDevice.setDevice(new Device(bluetoothAdapter.getAddress()));
        bluetoothOn = bluetoothAdapter.isEnabled();
        btEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
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
                        Toast.makeText(this, "Bluetooth enabled successfully!", Toast.LENGTH_LONG).show();
                    } else if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "Bluetooth enabling was cancelled", Toast.LENGTH_LONG).show();
                    }
                }
        );

        if (bluetoothAdapter != null) {
            String deviceStatus = getDeviceStatus(bluetoothAdapter.getScanMode());
            binding.debugWindow.setText(deviceStatus);
        }

        binding.moreBtn.setOnClickListener(v -> {
            onMoreButtonClicked();
        });

        binding.listenBtn.setOnClickListener(v -> {
            ServerClass serverClass = new ServerClass(getApplicationContext(), handler, bluetoothAdapter);
            serverClass.setFunctionToExecute(ServerClass.Function.LISTEN);
            ServerClassHolder.setServerInstance(serverClass);
            ServerClassHolder.getServerInstance().start();
        });

        binding.listenForCallsBtn.setOnClickListener(v -> {
            ServerClass serverClassListen = new ServerClass(getApplicationContext(), handler, bluetoothAdapter);
            serverClassListen.setFunctionToExecute(ServerClass.Function.LISTEN_FOR_CALLS);
            ServerClassHolder.setListenServerInstance(serverClassListen);
            ServerClassHolder.getListenServer().start();
        });

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

        binding.enableDiscoveryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) { }
            startActivity(intent);
        });

        binding.bluetoothBtn.setOnClickListener(v -> {
            if (!bluetoothOn) {
                startBluetooth();
            } else {
                stopBluetooth();
            }

            bluetoothOn = !bluetoothOn;
        });
    }

    void onMoreButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        clicked = !clicked;
    }

    private void setVisibility(boolean clicked) {
        if (!clicked) {
            binding.bluetoothBtn.setVisibility(View.VISIBLE);
            binding.enableDiscoveryBtn.setVisibility(View.VISIBLE);
            binding.listenBtn.setVisibility(View.VISIBLE);
            binding.listenForCallsBtn.setVisibility(View.VISIBLE);
        } else {
            binding.bluetoothBtn.setVisibility(View.INVISIBLE);
            binding.enableDiscoveryBtn.setVisibility(View.INVISIBLE);
            binding.listenBtn.setVisibility(View.INVISIBLE);
            binding.listenForCallsBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(boolean clicked) {
        if (!clicked) {
            binding.bluetoothBtn.startAnimation(fromBottom);
            binding.enableDiscoveryBtn.startAnimation(fromBottom);
            binding.listenBtn.startAnimation(fromBottom);
            binding.listenForCallsBtn.startAnimation(fromBottom);
            binding.moreBtn.startAnimation(rotateOpen);
        } else {
            binding.bluetoothBtn.startAnimation(toBottom);
            binding.enableDiscoveryBtn.startAnimation(toBottom);
            binding.listenBtn.startAnimation(toBottom);
            binding.listenForCallsBtn.startAnimation(toBottom);
            binding.moreBtn.startAnimation(rotateClose);
        }
    }

    private String getDeviceStatus(int scanMode) {
        String deviceStatus;
        if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {
            deviceStatus = "Connected but not discoverable";
        } else if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            deviceStatus = "Connected and discoverable";
        } else if (scanMode == BluetoothAdapter.SCAN_MODE_NONE) {
            deviceStatus = "Not connected";
        } else {
            deviceStatus = "Error retrieving info!";
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
            Toast.makeText(MainActivity.this, "Sorry! Your device doesn't support bluetooth", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Bluetooth disabled successfully!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth enabled successfully!", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth enabling was cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }
}