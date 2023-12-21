package com.eldorado.unishare.adapter;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.eldorado.unishare.R;
import com.eldorado.unishare.activity.MainActivity;
import com.eldorado.unishare.databinding.RowDevicesBinding;
import com.eldorado.unishare.activity.ChatActivity;
import com.eldorado.unishare.activity.VoiceCallActivity;
import com.eldorado.unishare.feature.ClientClass;
import com.eldorado.unishare.model.Device;
import com.eldorado.unishare.singleton.ClientClassHolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder> implements View.OnCreateContextMenuListener {

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
                if (Objects.equals(intent.getAction(), "status.connected")) {
                    Device device = devices.get(intent.getIntExtra("devicePosition", -1));
                    if (device != null) {
                        Intent chatIntent = new Intent(context, ChatActivity.class);
                        chatIntent.putExtra("name", device.getName());
                        chatIntent.putExtra("blueId", device.getBlueId());
                        context.startActivity(chatIntent);
                    }
                } else if (Objects.equals(intent.getAction(), "call.send")) {
                    Device device = devices.get(intent.getIntExtra("devicePosition", -1));
                    if (device != null) {
                        Intent callIntent = new Intent(context, VoiceCallActivity.class);
                        callIntent.putExtra("deviceName", device.getName());
                        callIntent.putExtra("blueId", device.getBlueId());
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

        holder.binding.deviceName.setText(device.getName());

        holder.binding.status.setText(device.getBlueId());

//        if (!device.getProfileImage().isEmpty()) {
//            Glide.with(context)
//                    .load(device.getProfileImage())
//                    .placeholder(R.drawable.avatar)
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .into(holder.binding.profileImage);
//        }

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
            if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
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

//    public boolean showPopupMenu(View v, Device device, int position) {
//        PopupMenu popupMenu = new PopupMenu(context, v);
//        MenuInflater inflater = popupMenu.getMenuInflater();
//        inflater.inflate(R.menu.contact_menu, popupMenu.getMenu());
//        popupMenu.show();
//        MenuItem title = popupMenu.getMenu().findItem(R.id.blueId);
//        title.setTitle(device.getBlueId());
//
//        boolean[] itemUsed = {false};
//
//        popupMenu.setOnMenuItemClickListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == R.id.save) {
////                ArrayList<Device> savedContactsList = callback.loadDevicesFromStorage();
////                Device newContact = savedContactsList != null ? savedContactsList.get(position) : null;
////                final String[] firstName = new String[1];
////                final String[] lastName = new String[1];
////                final String[] address = new String[1];
////                final String[] city = new String[1];
////                final String[] zip = new String[1];
////                View layout = LayoutInflater.from(context).inflate(R.layout.dialog_save_contact, null);
////                TextInputEditText firstNameText = layout.findViewById(R.id.first_name);
////                TextInputEditText lastNameText = layout.findViewById(R.id.last_name);
////                TextInputEditText blueIdText = layout.findViewById(R.id.blue_id);
////                TextInputEditText addressText = layout.findViewById(R.id.address);
////                TextInputEditText cityText = layout.findViewById(R.id.city);
////                TextInputEditText zipText = layout.findViewById(R.id.zip);
////
////                firstNameText.setText(newContact != null ? newContact.getFirstName() : "");
////                lastNameText.setText(newContact != null ? newContact.getLastName() : "");
////                blueIdText.setText(blueId);
////                addressText.setText(newContact != null ? newContact.getAddress() : "");
////                cityText.setText(newContact != null ? newContact.getCity() : "");
////                zipText.setText(newContact != null ? newContact.getZip() : "");
////
////                AlertDialog alertDialog = new MaterialAlertDialogBuilder(context)
////                        .setTitle("Add to contacts")
////                        .setView(layout)
////                        .setPositiveButton("Save", (dialog, which) -> {
////                            firstName[0] = firstNameText.getText().toString();
////                            lastName[0] = lastNameText.getText().toString();
////                            address[0] = addressText.getText().toString();
////                            city[0] = cityText.getText().toString();
////                            zip[0] = zipText.getText().toString();
////
////                            if (newContact != null) {
////                                newContact.setFirstName(firstName[0]);
////                                newContact.setLastName(lastName[0]);
////                                newContact.setAddress(address[0]);
////                                newContact.setCity(city[0]);
////                                newContact.setZip(zip[0]);
////                            }
////
////                            if (savedContactsList != null) {
////                                callback.saveDevicesToStorage(savedContactsList);
////                                callback.updateDataset(savedContactsList);
////                            }
////
////                            dialog.dismiss();
////                        })
////                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
////                        .create();
////
////                alertDialog.show();
//                itemUsed[0] = true;
//            }
//
//            if (itemId == R.id.copy) {
//                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//                ClipData clipData = ClipData.newPlainText(device.getName(), device.getBlueId());
//                clipboardManager.setPrimaryClip(clipData);
//                Toast.makeText(context, "Number copied", Toast.LENGTH_SHORT).show();
//                itemUsed[0] = true;
//                return true;
//            }
//
//            if (itemId == R.id.block) {
//                itemUsed[0] = true;
//                return true;
//            }
//
//            if (itemId == R.id.delete) {
//                itemUsed[0] = true;
//                return true;
//            }
//
//            else {
//                itemUsed[0] = false;
//                return false;
//            }
//        });
//
//        return itemUsed[0];
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = new MenuInflater(v.getContext());
        menu.setHeaderTitle("Contact");
        inflater.inflate(R.menu.contact_menu, menu);
    }

    public void copyNumber(Device device) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(device.getName(), device.getBlueId());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(context, "Number copied", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class DevicesViewHolder extends RecyclerView.ViewHolder {

        RowDevicesBinding binding;
        public DevicesViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowDevicesBinding.bind(itemView);
        }
    }
}
