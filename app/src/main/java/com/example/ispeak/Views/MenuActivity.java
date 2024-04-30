package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.R;
import com.example.ispeak.databinding.ActivityMenuBinding;

public class MenuActivity extends BaseApp {

    ActivityMenuBinding binding;
    @Override
    public void init(){
        setPatientInfoDisplay();
    }

    @Override
    public void listenBtn(){
        listenBoDysAssessment();
    }

    @Override
    public void setBinding() {
        binding = ActivityMenuBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    private void setPatientInfoDisplay(){
        binding.patientId.setText(getString(R.string.patientData, patientInfo.getPatientId()));
        binding.caseId.setText(getString(R.string.caseData, patientInfo.getCaseId()));
        binding.patientDiagnosis.setText(patientInfo.getDiagnosis());
        binding.caseDate.setText(patientInfo.getFormattedDate());
    }

    private void listenBoDysAssessment() {
        binding.assessmentBtn.setOnClickListener(view -> navigateToNextActivity(this, BoDySMenuActivity.class));
    }

    @Override
    public void prepareIntent(Intent intent) {
    }

    @Override
    public void processReceivedIntent(Intent intent) {

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem homeItem = menu.findItem(R.id.action_return_home);
        if(homeItem!= null) {
            homeItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
