package fr.thomas.menard.ispeak.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.core.app.ActivityCompat;

public class AudioVisualizer {

    private AudioRecord audioRecord;
    private int bufferSize;
    private boolean isRecording = false;

    private WaveformView waveformView;




    public AudioVisualizer(WaveformView view, Context context) {
        int minBufferSize = AudioRecord.getMinBufferSize(
                44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        bufferSize = Math.max(minBufferSize, 8192); // Utilisez une taille de buffer plus grande si nécessaire
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            /*
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
            waveformView = view;

             */
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        waveformView = view;
    }
    /*

    public void startRecording() {
        isRecording = true;
        audioRecord.startRecording();
        byte[] buffer = new byte[bufferSize];
        while (isRecording) {
            int bytesRead = audioRecord.read(buffer, 0, buffer.length);
            waveformView.updateWaveform(buffer);
        }
    }

    public void stopRecording() {
        isRecording = false;
        audioRecord.stop();
        audioRecord.release();
    }

     */
}
