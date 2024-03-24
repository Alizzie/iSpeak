package com.example.ispeak.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.ispeak.R;
import com.masoudss.lib.WaveformSeekBar;

import java.io.File;
import java.io.IOException;

public class WaveformSeekbar extends WaveformSeekBar {
    
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private String audioPath;
    private TextView audioTime, audioDuration;
    private ImageView playBtn;
    private Drawable playImg, pauseImg;
    private boolean isPaused, hasStarted;
    
    public WaveformSeekbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveformSeekbar(@NonNull Context context) {
        super(context);
    }

    public WaveformSeekbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    public void init(String audioPath, TextView audioTime, TextView audioDuration, ImageView playBtn){
        this.audioDuration = audioDuration;
        this.audioTime = audioTime;
        this.audioPath = audioPath;
        this.handler = new Handler();
        this.playBtn = playBtn;
        this.playImg = ContextCompat.getDrawable(getContext(), R.drawable.play_24);
        this.pauseImg = ContextCompat.getDrawable(getContext(), R.drawable.pause_24);
        this.isPaused = false;
        this.hasStarted = false;
        
        initMediaPlayer();
        initWaveformBar();
    }
    
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initWaveformBar(){
        this.setSampleFrom(new File(audioPath));
        this.setMaxProgress(mediaPlayer.getDuration());

        if(audioTime == null || audioDuration == null) {
            return;
        }
        
        audioDuration.setText(Utils.formatTime((long) mediaPlayer.getDuration()));
        audioTime.setText(Utils.formatTime((long) this.getProgress()));

        this.setOnProgressChanged((waveformSeekBar, progress, fromUser) -> {
            this.audioTime.setText(Utils.formatTime((long) progress));
        });
    }

    private void updateProgress(){

        if(mediaPlayer.isPlaying() && !isPaused){
            this.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(this::updateProgress, 250);
            playBtn.setBackground(pauseImg);
        } else {
            handler.removeCallbacksAndMessages(null);
            playBtn.setBackground(playImg);
        }
    }

    public void changeAudioPlayback(int direction, ImageButton playBtn) {

        if(!hasStarted) {
            return;
        }

        mediaPlayer.pause();
        isPaused = true;
        playBtn.setBackground(playImg);

        int newTime = Math.max(mediaPlayer.getCurrentPosition() + direction * 100, 0);
        newTime = Math.min(mediaPlayer.getDuration(), newTime);

        mediaPlayer.seekTo(newTime);
        this.setProgress(mediaPlayer.getCurrentPosition());
    }

    public void startWaveformAudio() {

        hasStarted = true;

        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
        } else {
            mediaPlayer.start();
            isPaused = false;
        }

        updateProgress();
    }

    public boolean isPaused() {
        return isPaused;
    }
}
