package com.example.ispeak.Views;

import static com.example.ispeak.Models.BoDyS.BODYSDIC;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
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
    private boolean evaluationMode;
    private int nextTask;
    private int totalScorePoints;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBodysOverviewPageBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        retrieveIntent(this);
        init();
        listenStartRecordingBtn();
    }

    private void init(){
        Patient patient = Patient.getInstance();
        binding.patientId.setText(getString(R.string.patientData, patient.getPatientId()));
        binding.caseId.setText(getString(R.string.caseData, patient.getCaseId()));
        binding.patientDiagnosis.setText(patient.getDiagnosis());
        binding.caseDate.setText(patient.getFormattedDate());
        binding.totalTasksScore.setText(getString(R.string.taskOverviewScoringDE, 0));

        this.assessment = (BoDyS) patient.getAssessmentList().get(this.assessmentNr);

        initTasks();
        initProgressBar();

        if(nextTask >= assessment.getMaxRecordingNr()){
            listenFinishBtn();
        }
    }

    private void initProgressBar(){

        float progress = (100 / assessment.getMaxRecordingNr()) * (nextTask);
        progress = Math.max(progress, 0);

        binding.taskProgressBar.setProgress((int) progress);
        binding.taskProgressNr.setText(getString(R.string.taskOverviewProgress, progress));
    }

    private void initTasks(){
        Recording[] recordings = assessment.getRecordings();
        BoDySSheet[] boDySSheets = assessment.getBoDySSheets();
        nextTask = 0;

        for(int i = 0; i < recordings.length; i++ ){

            int taskOverviewId = getResources().getIdentifier("boDySTask" + i, "id", getPackageName());
            CardView taskOverview = findViewById(taskOverviewId);

            TextView taskName = taskOverview.findViewById(R.id.taskName);
            TextView taskDuration = taskOverview.findViewById(R.id.taskDuration);
            TextView taskScore = taskOverview.findViewById(R.id.taskScore);
            TextView taskStatus = taskOverview.findViewById(R.id.taskStatus);

            if(!isAllRecordingAvailable(recordings[i], i, taskStatus, taskName, taskDuration)){
                break;
            }

            if(isRecordingEvaluated(recordings[i], boDySSheets[i], taskStatus, taskScore)) {
                activateNextEvaluationTask(i);
                nextTask = i + 1;
                continue;
            }

            initEvaluationMode(taskStatus);
        }

        if(evaluationMode) {
            disableRecordingMode();
            activateNextEvaluationTask(nextTask);
        }
    }

    private void activateNextEvaluationTask(int taskId){

        if(taskId >= assessment.getMaxRecordingNr()) {
            return;
        }

        int taskOverviewId = getResources().getIdentifier("boDySTask" + nextTask, "id", getPackageName());
        CardView taskOverview = findViewById(taskOverviewId);

        TextView taskName = taskOverview.findViewById(R.id.taskName);
        TextView taskDuration = taskOverview.findViewById(R.id.taskDuration);
        TextView taskScore = taskOverview.findViewById(R.id.taskScore);

        taskName.setTextColor(getColor(R.color.black));
        taskDuration.setTextColor(getColor(R.color.black));
        taskScore.setTextColor(getColor(R.color.black));

        listenTaskOveview(taskOverview, taskId);
    }

    private boolean isAllRecordingAvailable(Recording recording, int nr, TextView taskStatus, TextView taskName, TextView taskDuration) {
        if(recording == null) {
            taskStatus.setText(getString(R.string.recordingStatusNegativeDE));
            return false;
        } else {
            taskStatus.setText(getString(R.string.recordingStatusPositiveDE));
            taskName.setText(BODYSDIC.get(nr));
            taskDuration.setText(recording.getFormattedPatientTime());
            return true;
        }
    }

    private boolean isRecordingEvaluated(Recording recording, BoDySSheet sheet, TextView taskStatus, TextView taskScore){
        if(recording.isEvaluated()) {
            evaluationMode = true;
            taskStatus.setText(getString(R.string.scoringStatusPositiveDE));
            taskScore.setText(String.valueOf(sheet.getTotalScore()));
            totalScorePoints = totalScorePoints + sheet.getTotalScore();
            binding.totalTasksScore.setText(String.valueOf(totalScorePoints));
            return true;
        }

        return false;
    }

    private void initEvaluationMode(TextView taskStatus){
        evaluationMode = true;
        binding.startRecordingBtn.setVisibility(View.GONE);
        binding.startRecordingBtnNotActivated.setVisibility(View.VISIBLE);
        taskStatus.setText(getString(R.string.scoringStatusNegativeDE));

        if(nextTask == assessment.getMaxRecordingNr()) {
            binding.finishBoDySBtnNotActivated.setVisibility(View.GONE);
            binding.finishBoDySBtn.setVisibility(View.VISIBLE);
        }
    }

    private void listenTaskOveview(CardView taskOverview, int taskId){
        taskOverview.setOnClickListener(view -> {
            nextTask = taskId;
            navigateToNextActivity(this, BoDySSheetActivity.class);});
    }

    private void listenStartRecordingBtn(){
        binding.startRecordingBtn.setOnClickListener(view -> navigateToNextActivity(this, MicrophoneConnectionActivity.class));
    }

    private void disableRecordingMode(){
        binding.startRecordingBtn.setVisibility(View.GONE);
        binding.startRecordingBtnNotActivated.setVisibility(View.VISIBLE);
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

        if(evaluationMode) {
            intent.putExtra("prefill", false);
        }

        intent.putExtra("assessmentTaskId", nextTask);
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        assessmentNr = intent.getIntExtra("assessmentNr", -1);
        intent.getBooleanExtra("assessmentNew", true);
    }
}
