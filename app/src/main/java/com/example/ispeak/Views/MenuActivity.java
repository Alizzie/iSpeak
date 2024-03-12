/*
 * Elisa D.
 */

package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ispeak.Interfaces.IntentHandler;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.R;
import com.example.ispeak.databinding.ActivityMenuBinding;

public class MenuActivity extends AppCompatActivity implements IntentHandler {

    ActivityMenuBinding binding;
    Patient patientInfo;
    BoDyS selectedAssessment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        listenBoDysAssessment();
    }

    private void init(){
        retrieveIntent(this);
        patientInfo = Patient.getInstance();
        setPatientInfoDisplay();
    }

    private void setPatientInfoDisplay(){
        binding.patientId.setText(getResources().getString(R.string.patientData, patientInfo.getPatientId()));
        binding.caseId.setText(getResources().getString(R.string.caseData, patientInfo.getCaseId()));
        binding.patientDiagnosis.setText(patientInfo.getDiagnosis());
        binding.caseDate.setText(patientInfo.getFormattedDate());
    }

    private void listenBoDysAssessment() {
        selectedAssessment = new BoDyS(patientInfo.getPatientFolderPath());
        binding.assessmentBtn.setOnClickListener(view -> navigateToNextActivity(this, BoDySMenuActivity.class));
    }

    @Override
    public void prepareIntent(Intent intent) {
        intent.putExtra("assessment", selectedAssessment);
    }

    @Override
    public void processReceivedIntent(Intent intent) {

    }
}
