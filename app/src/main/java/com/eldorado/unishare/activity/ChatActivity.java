package com.eldorado.unishare.activity;

import static android.view.WindowInsetsAnimation.Callback.DISPATCH_MODE_STOP;

import static com.eldorado.unishare.activity.SettingsActivity.updateSystemUiVisibility;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsAnimationCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eldorado.unishare.R;
import com.eldorado.unishare.adapter.MessagesAdapter;
import com.eldorado.unishare.databinding.ActivityChatBinding;
import com.eldorado.unishare.feature.SendReceive;
import com.eldorado.unishare.model.Message;
import com.eldorado.unishare.singleton.BluetoothSocketHolder;
import com.eldorado.unishare.singleton.ReceiverInstanceHolder;
import com.eldorado.unishare.singleton.ThisDevice;
import com.google.android.material.transition.platform.MaterialFade;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    public static final int RECEIVED_TXT = 5;
    public static final int RECEIVED_IMG = 6;

    ActivityChatBinding binding;
    ActivityResultLauncher<String> getContentLauncher;
    String deviceName, senderBlueId, clientBlueId;
    Handler handler;
    SendReceive sendReceive;
    MessagesAdapter adapter;
    ArrayList<Message> messages;
    boolean isKeyboardOpen = false;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateSystemUiVisibility(getWindow());
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.userToolbar);

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
        clientBlueId = getIntent().getStringExtra("blueId");
        senderBlueId = ThisDevice.getDevice().getBlueId();
        BluetoothSocket socket = BluetoothSocketHolder.getSocket();
        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this, messages, senderBlueId, clientBlueId);
        binding.chatView.setAdapter(adapter);
        binding.chatView.setLayoutManager(new LinearLayoutManager(this));
        scrollToBottom(false);

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
                    Message message = new Message(msgTemp, receiverId);
                    messages.add(message);

                    for (int i = 0; i < messages.size(); i++) {
                        Message currentMsg = messages.get(i);

                        String lastMsgId = "";
                        String nextMsgId = "";

                        if (i > 0) {
                            Message lastMsg = messages.get(i - 1);
                            lastMsgId = lastMsg.getSenderId();
                        }

                        if (i < messages.size() - 1) {
                            Message nextMsg = messages.get(i + 1);
                            nextMsgId = nextMsg.getSenderId();
                        }

                        currentMsg.setLastMsgId(lastMsgId);
                        currentMsg.setNextMsgId(nextMsgId);
                    }

                    adapter.notifyDataSetChanged();
                    scrollToBottom(false);
                    break;
            }

            return true;
        });

        sendReceive = new SendReceive(socket, handler, clientBlueId);
        sendReceive.setFunctionToExecute(SendReceive.Function.READ);
        ReceiverInstanceHolder.setInstance(sendReceive);
        ReceiverInstanceHolder.getInstance().start(); /**/

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

        View chatView = binding.chatView;

        WindowInsetsAnimationCompat.Callback softKeyboardAnimation = new WindowInsetsAnimationCompat.Callback(WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_STOP) {
            float start = 0f;
            float end = 0f;

            @Override
            public void onPrepare(WindowInsetsAnimationCompat animation) {
                start = chatView.getBottom();
            }

            @Override
            public WindowInsetsAnimationCompat.BoundsCompat onStart(WindowInsetsAnimationCompat animation, WindowInsetsAnimationCompat.BoundsCompat bounds) {
                end = chatView.getBottom();
                chatView.setTranslationY(start - end);
                return bounds;
            }

            @Override
            public WindowInsetsCompat onProgress(WindowInsetsCompat insets, List<WindowInsetsAnimationCompat> runningAnimations) {
                WindowInsetsAnimationCompat imeAnimation = null;
                for (WindowInsetsAnimationCompat anim : runningAnimations) {
                    if ((anim.getTypeMask() & WindowInsetsCompat.Type.ime()) != 0) {
                        imeAnimation = anim;
                        break;
                    }
                }

                if (imeAnimation == null) {
                    return insets;
                }

                float offset = (start - end) * (1 - imeAnimation.getInterpolatedFraction());

                chatView.setTranslationY(offset);
                return insets;
            }
        };

        ViewCompat.setWindowInsetsAnimationCallback(chatView, softKeyboardAnimation);

        binding.msgBox.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (binding.msgBox.isFocused() && TextUtils.isEmpty(binding.msgBox.getText())) {
                    binding.msgBox.clearFocus();
                    return true;
                }
            }
            return false;
        });

        binding.msgBox.setOnFocusChangeListener((v, hasFocus) -> {
            MaterialFade materialFade = new MaterialFade();
            materialFade.setDuration(200);

            TransitionManager.beginDelayedTransition(binding.chatLayout, materialFade);

            binding.sendBtn.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
            binding.attachmentBtn.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
            binding.emojiBtn.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
        });

        binding.msgBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            } else {
                return false;
            }
        });


        binding.sendBtn.setOnClickListener(v -> {
            sendMessage();
        });

        binding.attachmentBtn.setOnClickListener(v -> Toast.makeText(this, "I'll implement this later!", Toast.LENGTH_SHORT).show());
    }

    void sendMessage() {
        String messageTxt = Objects.requireNonNull(binding.msgBox.getText()).toString();

        if (messageTxt.isEmpty()) {
            return;
        }

        if (sendReceive != null) {
            String header = "TXT";
            byte[] messageBytes = (header + messageTxt).getBytes();
            sendReceive.write(messageBytes);
        }

        Message msg = new Message(messageTxt, senderBlueId);
        messages.add(msg);

        for (int i = 0; i < messages.size(); i++) {
            Message currentMsg = messages.get(i);

            String lastMsgId = "";
            String nextMsgId = "";

            if (i > 0) {
                Message lastMsg = messages.get(i - 1);
                lastMsgId = lastMsg.getSenderId();
            }

            if (i < messages.size() - 1) {
                Message nextMsg = messages.get(i + 1);
                nextMsgId = nextMsg.getSenderId();
            }

            currentMsg.setLastMsgId(lastMsgId);
            currentMsg.setNextMsgId(nextMsgId);
        }

        adapter.notifyDataSetChanged();
        scrollToBottom(false);
        binding.msgBox.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    void scrollToBottom(Boolean animate) {

        if (animate) {
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