package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ispeak.Models.Microphone;
import com.example.ispeak.R;
import com.example.ispeak.databinding.ActivityMicrophoneConnectionBinding;

public class MicrophoneConnectionActivity extends BaseApp {

    private ActivityMicrophoneConnectionBinding binding;
    private Microphone microphone;
    private int assessmentNr, assessmentTaskId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void init(){
        retrieveIntent(this);
        enableNavBackArrow();
        microphone = new Microphone(this);
    }

    @Override
    public void listenBtn() {
        listenBtnCheckMicrophoneConnection();
        listenBtnStartRecordingActivity();
    }

    @Override
    public void setBinding() {
        binding = ActivityMicrophoneConnectionBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
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
            binding.connectionTxt.setText(getString(R.string.divasConnected));
            binding.connectionTxt.setTextColor(getResources().getColor(R.color.green, getTheme()));
        } else {
            binding.connectionTxt.setText(getString(R.string.divasNotConnected));
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            navigateToNextActivity(this, BoDySMenuActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem menuItem = menu.findItem(R.id.action_skip_task);
        if(menuItem != null){
            menuItem.setVisible(true);
            menuItem.setOnMenuItemClickListener((item) -> {
                navigateToNextActivity(this, RecordingActivity.class);
                return true;
            });
        }

        return super.onPrepareOptionsMenu(menu);
    }
}
