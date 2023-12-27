package com.eldorado.unishare.fragment;

import static com.eldorado.unishare.activity.MainActivity.REQUEST_ENABLE_BT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.eldorado.unishare.R;
import com.eldorado.unishare.activity.MainActivity;
import com.eldorado.unishare.adapter.DevicesAdapter;
import com.eldorado.unishare.database.AppDatabase;
import com.eldorado.unishare.databinding.FragmentDevicesBinding;
import com.eldorado.unishare.model.Device;
import com.eldorado.unishare.singleton.ThisDevice;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class DevicesFragment extends Fragment {

    Context context;
    DevicesAdapter devicesAdapter;
    BluetoothAdapter bluetoothAdapter;
    Handler handler;
    ArrayList<Device> devices;
    FragmentDevicesBinding binding;
    AppDatabase appDatabase;
    ExecutorService executorService;
    ActivityResultLauncher<String> getContentLauncher;
    int contextItemPosition;
    ActivityResultCallback<Uri> imageCallback;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDevicesBinding.inflate(inflater, container, false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        context = requireContext();
        devices = new ArrayList<>();
        devices.add(ThisDevice.getDevice());
        handler = ((MainActivity) requireActivity()).getHandler();

        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (imageCallback != null) {
                imageCallback.onActivityResult(uri);
            }
        });

        devicesAdapter = new DevicesAdapter(context, devices, handler, bluetoothAdapter, position -> {
            contextItemPosition = position;
            return position;
        });

        binding.deviceList.setAdapter(devicesAdapter);
        devicesAdapter.registerReceiver();

        appDatabase = Room.databaseBuilder(requireContext(), AppDatabase.class, "app-database").build();
        executorService = Executors.newSingleThreadExecutor();

        LiveData<List<Device>> storedDevices = appDatabase.deviceDao().getAllDevices();
        storedDevices.observe(getViewLifecycleOwner(), bluetoothDevices -> {
            if (bluetoothDevices != null && bluetoothDevices.size() > 0) {
                devices.clear();
                devices.add(ThisDevice.getDevice());
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
                int resourceId = R.drawable.avatar;
                Uri uri = Uri.parse("android.resource://" + getResources().getResourcePackageName(resourceId) + "/" + getResources().getResourceTypeName(resourceId) + "/" + getResources().getResourceEntryName(resourceId));

                if (bt != null && bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        Device newDevice = new Device(device.getAddress());
                        newDevice.setStatus("Tap to Chat");
                        newDevice.setDeviceName(device.getName());
                        newDevice.setProfileImage(uri.toString());
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

    private void updateDevice(Device device) {
        executorService.execute(() -> appDatabase.deviceDao().updateDatabase(device));
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Device selectedDevice = devices.get(contextItemPosition);

        int itemId = item.getItemId();
        if (itemId == R.id.save) {
            View layout = LayoutInflater.from(context).inflate(R.layout.dialog_save_contact, null);

            ArrayList<Device> updatedList = devices;
            final Uri[] selectedImgUri = new Uri[1];
            final String[] firstName = new String[1];
            final String[] lastName = new String[1];
            final String[] deviceName = new String[1];
            final String[] address = new String[1];
            final String[] city = new String[1];
            final String[] zip = new String[1];

            Device newContact = updatedList.get(contextItemPosition);
            assert newContact != null;

            CircleImageView profileImage = layout.findViewById(R.id.profile_image);
            TextInputEditText firstNameText = layout.findViewById(R.id.first_name);
            TextInputEditText lastNameText = layout.findViewById(R.id.last_name);
            TextInputEditText deviceNameText = layout.findViewById(R.id.device_name);
            TextInputEditText blueIdText = layout.findViewById(R.id.blue_id);
            TextInputEditText addressText = layout.findViewById(R.id.address);
            TextInputEditText cityText = layout.findViewById(R.id.city);
            TextInputEditText zipText = layout.findViewById(R.id.zip);

            imageCallback = selectedImage -> {
                profileImage.setImageURI(selectedImage);
                selectedImgUri[0] = selectedImage;
                Glide.with(context)
                        .load(selectedImgUri[0])
                        .placeholder(R.drawable.avatar)
                        .into(profileImage);
            };

            if (newContact.getProfileImage() != null) {
                Glide.with(this)
                        .load(Uri.parse(newContact.getProfileImage()))
                        .placeholder(R.drawable.avatar)
                        .into(profileImage);
            }

            profileImage.setOnClickListener(v -> getContentLauncher.launch("image/*"));

            firstNameText.setText(newContact.getFirstName());
            lastNameText.setText(newContact.getLastName());
            deviceNameText.setText(newContact.getDeviceName());
            blueIdText.setText(newContact.getBlueId());
            addressText.setText(newContact.getAddress());
            cityText.setText(newContact.getCity());
            zipText.setText(newContact.getZip());

            AlertDialog alertDialog = new MaterialAlertDialogBuilder(context, R.style.alertDialogStyle)
                    .setTitle("Add to contacts")
                    .setCancelable(false)
                    .setView(layout)
                    .setPositiveButton("Save", (dialog, which) -> {
                        firstName[0] = Objects.requireNonNull(firstNameText.getText()).toString();
                        lastName[0] = Objects.requireNonNull(lastNameText.getText()).toString();
                        deviceName[0] = Objects.requireNonNull(deviceNameText.getText()).toString();
                        address[0] = Objects.requireNonNull(addressText.getText()).toString();
                        city[0] = Objects.requireNonNull(cityText.getText()).toString();
                        zip[0] = Objects.requireNonNull(zipText.getText()).toString();

                        newContact.setFirstName(firstName[0]);
                        newContact.setLastName(lastName[0]);
                        newContact.setDeviceName(deviceName[0]);
                        newContact.setAddress(address[0]);
                        newContact.setCity(city[0]);
                        newContact.setZip(zip[0]);
                        if (selectedImgUri[0] != null) {
                            newContact.setProfileImage(selectedImgUri[0].toString());
                        }

                        updateDevice(newContact);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                    .create();

            alertDialog.show();
        }
        else if (itemId == R.id.copy) {
            devicesAdapter.copyNumber(selectedDevice);
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