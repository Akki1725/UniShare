package com.eldorado.unishare.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.eldorado.unishare.activity.MainActivity;
import com.eldorado.unishare.adapter.DevicesAdapter;
import com.eldorado.unishare.feature.ServerClass;
import com.eldorado.unishare.model.Device;
import com.eldorado.unishare.singleton.ServerClassHolder;
import com.eldorado.unishare.databinding.FragmentDevicesBinding;

import java.util.ArrayList;
import java.util.Set;

public class DevicesFragment extends Fragment {

    DevicesAdapter devicesAdapter;
    BluetoothAdapter bluetoothAdapter;
    Handler handler;
    ArrayList<Device> devices;
    FragmentDevicesBinding binding;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDevicesBinding.inflate(inflater, container, false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devices = new ArrayList<>();
        Context context = getContext();
        handler = ((MainActivity) requireActivity()).getHandler();
        devicesAdapter = new DevicesAdapter(context, devices, handler,bluetoothAdapter);
        binding.deviceList.setAdapter(devicesAdapter);
        devicesAdapter.registerReceiver();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
        Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();

        if (bt.size() > 0) {
            for (BluetoothDevice device: bt) {
                Device newDevice = new Device(device.getAddress());

                newDevice.setName(device.getName());
                newDevice.setStatus("Paired");

                devices.add(newDevice);
                devicesAdapter.notifyDataSetChanged();
            }
        }

        return binding.getRoot();
    }
}