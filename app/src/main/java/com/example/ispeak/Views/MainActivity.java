package com.example.ispeak.Views;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;

import com.example.ispeak.Models.Assessment;
import com.example.ispeak.R;
import com.example.ispeak.Utils.AssessmentFactory;
import com.example.ispeak.Utils.Utils;
import com.example.ispeak.databinding.ActivityMainBinding;

import java.io.File;
import java.util.Objects;


public class MainActivity extends BaseApp {
    private ActivityMainBinding binding;
    private String patientId, caseId, diagnosis;

    private void listenBtnConfirm() {
        binding.patientRegisterBtn.setOnClickListener(view -> {
            boolean correctPatientIdInput = readPatientId();
            boolean correctCaseIdInput = readCaseId();
            readPatientDiagnosis();

            if (correctPatientIdInput && correctCaseIdInput && !diagnosis.equals(getString(R.string.diagnosisError))) {
                createPatient();
            }

//            patientInfo = Patient.getPatient(patientId, caseId, diagnosis, getApplicationContext());
//            navigateToNextActivity(this, MenuActivity.class);
        });
    }

    private boolean readPatientId(){
        patientId = Objects.requireNonNull(binding.patientIdInput.getText()).toString().trim();
        return checkPatientIdLength();
    }

    private boolean readCaseId(){
        caseId = Objects.requireNonNull(binding.caseIdInput.getText()).toString().trim();
        return checkCaseIdLength();
    }

    private void readPatientDiagnosis(){
        int diagnosisId = binding.radioGroupDiagnosis.getCheckedRadioButtonId();
        diagnosis = convertDiagnosisToString(diagnosisId);
    }

    private boolean checkPatientIdLength(){
        if (patientId.length() != 7) {
            binding.patientIdInput.setError(getString(R.string.patientIdInputError));
            return false;
        }

        return true;
    }

    private boolean checkCaseIdLength(){
        if (caseId.length() != 7) {
            binding.caseIdInput.setError(getString(R.string.caseIdInputError));
            return false;
        }

        return true;
    }

    private String convertDiagnosisToString(int diagnosis) {
        if (diagnosis == binding.radioBtnStroke.getId()){
            return getString(R.string.diagnosisStroke);
        } else if (diagnosis == binding.radioBtnPakinson.getId()){
            return getString(R.string.diagnosisPakinson);
        } else if (diagnosis == binding.radioBtnOther.getId()){
            return getString(R.string.diagnosisOther);
        } else {
            binding.diagnosisTextInput.setError("Select diagnosis");
            return getString(R.string.diagnosisError);
        }
    }

    private void createPatient(){
        patientInfo.setPatientData(patientId, caseId, diagnosis, this);

        restoreAssessments();
        navigateToNextActivity(this, MenuActivity.class);
    }

    public void restoreAssessments() {
        File[] assessmentFiles = getPatientFilesIfExisting();

        if(assessmentFiles == null){
            return;
        }

        for(File file : assessmentFiles) {
            restoreAssessmentIfNotEmpty(file);
        }
    }

    private File[] getPatientFilesIfExisting(){
        return Utils.getFilesFromInternalStorageFolder(patientInfo.getPatientFolderPath());
    }

    private void restoreAssessmentIfNotEmpty(File file){
        String fileName = file.getName();
        AssessmentFactory.AssessmentNames assessmentName = AssessmentFactory.AssessmentNames.valueOf(fileName);
        Assessment assessment = AssessmentFactory.createAssessment(assessmentName);

        if(assessment == null) {
            return;
        }

        boolean notEmptyAssessment = assessment.retrieveAssessment(file);
        if(notEmptyAssessment){
            patientInfo.addAssessment(assessment);
        }
    }

    private void initRadioGroup() {
        binding.radioGroupDiagnosis.setOnCheckedChangeListener((radioGroup, i) -> binding.diagnosisTextInput.setError(null));
    }

    @Override
    public void init() {
        initRadioGroup();
    }

    @Override
    public void listenBtn() {
        listenBtnConfirm();
    }

    @Override
    public void setBinding() {
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void prepareIntent(Intent intent) {
    }

    @Override
    public void processReceivedIntent(Intent intent) {

    }
}