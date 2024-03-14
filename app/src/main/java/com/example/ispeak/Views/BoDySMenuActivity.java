package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ispeak.Interfaces.IntentHandler;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.databinding.ActivityBodysMenuBinding;

public class BoDySMenuActivity extends AppCompatActivity implements IntentHandler {

    ActivityBodysMenuBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBodysMenuBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        retrieveIntent(this);
        listenBtnNewAssessment();
    }

    private void listenBtnNewAssessment(){
        BoDyS boDyS = new BoDyS();
        Patient.getInstance().addAssessment(boDyS);
        binding.newAssessment.setOnClickListener(view -> navigateToNextActivity(this, MicrophoneConnectionActivity.class));
    }

    @Override
    public void prepareIntent(Intent intent) {
        int assessmentNr = Patient.getInstance().getAssessmentList().size() - 1;
        intent.putExtra("assessmentNr", assessmentNr);
    }

    @Override
    public void processReceivedIntent(Intent intent) {
    }
}
