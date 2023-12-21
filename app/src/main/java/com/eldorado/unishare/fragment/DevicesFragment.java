package com.eldorado.unishare.fragment;

import static com.eldorado.unishare.activity.MainActivity.REQUEST_ENABLE_BT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.eldorado.unishare.R;
import com.eldorado.unishare.activity.MainActivity;
import com.eldorado.unishare.adapter.DevicesAdapter;
import com.eldorado.unishare.database.AppDatabase;
import com.eldorado.unishare.databinding.FragmentDevicesBinding;
import com.eldorado.unishare.model.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DevicesFragment extends Fragment {

    Context context;
    DevicesAdapter devicesAdapter;
    BluetoothAdapter bluetoothAdapter;
    Handler handler;
    ArrayList<Device> devices;
    FragmentDevicesBinding binding;
    AppDatabase appDatabase;
    ExecutorService executorService;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDevicesBinding.inflate(inflater, container, false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        context = requireContext();
        devices = new ArrayList<>();
        handler = ((MainActivity) requireActivity()).getHandler();
        devicesAdapter = new DevicesAdapter(context, devices, handler, bluetoothAdapter);

        binding.deviceList.setAdapter(devicesAdapter);
        devicesAdapter.registerReceiver();

        appDatabase = Room.databaseBuilder(requireContext(), AppDatabase.class, "app-database").build();
        executorService = Executors.newSingleThreadExecutor();

        LiveData<List<Device>> storedDevices = appDatabase.deviceDao().getAllDevices();

        storedDevices.observe(getViewLifecycleOwner(), bluetoothDevices -> {
            if (bluetoothDevices != null && bluetoothDevices.size() > 0) {
                devices.clear();
                for (Device bluetoothDevice: bluetoothDevices) {
                    Device newDevice = new Device(bluetoothDevice.getMac());
                    newDevice.setDeviceName(bluetoothDevice.getDeviceName());
                    newDevice.setFirstName(bluetoothDevice.getFirstName());
                    newDevice.setLastName(bluetoothDevice.getLastName());
                    newDevice.setProfileImage(bluetoothDevice.getProfileImage());
                    newDevice.setStatus(bluetoothDevice.getStatus());
                    newDevice.setLastMsg(bluetoothDevice.getLastMsg());
                    newDevice.setCity(bluetoothDevice.getCity());
                    newDevice.setAddress(bluetoothDevice.getAddress());
                    newDevice.setZip(bluetoothDevice.getZip());
                    newDevice.updateName();
                    devices.add(newDevice);
                }

                devicesAdapter.notifyDataSetChanged();
            } else {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH}, REQUEST_ENABLE_BT);
                }
                Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();

                if (bt != null && bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        Device newDevice = new Device(device.getAddress());
                        newDevice.setStatus("Tap to Chat");
                        newDevice.setDeviceName(device.getName());
                        newDevice.updateName();
                        insertDevice(newDevice);
                        devices.add(newDevice);
                    }
                    devicesAdapter.notifyDataSetChanged();
                }
            }
        });

        registerForContextMenu(binding.deviceList);

        return binding.getRoot();
    }

    private void insertDevice(Device device) {
        executorService.execute(() -> appDatabase.deviceDao().addDevice(device));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        int position = info.position;

//        Device device = devices.get(position);

        int itemId = item.getItemId();
        if (itemId == R.id.save) {

        }
        else if (itemId == R.id.copy) {
//            devicesAdapter.copyNumber(device);
        }
        else if (itemId == R.id.delete) {

        }
        else if (itemId == R.id.block) {

        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        devicesAdapter.unregisterReceiver();
        executorService.shutdown();
    }
}