package com.example.ispeak.Views;

import static com.example.ispeak.Models.BoDyS.BODYSDIC;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.ispeak.Interfaces.IntentHandler;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.BoDySSheet;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.Models.Recording;
import com.example.ispeak.R;
import com.example.ispeak.databinding.ActivityBodysOverviewPageBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class BoDySOverviewPageActivity extends AppCompatActivity implements IntentHandler {

    private ActivityBodysOverviewPageBinding binding;
    private int assessmentNr;
    private BoDyS assessment;
    private int nextTask;
    private int totalScorePoints;
    private boolean isPrefill;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBodysOverviewPageBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        retrieveIntent(this);
        init();
        listenStartRecordingBtn();
        listenStartFrequencyObservationBtn();
        listenEditInfoBox();
    }

    private void init(){
        this.isPrefill = false;
        initPatientData();
        initTasks();
        initProgressBar();

        if(nextTask >= assessment.getMaxRecordingNr()){
            listenFinishBtn();
        }
    }

    private void initPatientData(){
        Patient patient = Patient.getInstance();
        binding.patientId.setText(getString(R.string.patientData, patient.getPatientId()));
        binding.caseId.setText(getString(R.string.caseData, patient.getCaseId()));
        binding.patientDiagnosis.setText(patient.getDiagnosis());
        binding.caseDate.setText(patient.getFormattedDate());
        binding.totalTasksScore.setText(getString(R.string.taskOverviewScoringDE, 0));

        this.assessment = (BoDyS) patient.getAssessmentList().get(this.assessmentNr);
        binding.circumstancesInfo.setText("Begleitumstände: " + assessment.getFormattedCircumstances());
        binding.commentsInfo.setText("Anmerkungen: " + assessment.getFormattedNotes());
    }

    private void initProgressBar(){

        float progress = ((float) 100 / assessment.getMaxRecordingNr()) * (nextTask);
        progress = Math.max(progress, 0);

        binding.taskProgressBar.setProgress((int) progress);
        binding.taskProgressNr.setText(getString(R.string.taskOverviewProgress, progress));
    }

    private void initTasks(){
        Recording[] recordings = assessment.getRecordings();
        BoDySSheet[] boDySSheets = assessment.getBoDySSheets();

        nextTask = 0;
        boolean recordingFinished = isAllRecordingAvailable(recordings);

        if(recordingFinished) {
            nextTask = 0;
            disableRecordingMode();
            initEvaluationMode(recordings, boDySSheets);
        }

        if(checkEvaluationDone()){
            activateFinishBtn();
        }

    }

    private void initEvaluationMode(Recording[] recordings, BoDySSheet[] boDySSheets){
        for(int i = 0; i < recordings.length; i++ ){

            int taskOverviewId = getResources().getIdentifier("boDySTask" + i, "id", getPackageName());
            CardView taskOverview = findViewById(taskOverviewId);
            TextView taskStatus = taskOverview.findViewById(R.id.taskStatus);

            if(((boDySSheets[i] != null) && !boDySSheets[i].isPrefill()) ) {
                updateEvaluationMode(boDySSheets[i], false, i);
                nextTask = i + 1;
            } else {
                taskStatus.setText(getString(R.string.scoringStatusNegativeDE));
            }
        }

        if(!checkEvaluationDone()) {
            updateEvaluationMode(boDySSheets[nextTask], true, nextTask);
        }
    }

    private boolean isAllRecordingAvailable(Recording[] recordings) {

        boolean recordingDone = true;

        for(int i = 0; i < recordings.length; i++ ){

            int taskOverviewId = getResources().getIdentifier("boDySTask" + i, "id", getPackageName());
            CardView taskOverview = findViewById(taskOverviewId);

            TextView taskName = taskOverview.findViewById(R.id.taskName);
            TextView taskDuration = taskOverview.findViewById(R.id.taskDuration);
            TextView taskStatus = taskOverview.findViewById(R.id.taskStatus);

            updateRecordingStatus(recordings[i], taskStatus, taskName, taskDuration, i);

            if(recordings[i] == null) {
                recordingDone = false;
            } else {
                nextTask = nextTask + 1;
            }
        }

        return recordingDone;
    }

    private void updateRecordingStatus(Recording recording, TextView taskStatus, TextView taskName, TextView taskDuration, int nr) {
        if(recording == null) {
            taskStatus.setText(getString(R.string.recordingStatusNegativeDE));
        } else {
            taskStatus.setText(getString(R.string.recordingStatusPositiveDE));
            taskDuration.setText(recording.getFormattedPatientTime());
        }

        taskName.setText(BODYSDIC.get(nr));
    }

    private void updateEvaluationMode(BoDySSheet sheet, boolean last, int nr){
        int taskOverviewId = getResources().getIdentifier("boDySTask" + nr, "id", getPackageName());
        CardView taskOverview = findViewById(taskOverviewId);

        TextView taskName = taskOverview.findViewById(R.id.taskName);
        TextView taskDuration = taskOverview.findViewById(R.id.taskDuration);
        TextView taskScore = taskOverview.findViewById(R.id.taskScore);
        TextView taskStatus = taskOverview.findViewById(R.id.taskStatus);
        ImageView playImage = taskOverview.findViewById(R.id.playImage);

        taskName.setTextColor(getColor(R.color.black));
        taskDuration.setTextColor(getColor(R.color.black));
        taskScore.setTextColor(getColor(R.color.black));
        taskStatus.setTextColor(getColor(R.color.black));
        playImage.setImageResource(R.drawable.play_24_darkblue);

        listenTaskOveview(taskOverview, nr);

        if(!last) {
            taskStatus.setText(getString(R.string.scoringStatusPositiveDE));
            taskStatus.setTextColor(getColor(R.color.green));
            taskScore.setText(String.valueOf(sheet.getTotalScore()));
            totalScorePoints = totalScorePoints + sheet.getTotalScore();
            binding.totalTasksScore.setText(getString(R.string.taskOverviewScoringDE, totalScorePoints));
        }
    }

    private boolean checkEvaluationDone(){
        return nextTask == assessment.getMaxRecordingNr();
    }

    private void activateFinishBtn(){
        binding.finishBoDySBtnNotActivated.setVisibility(View.GONE);
        binding.finishBoDySBtn.setVisibility(View.VISIBLE);
    }

    private void listenTaskOveview(CardView taskOverview, int number){
        taskOverview.setOnClickListener(view -> {
            assessment.setTaskId(number);
            navigateToNextActivity(this, BoDySSheetActivity.class);});
    }

    private void listenStartRecordingBtn(){
        binding.startRecordingBtn.setOnClickListener(view -> {
            isPrefill = true;
            assessment.continueBoDyS(nextTask);
            navigateToNextActivity(this, MicrophoneConnectionActivity.class);
        });
    }

    private void listenEditInfoBox(){
        binding.notesInfoBox.setOnClickListener(view -> {
            navigateToNextActivity(this, BoDySNotesActivity.class);
        });
    }

    private void disableRecordingMode(){
        binding.startRecordingBtn.setVisibility(View.GONE);
//        binding.startRecordingBtnNotActivated.setVisibility(View.VISIBLE);
        binding.startFrequencyObservation.setVisibility(View.VISIBLE);
        binding.textView3.setVisibility(View.VISIBLE);
    }

    private void listenStartFrequencyObservationBtn(){
        binding.startFrequencyObservation.setOnClickListener(view ->
                navigateToNextActivity(this, BoDySFrequencyObservationActivity.class));
    }

    private void listenFinishBtn(){
        binding.finishBoDySBtnNotActivated.setVisibility(View.GONE);
        binding.finishBoDySBtn.setVisibility(View.VISIBLE);
        binding.finishBoDySBtn.setOnClickListener(view -> showFinishConfirmDialog());
    }

    private void showFinishConfirmDialog(){
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle("Willst du das Assessment abschliessen?")
                .setMessage("Nach dem Abschliessen ist das Überarbeiten des Assessments nicht mehr möglich.")
                .setPositiveButton("Ja", null)
                .setNegativeButton("Nein", null)
                .show();
    }

    @Override
    public void prepareIntent(Intent intent) {
        intent.putExtra("assessmentNr", assessmentNr);
        intent.putExtra("prefill", isPrefill);
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        assessmentNr = intent.getIntExtra("assessmentNr", -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }
}
