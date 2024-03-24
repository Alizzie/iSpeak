package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ispeak.Interfaces.IntentHandler;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.Microphone;
import com.example.ispeak.R;
import com.example.ispeak.databinding.ActivityMicrophoneConnectionBinding;

public class MicrophoneConnectionActivity extends AppCompatActivity implements IntentHandler {

    private ActivityMicrophoneConnectionBinding binding;
    private Microphone microphone;
    private int assessmentNr, assessmentTaskId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMicrophoneConnectionBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        listenBtnCheckMicrophoneConnection();
        listenBtnStartRecordingActivity();

    }

    private void init(){
        retrieveIntent(this);
        microphone = new Microphone(this);
    }

    private void listenBtnCheckMicrophoneConnection(){
        binding.btnCheckMicroConnection.setOnClickListener(view -> updateConnectionMessage());
    }

    private void listenBtnStartRecordingActivity(){
        binding.btnStartRecordingPageActivated.setOnClickListener(view -> navigateToNextActivity(this, RecordingActivity.class));
    }

    private void updateConnectionMessage(){
        boolean connected = microphone.connectMicrophone();
        setConnectionMessage(connected);
        setNextBtn(connected);
    }

    private void setConnectionMessage(boolean connected){
        if (connected) {
            binding.connectionTxt.setText(getResources().getString(R.string.divasConnected));
            binding.connectionTxt.setTextColor(getResources().getColor(R.color.green, getTheme()));
        } else {
            binding.connectionTxt.setText(getResources().getString(R.string.divasNotConnected));
            binding.connectionTxt.setTextColor(getResources().getColor(R.color.red, getTheme()));
        }
    }

    private void setNextBtn(boolean connected) {
        if (connected) {
            binding.btnStartRecordingPageNotActivated.setVisibility(View.GONE);
            binding.btnStartRecordingPageActivated.setVisibility(View.VISIBLE);
        } else {
            binding.btnStartRecordingPageNotActivated.setVisibility(View.VISIBLE);
            binding.btnStartRecordingPageActivated.setVisibility(View.GONE);
        }
    }

    @Override
    public void prepareIntent(Intent intent) {
        intent.putExtra("assessmentNr", assessmentNr);
        intent.putExtra("assessmentTaskId", assessmentTaskId);
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        assessmentNr = intent.getIntExtra("assessmentNr", -1);
        assessmentTaskId = intent.getIntExtra("assessmentTaskId", 0);
    }
}
