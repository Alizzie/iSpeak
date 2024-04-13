package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import com.example.ispeak.Interfaces.IntentHandler;
import com.example.ispeak.Models.Assessment;
import com.example.ispeak.R;
import com.example.ispeak.Utils.AssessmentFactory;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.Utils.Utils;
import com.example.ispeak.databinding.ActivityMainBinding;

import java.io.File;
import java.util.Objects;


public class MainActivity extends BaseApp {
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

            if (correctInputLength && !diagnosis.equals("NaN")) {
                patientInfo = Patient.getInstance();
                patientInfo.setPatientData(patientId, caseId, diagnosis, this);

                restoreAssessments();
                navigateToNextActivity(this, MenuActivity.class);
            }


//            patientInfo = Patient.getInstance(patientId, caseId, diagnosis, getApplicationContext());
//            navigateToNextActivity(this, MenuActivity.class);
        });
    }

    public void restoreAssessments() {
        File[] assessmentFiles = Utils.getFilesFromInternalStorageFolder(Patient.getInstance().getPatientFolderPath());
        if(assessmentFiles != null) {
            for(File file : assessmentFiles) {
                String fileName = file.getName();
                AssessmentFactory.AssessmentNames assessmentName = AssessmentFactory.AssessmentNames.valueOf(fileName);
                Log.d("TESTFILES", String.valueOf(assessmentName));
                Assessment assessment = AssessmentFactory.createAssessment(assessmentName);

                if(assessment != null) {
                    boolean existing = assessment.retrieveAssessment(file);

                    if(existing){
                        Patient.getInstance().addAssessment(assessment);
                    }
                }
            }
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}