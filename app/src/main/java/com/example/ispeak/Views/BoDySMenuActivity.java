package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ispeak.Interfaces.IntentHandler;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.Utils.Utils;
import com.example.ispeak.databinding.ActivityBodysMenuBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;

public class BoDySMenuActivity extends AppCompatActivity implements IntentHandler {

    ActivityBodysMenuBinding binding;
    private int assessmentNr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBodysMenuBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        assessmentNr = Patient.getInstance().findAssessment(BoDyS.class);

        retrieveIntent(this);
        listenBtnNewAssessment();
        listenBtnContinueAssessment();
    }

    private void listenBtnNewAssessment(){
        binding.newAssessment.setOnClickListener(view -> {

            if(isExisting()){
                showNewAssessmentConfirmation();
                return;
            }

            assessmentNr = 0;
            startNewAssessment();

        });
    }

    private void listenBtnContinueAssessment(){
        binding.oldAssessment.setOnClickListener(view -> {
            if(!isExisting()) {
                showNoExistingAssessmentMessage();
                return;
            }

            navigateToNextActivity(this, BoDySOverviewPageActivity.class);
        });
    }

    private boolean isExisting(){
        return assessmentNr != -1;
    }

    private void showNoExistingAssessmentMessage(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Kein BoDyS Assessment durchgeführt")
                .setMessage("Bisher wurde noch kein BoDyS durchgeführt. Bitte starten Sie ein neues an.")
                .setPositiveButton("Okay", null)
                .show();
    }
    private void showNewAssessmentConfirmation(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Existierender BoDyS Assessment")
                .setMessage("Es existiert bereits ein BoDyS Assessment. Möchten Sie diese verwerfen und mit einem neuen beginnen?")
                .setPositiveButton("Okay", ((dialogInterface, i) -> startNewAssessment()))
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    private void startNewAssessment(){
        BoDyS boDyS = new BoDyS();
        Patient.getInstance().addAssessment(boDyS, assessmentNr);
        Utils.deleteFromDir(new File(boDyS.getFolderPath() + File.separator + "Recordings"));
        Utils.deleteFromDir(new File(boDyS.getFolderPath() + File.separator + "CSV"));

        navigateToNextActivity(this, BoDySOverviewPageActivity.class);
    }

    @Override
    public void prepareIntent(Intent intent) {
        intent.putExtra("assessmentNr", assessmentNr);
    }

    @Override
    public void processReceivedIntent(Intent intent) {
    }
}
