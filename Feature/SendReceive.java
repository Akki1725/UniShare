package com.michaelians.bluteeth.Feature;

import static com.michaelians.bluteeth.Activity.ChatActivity.RECEIVED_IMG;
import static com.michaelians.bluteeth.Activity.ChatActivity.RECEIVED_TXT;
import static com.michaelians.bluteeth.Feature.Call.RECEIVED_AUDIO;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
public class SendReceive extends Thread {

    String senderBlueId;
    Function function;
    Handler uiHandler;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public enum Function {
        READ,
        READ_AUDIO
    }

    public SendReceive(BluetoothSocket socket, Handler uiHandler, String senderBlueId) {
        this.uiHandler = uiHandler;
        this.function = Function.READ;
        this.senderBlueId = senderBlueId;

        InputStream insTemp = null;
        OutputStream outsTemp = null;
        try {
            insTemp = socket.getInputStream();
            outsTemp = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputStream = insTemp;
        outputStream = outsTemp;
    }

    public void setFunctionToExecute(SendReceive.Function function) {
        this.function = function;
    }


    public void run() {
        switch (function){
            case READ:
                read();
                break;
            case READ_AUDIO:
                readAudio();
                break;
            default:
                break;
        }
    }

    void read() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = inputStream.read(buffer);
                if (bytes == -1) {
                    break;
                }

                String receivedMessage = new String(buffer, 0, bytes);
                if (receivedMessage.startsWith("TXT")) {
                    byte[] message = Arrays.copyOfRange(buffer, 3, bytes);
                    uiHandler.obtainMessage(RECEIVED_TXT, message.length, -1, message).sendToTarget();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void readAudio() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = inputStream.read(buffer);
                if (bytes == -1) {
                    break;
                }

                String receivedMessage = new String(buffer, 0, bytes);
                if (receivedMessage.startsWith("AUDIO")) {
                    byte[] audioData = Arrays.copyOfRange(buffer, 5, bytes);
                    int dataSize = bytes - 5;
                    uiHandler.obtainMessage(RECEIVED_AUDIO, dataSize, -1, audioData).sendToTarget();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAudioData(byte[] audioData) {
        try {
            byte[] header = "AUDIO".getBytes();
            byte[] dataToSend = new byte[header.length + audioData.length];
            System.arraycopy(header, 0, dataToSend, 0, header.length);
            System.arraycopy(audioData, 0, dataToSend, header.length, audioData.length);
            outputStream.write(dataToSend);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
