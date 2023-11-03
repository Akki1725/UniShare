package com.michaelians.bluteeth.Adapter;

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

import com.michaelians.bluteeth.Model.Device;
import com.michaelians.bluteeth.R;
import com.michaelians.bluteeth.databinding.RowDevicesScannedBinding;

import java.util.ArrayList;

public class ScansAdapter extends RecyclerView.Adapter<ScansAdapter.ScansViewHolder> {

    Context context;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<Device> devices;

    public ScansAdapter(Context context, ArrayList<Device> devices, BluetoothAdapter bluetoothAdapter) {
        this.context = context;
        this.devices = devices;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    @NonNull
    @Override
    public ScansAdapter.ScansViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_devices_scanned, parent, false);
        return new ScansAdapter.ScansViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScansAdapter.ScansViewHolder holder, int position) {
        Device device = devices.get(position);

        holder.binding.deviceName.setText(device.getName());
        holder.binding.status.setText(device.getBlueId());

        holder.itemView.setOnClickListener(v -> {
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(device.getMac());

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
            boolean success = bluetoothDevice.createBond();

            if (success) {
                Toast.makeText(context, "Paired to " + device.getName(), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "Pairing failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ScansViewHolder extends RecyclerView.ViewHolder {

        RowDevicesScannedBinding binding;
        public ScansViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowDevicesScannedBinding.bind(itemView);
        }
    }
}
