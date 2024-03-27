package com.example.ispeak.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import com.example.ispeak.Interfaces.IntentHandler;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.Microphone;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.R;
import com.example.ispeak.Utils.Utils;
import com.example.ispeak.databinding.ActivityBodysMenuBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.Objects;

public class BoDySMenuActivity extends BaseApp {

    ActivityBodysMenuBinding binding;
    private int assessmentNr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBodysMenuBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        assessmentNr = Patient.getInstance().findAssessment(BoDyS.class);

        retrieveIntent(this);
        enableNavBackArrow();
        listenBtnNewAssessment();

        if(isExisting()){
            listenBtnContinueAssessment();
        } else {
            disableBtnContinueAssessment();
        }
    }

    private void listenBtnNewAssessment(){
        binding.newAssessment.setOnClickListener(view -> {

            if(isExisting()){
                showNewAssessmentConfirmation();
                return;
            }

            startNewAssessment();

        });
    }

    private void listenBtnContinueAssessment(){
        binding.oldAssessment.setOnClickListener(view -> {
            navigateToNextActivity(this, BoDySOverviewPageActivity.class);
        });
    }

    private boolean isExisting(){
        return assessmentNr != -1;
    }

    private void disableBtnContinueAssessment(){
        binding.oldAssessment.setVisibility(View.GONE);
        binding.oldAssessmentNotActivated.setVisibility(View.VISIBLE);
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
                .setPositiveButton("Okay", ((dialogInterface, i) -> showFinishedAssessmentConfirmation()))
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    private void showFinishedAssessmentConfirmation(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("BoDyS Assessment wurde bereits abgeschickt - TBD: Case")
                .setPositiveButton("Okay", ((dialogInterface, i) -> startNewAssessment()))
                .show();
    }

    private void startNewAssessment(){
        BoDyS boDyS = new BoDyS();
        assessmentNr = assessmentNr == -1? 0 : assessmentNr;
        Patient.getInstance().addAssessment(boDyS, assessmentNr);
        Utils.deleteFromDir(new File(boDyS.getFolderPath() + File.separator + "Recordings"));
        Utils.deleteFromDir(new File(boDyS.getFolderPath() + File.separator + "CSV"));

        navigateToNextActivity(this, MicrophoneConnectionActivity.class);
    }

    @Override
    public void prepareIntent(Intent intent) {
        intent.putExtra("assessmentNr", assessmentNr);
        intent.getIntExtra("assessmentTaskId", 0);
    }

    @Override
    public void processReceivedIntent(Intent intent) {
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            navigateToNextActivity(this, MenuActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }
}
