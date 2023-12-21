package com.eldorado.unishare.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.eldorado.unishare.R;
import com.eldorado.unishare.adapter.ScansAdapter;
import com.eldorado.unishare.databinding.FragmentScansBinding;
import com.eldorado.unishare.model.Device;
import com.google.android.material.transition.MaterialFadeThrough;

import java.util.ArrayList;

public class ScansFragment extends Fragment {
    FragmentScansBinding binding;
    Context context;
    ScansAdapter scansAdapter;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<Device> discoveredDevices;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();
        binding = FragmentScansBinding.inflate(inflater, container, false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        discoveredDevices = new ArrayList<>();
        scansAdapter = new ScansAdapter(context, discoveredDevices, bluetoothAdapter, binding.getRoot().findViewById(R.id.main_layout));
        binding.scanDeviceList.setAdapter(scansAdapter);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, filter);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) { }

        bluetoothAdapter.startDiscovery();

        return binding.getRoot();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                boolean isDuplicate = false;

                for (Device existingDevice: discoveredDevices) {
                    assert device != null;
                    if (existingDevice.getMac().equals(device.getAddress())) {
                        isDuplicate = true;
                        break;
                    }
                }

                if (!isDuplicate) {
                    assert device != null;
                    Device newDevice = new Device(device.getAddress());
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
                    newDevice.setDeviceName(device.getName());
                    newDevice.setStatus("Discovered");
                    newDevice.updateName();
                    discoveredDevices.add(newDevice);
                    scansAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(receiver);
    }
}
