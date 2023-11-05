package com.michaelians.bluteeth.Adapter;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.michaelians.bluteeth.Activity.ChatActivity;
import com.michaelians.bluteeth.Activity.VoiceCallActivity;
import com.michaelians.bluteeth.Feature.ClientClass;
import com.michaelians.bluteeth.Model.Device;
import com.michaelians.bluteeth.R;
import com.michaelians.bluteeth.Singleton.ClientClassHolder;
import com.michaelians.bluteeth.databinding.RowDevicesBinding;

import java.util.ArrayList;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder> {

    Context context;
    Handler handler;
    BluetoothAdapter bluetoothAdapter;
    BroadcastReceiver statusReceiver;
    ArrayList<Device> devices;
    boolean isReceiverRegistered = false;

    public DevicesAdapter(Context context, ArrayList<Device> devices, Handler handler,BluetoothAdapter bluetoothAdapter) {
        this.context = context;
        this.devices = devices;
        this.handler = handler;
        this.bluetoothAdapter = bluetoothAdapter;

        statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("status.connected")) {
                    Device device = devices.get(intent.getIntExtra("devicePosition", -1));
                    if (device != null) {
                        Intent chatIntent = new Intent(context, ChatActivity.class);
                        chatIntent.putExtra("name", device.getName());
                        chatIntent.putExtra("blueId", device.getBlueId());
                        context.startActivity(chatIntent);
                    }
                } else if (intent.getAction().equals("call.send")) {
                    Device device = devices.get(intent.getIntExtra("devicePosition", -1));
                    if (device != null) {
                        Intent chatIntent = new Intent(context, VoiceCallActivity.class);
                        chatIntent.putExtra("deviceName", device.getName());
                        context.startActivity(chatIntent);
                    }
                }
            }
        };
    }

    @NonNull
    @Override
    public DevicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_devices, parent, false);
        return new DevicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesViewHolder holder, int position) {
        Device device = devices.get(position);

        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(device.getMac());
        int devicePosition = holder.getAdapterPosition();

        holder.binding.deviceName.setText(device.getName());
        holder.binding.status.setText(device.getBlueId());

        holder.binding.voiceCall.setOnClickListener(v -> {
            ClientClass clientClass = new ClientClass(bluetoothDevice, context, handler, devicePosition, bluetoothAdapter);
            clientClass.setFunctionToExecute(ClientClass.Function.CALL);
            ClientClassHolder.setClientInstance(clientClass);
            ClientClassHolder.getInstance().start();
        });

        holder.binding.videoCall.setOnClickListener(v -> {

        });

        holder.itemView.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
            int bondState = bluetoothDevice.getBondState();
            if (bondState == BluetoothDevice.BOND_NONE) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
                boolean success = bluetoothDevice.createBond();

                if (success) {
                    Toast.makeText(context, "Paired to " + device.getName(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Pairing failed!", Toast.LENGTH_SHORT).show();
                }
            }

            ClientClass clientClass = new ClientClass(bluetoothDevice, context, handler, devicePosition, bluetoothAdapter);
            clientClass.setFunctionToExecute(ClientClass.Function.CONNECT);
            ClientClassHolder.setClientInstance(clientClass);
            ClientClassHolder.getInstance().start();
        });
    }

    public void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("status.connected");
            intentFilter.addAction("call.send");
            context.registerReceiver(statusReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }

    public void unregisterReceiver() {
        if (isReceiverRegistered) {
            context.unregisterReceiver(statusReceiver);
            isReceiverRegistered = false;
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class DevicesViewHolder extends RecyclerView.ViewHolder {

        RowDevicesBinding binding;
        public DevicesViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowDevicesBinding.bind(itemView);
        }
    }
}
