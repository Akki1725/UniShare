package com.michaelians.bluteeth.Activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.michaelians.bluteeth.Adapter.MessagesAdapter;
import com.michaelians.bluteeth.Feature.SendReceive;
import com.michaelians.bluteeth.Model.Message;
import com.michaelians.bluteeth.R;
import com.michaelians.bluteeth.Singleton.BluetoothSocketHolder;
import com.michaelians.bluteeth.Singleton.ReceiverInstanceHolder;
import com.michaelians.bluteeth.databinding.ActivityChatBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    ActivityResultLauncher<String> getContentLauncher;
    String deviceName, blueId, senderId;
    Handler handler;
    SendReceive sendReceive;
    MessagesAdapter adapter;
    ArrayList<Message> messages;
    String lastMsgId = "", nextMsgId = "";

    boolean isKeyboardOpen = false;
    public static final int RECEIVED_TXT = 5;
    public static final int RECEIVED_IMG = 6;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.userToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());

                    Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

                    if (fileExtension != null) {
                        fileExtension = fileExtension.toLowerCase();
                        if (fileExtension.equals("png")) {
                            compressFormat = Bitmap.CompressFormat.PNG;
                        }
                    }

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(compressFormat, 50, stream);
                    byte[] imageBytes = stream.toByteArray();

                    String header = "IMG";
                    byte[] headerBytes = header.getBytes();

                    byte[] dataToSend = new byte[headerBytes.length + imageBytes.length];
                    sendReceive.write(dataToSend);

                    Log.d("ChatActivity", "Sending data: " + dataToSend);
                    sendReceive.write(dataToSend);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        deviceName = getIntent().getStringExtra("name");
        blueId = getIntent().getStringExtra("blueId");
        senderId = blueId;
        BluetoothSocket socket = BluetoothSocketHolder.getSocket();
        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this, messages, senderId);
        binding.chatView.setAdapter(adapter);
        binding.chatView.setLayoutManager(new LinearLayoutManager(this));

        binding.chatView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            binding.chatView.getWindowVisibleDisplayFrame(r);
            int screenHeight = binding.chatView.getRootView().getHeight();
            int heightDiff = screenHeight - (r.bottom - r.top);

            int keyboardOpenThreshold = screenHeight / 3;
            boolean isKeyboardOpenNow = heightDiff > keyboardOpenThreshold;

            if (isKeyboardOpenNow && !isKeyboardOpen) {

                isKeyboardOpen = true;
                scrollToBottom(false);
            }

            else if (!isKeyboardOpenNow && isKeyboardOpen) {
                isKeyboardOpen = false;
            }
        });

        binding.chatView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                isKeyboardOpen = false;
                binding.chatView.clearFocus();
            }
        });

        handler = new Handler(msg -> {
            switch (msg.what) {
                case RECEIVED_TXT:
                    byte[] readBuffer = (byte[]) msg.obj;
                    int length = msg.arg1;
                    String receiverId = String.valueOf(msg.arg2);
                    String msgTemp = new String(readBuffer, 0, length);
                    Message message = new Message(msgTemp, "null");
                    messages.add(message);
                    adapter.notifyDataSetChanged();
                    scrollToBottom(false);
                    break;
            }
            return true;
        });

        sendReceive = new SendReceive(socket, handler, senderId);
        sendReceive.setFunctionToExecute(SendReceive.Function.READ);
        sendReceive.start();
        ReceiverInstanceHolder.setInstance(sendReceive);

        binding.userToolbar.setTitle(deviceName);
        binding.userToolbar.setNavigationOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        binding.userToolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.videoCall) {
                Toast.makeText(ChatActivity.this, "Video calling to " + deviceName, Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.voiceCall) {
                Toast.makeText(ChatActivity.this, "Voice calling to " + deviceName, Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        binding.sendBtn.setOnClickListener(v -> {
            String messageTxt = binding.msgBox.getText().toString();

            if (messageTxt.isEmpty()) {
                return;
            }

            if (sendReceive != null) {
                String header = "TXT";
                byte[] messageBytes = (header + messageTxt).getBytes();
                sendReceive.write(messageBytes);
            }

            Message message = new Message(messageTxt, senderId);
            messages.add(message);
            adapter.notifyDataSetChanged();
            scrollToBottom(false);
            binding.msgBox.setText("");
        });

        binding.attachmentBtn.setOnClickListener(v -> Toast.makeText(this, "I'll implement this later!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    void scrollToBottom(Boolean fast) {

        if (fast) {
            if (adapter.getItemCount() > 0) {

                LinearLayoutManager layoutManager = (LinearLayoutManager) binding.chatView.getLayoutManager();
                layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
            }
        }

        else {
            if (adapter.getItemCount() > 0) {
                binding.chatView.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        }
    }
}