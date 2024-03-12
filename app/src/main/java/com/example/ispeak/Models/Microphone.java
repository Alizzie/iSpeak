package com.example.ispeak.Models;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.ispeak.Utils.WaveformView;

import java.io.IOException;
import java.util.Arrays;

public class Microphone {

    private AudioManager audioManager;
    private AudioRecord audioRecord;
    private MediaRecorder mediaRecorder;
    private byte[] audioBuffer = new byte[4000];
    private boolean isPaused = false;
    private boolean isActive = false;

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    private Context context;
    private Activity activity;

    public Microphone(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public boolean connectMicrophone() {
        boolean ONLYDIVAS = true;
        if (!isMicroPresent() || !ONLYDIVAS) {
            return false;
        }

        return checkDIVASMic();
    }

    private void checkMicroPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            getMicroPermission();
        }
    }

    private void getMicroPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 200);
    }

    private boolean isMicroPresent() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private boolean checkDIVASMic() {
        AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);

        // TODO: Set false after deploy
        boolean externalMicro = true;

        for (AudioDeviceInfo device : audioDevices) {
            int deviceTyp = device.getType();

            if (deviceTyp == AudioDeviceInfo.TYPE_USB_HEADSET) {
                externalMicro = true;
                audioRecord.setPreferredDevice(device);
                break;
            }
        }

        return externalMicro;
    }

    public void startRecording(WaveformView waveformView, String outputAudio) {
        isActive = true;
        isPaused = false;
        startMediaPlayer(outputAudio);
        startAudioRecorder(waveformView);
    }

    public void stopRecording(){
        isActive = false;
        Log.d("PLAYBACKK", String.valueOf(mediaRecorder == null));
        releaseMediaRecorder();
        releaseAudioRecorder();
    }

    public void pauseRecording(){
        if(!isPaused){
            mediaRecorder.pause();
            isPaused = true;
        }
    }

    public void resumeRecording(WaveformView waveformView) {
        if(isPaused){
            mediaRecorder.resume();
            isPaused = false;
        }

        visualizeRecording(waveformView);
    }

    private void startMediaPlayer(String outputAudio){
        prepareMediaRecorder(outputAudio);
        mediaRecorder.start();
    }

    private void startAudioRecorder(WaveformView waveformView) {
        prepareAudioRecorder();
        audioRecord.startRecording();

        visualizeRecording(waveformView);
    }

    private void visualizeRecording(WaveformView waveformView) {
        new Thread(() -> {
            try {
                while (!isPaused && isActive) {
                    int bytesRead = audioRecord.read(audioBuffer, 0, BUFFER_SIZE);
                    if (bytesRead > 0) {
                        // Update the waveform view with the new audio data
                        activity.runOnUiThread(() -> waveformView.setWaveform(Arrays.copyOf(audioBuffer, bytesRead)));
                    } else {
                        Log.e("AudioRecord", "Error reading audio data");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void prepareMediaRecorder(String outputAudio){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(outputAudio);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareAudioRecorder(){
        checkMicroPermission();
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
    }

    private void releaseMediaRecorder(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void releaseAudioRecorder(){
        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            audioRecord.release();
        }
    }

    public boolean isActive(){
        return isActive;
    }

    public boolean isPaused() {return isPaused;}

}
