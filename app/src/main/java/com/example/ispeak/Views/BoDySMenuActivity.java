package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ispeak.Interfaces.IntentHandler;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.databinding.ActivityBodysMenuBinding;

public class BoDySMenuActivity extends AppCompatActivity implements IntentHandler {

    ActivityBodysMenuBinding binding;
    BoDyS assessment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBodysMenuBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        retrieveIntent(this);
        listenBtnNewAssessment();
    }

    private void listenBtnNewAssessment(){
        binding.newAssessment.setOnClickListener(view -> navigateToNextActivity(this, MicrophoneConnectionActivity.class));
    }

    @Override
    public void prepareIntent(Intent intent) {
        intent.putExtra("assessment", assessment);
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        assessment = intent.getParcelableExtra("assessment");
        Log.d("ASSESSMENTT", String.valueOf(assessment));
    }
}
