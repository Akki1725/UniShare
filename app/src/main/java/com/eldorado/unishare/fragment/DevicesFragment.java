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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.eldorado.unishare.activity.MainActivity;
import com.eldorado.unishare.adapter.DevicesAdapter;
import com.eldorado.unishare.databinding.FragmentDevicesBinding;
import com.eldorado.unishare.model.Device;
import com.google.android.material.transition.MaterialFadeThrough;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDevicesBinding.inflate(inflater, container, false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Context context = requireContext();
        devices = new ArrayList<>();
        handler = ((MainActivity) requireActivity()).getHandler();
        devicesAdapter = new DevicesAdapter(context, devices, handler, bluetoothAdapter);

        binding.deviceList.setAdapter(devicesAdapter);
        devicesAdapter.registerReceiver();

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
                devices.add(newDevice);
            }
            devicesAdapter.notifyDataSetChanged();
        }

        return binding.getRoot();

//        if (devices == null) {
//            devices = new ArrayList<>();
//            @SuppressLint("UseCompatLoadingForDrawables")
//            Bitmap placeholderBitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.avatar)).getBitmap();
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
//            Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
//
//            if (bt != null && bt.size() > 0) {
//                for (BluetoothDevice device: bt) {
//                    Device newDevice = new Device(device.getAddress());
//                    String savedImagePath = saveBitmapToInternalStorage(context, placeholderBitmap, "profile_image_" + newDevice.getBlueId() + ".png");
//                    newDevice.setProfileImage(savedImagePath);
//                    newDevice.setStatus("Tap to Chat");
//                    newDevice.setDeviceName(device.getName());
//                    devices.add(newDevice);
//                }
//
//                saveDevicesToStorage(context, devices);
//            } else {
//                devices = new ArrayList<>();
//            }
//
//            devices = loadDevicesFromStorage(context);
//        }
    }

//
//    public ArrayList<Device> loadDevicesFromStorage(Context context) {
//        SharedPreferences preferences = context.getSharedPreferences("UniSharePreferences", MODE_PRIVATE);
//        String savedJsonArray = preferences.getString("devices", "");
//
//        if (savedJsonArray.isEmpty()) {
//            return null;
//        }
//
//        Gson gson = new Gson();
//        Type type = new TypeToken<ArrayList<Device>>() {}.getType();
//        return gson.fromJson(savedJsonArray, type);
//    }
//
//    public void saveDevicesToStorage(Context context, ArrayList<Device> devices) {
//        SharedPreferences preferences = context.getSharedPreferences("UniSharePreferences", MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//
//        Gson gson = new Gson();
//        String jsonArray = gson.toJson(devices);
//
//        editor.putString("devices", jsonArray);
//        editor.apply();
//    }
//
//    public String saveBitmapToInternalStorage(Context context, Bitmap bitmap, String fileName) {
//        File fileDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File file = new File(fileDir, fileName);
//
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return file.getAbsolutePath();
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}