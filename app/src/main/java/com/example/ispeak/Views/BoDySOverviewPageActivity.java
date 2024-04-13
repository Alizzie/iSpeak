package com.example.ispeak.Views;

import static com.example.ispeak.Models.BoDyS.BODYSDIC;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.BoDySSheet;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.Models.Recording;
import com.example.ispeak.R;
import com.example.ispeak.databinding.ActivityBodysOverviewPageBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class BoDySOverviewPageActivity extends BaseApp {

    private ActivityBodysOverviewPageBinding binding;
    private int assessmentNr;
    private BoDyS assessment;
    private int nextTask;
    private int evaluatedTasks;
    private int totalScorePoints;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBodysOverviewPageBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        retrieveIntent(this);
        init();
        listenStartRecordingBtn();
        listenStartFrequencyObservationBtn();
        listenStartScoreOverviewBtn();

        if(!assessment.isCompleted()){
            listenEditInfoBox();
        }

        if(evaluatedTasks >= assessment.getMaxRecordingNr() && !assessment.isCompleted()){
            listenFinishBtn();
        }
    }

    private void init(){
        initPatientData();
        enableNavBackArrow();
        initTasks();
        initProgressBar();

        if(assessment.isCompleted()){
            binding.completionStatus.setVisibility(View.VISIBLE);
        }
    }

    private void initPatientData(){
        Patient patient = Patient.getInstance();
        binding.patientId.setText(getString(R.string.patientData, patient.getPatientId()));
        binding.caseId.setText(getString(R.string.caseData, patient.getCaseId()));
        binding.patientDiagnosis.setText(patient.getDiagnosis());
        binding.caseDate.setText(patient.getFormattedDate());
//        binding.totalTasksScore.setText(getString(R.string.taskOverviewScoringDE, 0));

        this.assessment = (BoDyS) patient.getAssessmentList().get(this.assessmentNr);
        binding.circumstancesInfo.setText("Begleitumstände: " + assessment.getFormattedCircumstances());
        binding.commentsInfo.setText("Anmerkungen: " + assessment.getFormattedNotes());
    }

    private void initProgressBar(){

        float progress = ((float) 100 / assessment.getMaxRecordingNr()) * (evaluatedTasks);
        progress = Math.max(progress, 0);

        binding.taskProgressBar.setProgress((int) progress);
        binding.taskProgressNr.setText(getString(R.string.taskOverviewProgress, progress));
    }

    private void initTasks(){
        Recording[] recordings = assessment.getRecordings();
        BoDySSheet[] boDySSheets = assessment.getBoDySSheets();

        nextTask = 0;
        boolean recordingFinished = isAllRecordingAvailable(recordings, boDySSheets);

        if(recordingFinished) {
            nextTask = 0;
            evaluatedTasks = 0;
            disableRecordingMode();
            initEvaluationMode(recordings, boDySSheets);
        }

        if(checkEvaluationDone() && !assessment.isCompleted()){
            activateFinishBtn();
        }

    }

    private void initEvaluationMode(Recording[] recordings, BoDySSheet[] boDySSheets){
        boolean nextTaskFound = false;
        for(int i = 0; i < recordings.length; i++ ){

            int taskOverviewId = getResources().getIdentifier("boDySTask" + i, "id", getPackageName());
            CardView taskOverview = findViewById(taskOverviewId);
            TextView taskStatus = taskOverview.findViewById(R.id.taskStatus);

            if((boDySSheets[i] != null) && boDySSheets[i].getStatus().isSkipped()){
                evaluatedTasks += 1;

                if(!nextTaskFound){
                    updateEvaluationMode(boDySSheets[i], false, i);
                }
                continue;
            }

            if((boDySSheets[i] != null) && boDySSheets[i].getStatus().isPrefill()){
                taskStatus.setText(getString(R.string.scoringStatusNegativeDE));

                if(!nextTaskFound){
                    nextTask = i;
                    nextTaskFound = true;
                }
            }

            if(((boDySSheets[i] != null) && boDySSheets[i].getStatus().isEvaluated()) ) {
                evaluatedTasks += 1;
                updateEvaluationMode(boDySSheets[i], false, i);
            }

        }

        if(!checkEvaluationDone()) {
            updateEvaluationMode(boDySSheets[nextTask], true, nextTask);
        }
    }

    private boolean isAllRecordingAvailable(Recording[] recordings, BoDySSheet[] sheets) {

        boolean recordingDone = true;

        for(int i = 0; i < recordings.length; i++ ){

            int taskOverviewId = getResources().getIdentifier("boDySTask" + i, "id", getPackageName());
            CardView taskOverview = findViewById(taskOverviewId);

            TextView taskName = taskOverview.findViewById(R.id.taskName);
            TextView taskDuration = taskOverview.findViewById(R.id.taskDuration);
            TextView taskStatus = taskOverview.findViewById(R.id.taskStatus);

            updateRecordingStatus(recordings[i], sheets[i], taskStatus, taskName, taskDuration, i);
            if((sheets[i] == null) || (recordings[i] == null && (sheets[i].getStatus().isUnknown()))) {
                recordingDone = false;
            } else {
                nextTask = nextTask + 1;
            }
        }

        return recordingDone;
    }

    private void updateRecordingStatus(Recording recording, BoDySSheet sheet, TextView taskStatus, TextView taskName, TextView taskDuration, int nr) {
        if((sheet == null) || ((recording == null) && sheet.getStatus().isUnknown())) {
            taskStatus.setText(getString(R.string.recordingStatusNegativeDE));
        } else if (sheet.getStatus().isSkipped()) {
            taskStatus.setText("Übersprungen");
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

        if(sheet.getStatus().isSkipped()){
            return;
        }

        listenTaskOveview(taskOverview, nr);
        playImage.setImageResource(R.drawable.play_24_darkblue);

        if(!last) {
            taskStatus.setText(getString(R.string.scoringStatusPositiveDE));
            taskStatus.setTextColor(getColor(R.color.green));
            taskScore.setText(String.valueOf(sheet.getTotalScore()));
            totalScorePoints = totalScorePoints + sheet.getTotalScore();
//            binding.totalTasksScore.setText(getString(R.string.taskOverviewScoringDE, totalScorePoints));
        }
    }

    private boolean checkEvaluationDone(){
        return evaluatedTasks == assessment.getMaxRecordingNr();
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
        binding.startScoresOverview.setVisibility(View.VISIBLE);
        binding.textView4.setVisibility(View.VISIBLE);
    }

    private void listenStartFrequencyObservationBtn(){
        binding.startFrequencyObservation.setOnClickListener(view ->
                navigateToNextActivity(this, BoDySFrequencyObservationActivity.class));
    }

    private void listenStartScoreOverviewBtn(){
        binding.startScoresOverview.setOnClickListener(view -> navigateToNextActivity(this, BoDySScoreOverviewActivity.class));
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
                .setPositiveButton("Abschliessen", ((dialogInterface, i) -> finishBoDySAssessment()))
                .setNegativeButton("Weiter bearbeiten", null)
                .show();
    }

    private void finishBoDySAssessment(){
        assessment.finishAssessment();
        navigateToNextActivity(this, MenuActivity.class);
    }

    @Override
    public void prepareIntent(Intent intent) {
        intent.putExtra("assessmentNr", assessmentNr);
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        assessmentNr = intent.getIntExtra("assessmentNr", -1);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            navigateToNextActivity(this, BoDySMenuActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }
}
