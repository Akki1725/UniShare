package com.eldorado.unishare.adapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.eldorado.unishare.R;
import com.eldorado.unishare.activity.ChatActivity;
import com.eldorado.unishare.activity.VoiceCallActivity;
import com.eldorado.unishare.databinding.RowDevicesBinding;
import com.eldorado.unishare.feature.ClientClass;
import com.eldorado.unishare.model.Device;
import com.eldorado.unishare.singleton.ClientClassHolder;
import com.eldorado.unishare.singleton.ThisDevice;

import java.util.ArrayList;
import java.util.Objects;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder> implements View.OnCreateContextMenuListener {

    Context context;
    Handler handler;
    BluetoothAdapter bluetoothAdapter;
    BroadcastReceiver statusReceiver;
    ArrayList<Device> devices;
    static RecyclerViewClickListener recyclerViewClickListener;
    static int contextItemPosition = -1;
    boolean isReceiverRegistered = false;

    public DevicesAdapter(Context context, ArrayList<Device> devices, Handler handler,BluetoothAdapter bluetoothAdapter, RecyclerViewClickListener listener) {
        this.context = context;
        this.devices = devices;
        this.handler = handler;
        this.bluetoothAdapter = bluetoothAdapter;
        recyclerViewClickListener = listener;

        statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.equals(intent.getAction(), "status.connected")) {
                    Device device = devices.get(intent.getIntExtra("devicePosition", -1));
                    if (device != null) {
                        Intent chatIntent = new Intent(context, ChatActivity.class);
                        chatIntent.putExtra("name", device.getName());
                        chatIntent.putExtra("blueId", device.getBlueId());
                        chatIntent.putExtra("profileImage", device.getProfileImage());
                        chatIntent.putExtra("connected", true);
                        context.startActivity(chatIntent);
                    }
                }
                else if (Objects.equals(intent.getAction(), "call.send")) {
                    Device device = devices.get(intent.getIntExtra("devicePosition", -1));
                    if (device != null) {
                        Intent callIntent = new Intent(context, VoiceCallActivity.class);
                        callIntent.putExtra("deviceName", device.getName());
                        callIntent.putExtra("blueId", device.getBlueId());
                        callIntent.putExtra("profileImage", device.getProfileImage());
                        callIntent.putExtra("city", device.getCity());
                        callIntent.putExtra("isUnknown", false);
                        context.startActivity(callIntent);
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
        int devicePosition = holder.getBindingAdapterPosition();
        String statusText = device.getBlueId() + " " + ((device.getCity() != null) ? device.getCity() : "");

        if (device.getBlueId().equals(ThisDevice.getDevice().getBlueId())) {
            holder.binding.deviceName.setText(R.string.device_header);
            holder.binding.status.setText(device.getBlueId());
            holder.binding.deviceImage.setVisibility(View.VISIBLE);
            holder.binding.profileImage.setVisibility(View.INVISIBLE);
            holder.binding.videoCall.setVisibility(View.GONE);
            holder.binding.voiceCall.setVisibility(View.GONE);
        }

        else {
            holder.binding.deviceName.setText(device.getName());
            holder.binding.status.setText(statusText);

            Glide.with(context)
                    .load(Uri.parse(device.getProfileImage()))
                    .placeholder(R.drawable.avatar)
                    .into(holder.binding.profileImage);

            holder.itemView.setOnClickListener(v -> {
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
//            if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { }
//                boolean success = bluetoothDevice.createBond();
//                if (success) {
//                    Toast.makeText(context, "Paired to " + device.getName(), Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(context, "Pairing failed!", Toast.LENGTH_SHORT).show();
//                }
//            }

                if (bluetoothAdapter.isEnabled()) {
                    ClientClass clientClass = new ClientClass(bluetoothDevice, context, handler, devicePosition, bluetoothAdapter);
                    clientClass.setFunctionToExecute(ClientClass.Function.CONNECT);
                    ClientClassHolder.setClientInstance(clientClass);
                    ClientClassHolder.getInstance().start();
                }
                else {
                    Intent chatIntent = new Intent(context, ChatActivity.class);
                    chatIntent.putExtra("name", device.getName());
                    chatIntent.putExtra("blueId", device.getBlueId());
                    chatIntent.putExtra("profileImage", device.getProfileImage());
                    chatIntent.putExtra("connected", false);
                    context.startActivity(chatIntent);
                }
            });
        }

        holder.binding.voiceCall.setOnClickListener(v -> {
            if (bluetoothAdapter.isEnabled()) {
                ClientClass clientClass = new ClientClass(bluetoothDevice, context, handler, devicePosition, bluetoothAdapter);
                clientClass.setFunctionToExecute(ClientClass.Function.CALL);
                ClientClassHolder.setClientInstance(clientClass);
                ClientClassHolder.getInstance().start();
            }
            else {
                Toast.makeText(context, "Bluetooth is not connected", Toast.LENGTH_SHORT).show();
            }
        });

        holder.binding.videoCall.setOnClickListener(v -> {

        });

        holder.itemView.setOnCreateContextMenuListener(this);
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(v.getContext());
        Device selectedDevice = devices.get(contextItemPosition);
        menu.setHeaderTitle(selectedDevice.getBlueId());
        if (selectedDevice.getBlueId().equals(ThisDevice.getDevice().getBlueId())) {
            inflater.inflate(R.menu.this_contact_menu, menu);
        } else {
            inflater.inflate(R.menu.contact_menu, menu);

        }
    }

    public void copyNumber(Device device) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(device.getName(), device.getBlueId());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(context, "Number copied", Toast.LENGTH_SHORT).show();
    }

    public interface RecyclerViewClickListener
    {
        int recyclerViewListClicked(int position);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class DevicesViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        RowDevicesBinding binding;
        public DevicesViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowDevicesBinding.bind(itemView);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            contextItemPosition = this.getBindingAdapterPosition();
            recyclerViewClickListener.recyclerViewListClicked(this.getBindingAdapterPosition());
            return false;
        }
    }
}
