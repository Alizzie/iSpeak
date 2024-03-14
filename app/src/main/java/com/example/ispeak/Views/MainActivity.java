package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ispeak.Interfaces.IntentHandler;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.databinding.ActivityMainBinding;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements IntentHandler {
    ActivityMainBinding binding;
    Patient patientInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        listenBtnConfirm();
        initRadioGroup();
    }

    private void listenBtnConfirm() {
        binding.patientRegisterBtn.setOnClickListener(view -> {
            String patientId = Objects.requireNonNull(binding.patientIdInput.getText()).toString().trim();
            String caseId = Objects.requireNonNull(binding.caseIdInput.getText()).toString().trim();
            int diagnosisId = binding.radioGroupDiagnosis.getCheckedRadioButtonId();

            boolean correctInputLength = checkIdLength(patientId, caseId);
            String diagnosis = retrieveDiagnosis(diagnosisId);

            if (correctInputLength && diagnosis != "NaN") {
                patientInfo = Patient.getInstance(patientId, caseId, diagnosis, this);

                navigateToNextActivity(this, MenuActivity.class);
            }


//            patientInfo = Patient.getInstance(patientId, caseId, diagnosis, getApplicationContext());
//            navigateToNextActivity(this, MenuActivity.class);
        });
    }

    private boolean checkIdLength(String patientId, String caseId){
        boolean correctInput = true;

        if (patientId.length() != 7) {
            binding.patientIdInput.setError("Patient Id not correct");
            correctInput = false;
        }

        if (caseId.length() != 7) {
            binding.caseIdInput.setError("Case Id not correct");
            correctInput = false;
        }

        return correctInput;
    }

    private String retrieveDiagnosis(int diagnosis) {

        if (diagnosis == binding.radioBtnStroke.getId()){
            return "Stroke";
        } else if (diagnosis == binding.radioBtnPakinson.getId()){
            return "Pakinson";
        } else if (diagnosis == binding.radioBtnOther.getId()){
            return "Other";
        } else {
            binding.diagnosisTextInput.setError("Select diagnosis");
            return "NaN";
        }
    }

    private void initRadioGroup() {
        binding.radioGroupDiagnosis.setOnCheckedChangeListener((radioGroup, i) -> binding.diagnosisTextInput.setError(null));
    }


    @Override
    public void prepareIntent(Intent intent) {
    }

    @Override
    public void processReceivedIntent(Intent intent) {

    }
}