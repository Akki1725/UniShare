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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.eldorado.unishare.adapter.ScansAdapter;
import com.eldorado.unishare.model.Device;
import com.eldorado.unishare.databinding.FragmentScansBinding;

import java.util.ArrayList;

public class ScansFragment extends Fragment {
    FragmentScansBinding binding;
    Context context;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    ScansAdapter scansAdapter;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<Device> discoveredDevices;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();
        binding = FragmentScansBinding.inflate(inflater, container, false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        discoveredDevices = new ArrayList<>();
        scansAdapter = new ScansAdapter(context, discoveredDevices, bluetoothAdapter);
        binding.scanDeviceList.setAdapter(scansAdapter);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, filter);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
        }
        bluetoothAdapter.startDiscovery();

        return binding.getRoot();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                boolean isDuplicate = false;
                for (Device existingDevice : discoveredDevices) {
                    if (existingDevice.getMac().equals(device.getAddress())) {
                        isDuplicate = true;
                        break;
                    }
                }

                if (!isDuplicate) {
                    Device newDevice = new Device(device.getAddress());
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
                    newDevice.setName(device.getName());
                    newDevice.setStatus("Discovered");

                    discoveredDevices.add(newDevice);
                    scansAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(receiver);
    }
}