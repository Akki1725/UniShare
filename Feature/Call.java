package com.michaelians.bluteeth.Feature;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.michaelians.bluteeth.Singleton.BluetoothSocketHolder;

import java.util.ArrayList;
import java.util.List;

public class Call extends Thread {
    Context context;
    AudioRecord audioRecord;
    AudioTrack audioTrack;
    MediaPlayer mediaPlayer;
    List<Integer> byteSizeList;
    Handler handler;
    SendReceive sendReceive;
    String senderId;
    List<byte[]> audioBufferList;
    boolean isRecording = false;
    final int audioSource = MediaRecorder.AudioSource.MIC;
    public static final int RECEIVED_AUDIO = 1;
    final int sampleRate = 16000;
    final int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    final int audioFormat = AudioFormat.ENCODING_PCM_8BIT;
    int bufferSize;

    public Call(Context context, String senderId) {
        this.context = context;
        this.senderId = senderId;

        audioBufferList = new ArrayList<>();
        byteSizeList = new ArrayList<>();
        mediaPlayer = new MediaPlayer();
        bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, 24);
        }


        audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize);
        int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioTrack.MODE_STREAM, sampleRate, AudioFormat.CHANNEL_OUT_MONO, audioFormat, minBufferSize, AudioTrack.MODE_STREAM);

        handler = new Handler(msg -> {
            switch (msg.what) {
                case RECEIVED_AUDIO:
                    byte[] audioBuffer = (byte[]) msg.obj;
                    int readSize = msg.arg1;

                    if (audioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
                        return false;
                    }
                    audioTrack.play();
                    audioTrack.write(audioBuffer, 0, readSize);
                    break;
            }
            return true;
        });

        sendReceive = new SendReceive(BluetoothSocketHolder.getSocket(), handler, senderId);
    }

    public void run() {
        if (BluetoothSocketHolder.isClient()) {
            recordAudio();
        } else {
            sendReceive.setFunctionToExecute(SendReceive.Function.READ_AUDIO);
            sendReceive.start();
        }

    }

    void recordAudio() {
        if (isRecording) {
            return;
        }

        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e("AudioStreamingThread", "AudioRecord is not initialized.");
            return;
        }

        if (audioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
            Log.e("AudioStreamingThread", "AudioTrack is not initialized.");
            return;
        }

        audioRecord.startRecording();
        isRecording = true;
        Log.d("AudioStreamingThread", "Start recording and playing audio...");

        byte[] audioBuffer = new byte[bufferSize];
        int chunkSize = 1024;
        byte[] chunk = new byte[chunkSize];
        int chunkIndex = 0;

        while (isRecording) {
            int readSize = audioRecord.read(audioBuffer, 0, bufferSize);

            if (readSize > 0) {
                for (int i = 0; i < readSize; i++) {
                    chunk[chunkIndex] = audioBuffer[i];
                    chunkIndex++;

                    if (chunkIndex == chunkSize) {
                        sendReceive.sendAudioData(chunk);

                        chunk = new byte[chunkSize];
                        chunkIndex = 0;
                    }
                }
            }
        }


        Log.d("AudioStreamingThread", "Stop recording and playing audio...");
        audioRecord.stop();
        audioRecord.release();
    }

    public void stopStreaming() {
        isRecording = false;
    }

    public void playAudio() {
        if (audioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
            Log.e("AudioStreamingThread", "AudioTrack is not initialized.");
            return;
        }

        audioTrack.play();

        while (!audioBufferList.isEmpty()) {
            byte[] audioData = audioBufferList.remove(0);
            int dataSize = byteSizeList.remove(0);
            audioTrack.write(audioData, 0, dataSize);
        }

        audioTrack.stop();
        audioTrack.flush();
    }
}
