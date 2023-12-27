package com.eldorado.unishare.adapter;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.eldorado.unishare.feature.SnackbarUtils;
import com.eldorado.unishare.model.Device;
import com.eldorado.unishare.R;
import com.eldorado.unishare.databinding.RowDevicesScannedBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ScansAdapter extends RecyclerView.Adapter<ScansAdapter.ScansViewHolder> {

    Context context;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<Device> devices;
    View rootView;

    public ScansAdapter(Context context, ArrayList<Device> devices, BluetoothAdapter bluetoothAdapter, View rootView) {
        this.context = context;
        this.devices = devices;
        this.bluetoothAdapter = bluetoothAdapter;
        this.rootView = rootView;
    }

    @NonNull
    @Override
    public ScansAdapter.ScansViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_devices_scanned, parent, false);
        return new ScansViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScansAdapter.ScansViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.binding.deviceName.setText(device.getDeviceName());
        holder.binding.status.setText(device.getBlueId());
        holder.itemView.setOnClickListener(v -> pairDevice(device));
        holder.binding.pairDevice.setOnClickListener(v -> pairDevice(device));
    }

    private Boolean pairDevice(Device device) {

        if (bluetoothAdapter == null) {
            return false;
        }

        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(device.getMac());
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }

        boolean success = bluetoothDevice.createBond();

        if (success) {
            if (rootView != null) {
                SnackbarUtils.showSnackbar(context, rootView, "Paired to " + device.getName(), Snackbar.LENGTH_SHORT);
            }
        }
        else {
            if (rootView != null) {
                SnackbarUtils.showSnackbar(context, rootView, "Pairing failed!", Snackbar.LENGTH_SHORT, "Retry", new Runnable() {
                    @Override
                    public void run() {
                        pairDevice(device);
                    }
                });
            }
        }

        return success;
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class ScansViewHolder extends RecyclerView.ViewHolder {

        RowDevicesScannedBinding binding;
        public ScansViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowDevicesScannedBinding.bind(itemView);
        }
    }
}