package com.example.ispeak.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ispeak.Adapter.RecordingTrialAdapter;
import com.example.ispeak.Models.Assessment;
import com.example.ispeak.Models.Event;
import com.example.ispeak.Models.Microphone;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.Models.Recording;
import com.example.ispeak.R;
import com.example.ispeak.databinding.ActivityRecordingBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class RecordingActivity extends BaseApp {

    private ActivityRecordingBinding binding;
    private RecordingTrialAdapter adapter;
    private String outputAudio;
    private Microphone microphone;
    private int eventCounter = 0;
    private boolean eventRegistered;
    private int assessmentNr, taskId;
    private Assessment assessment;
    private ArrayList<Event> eventList = new ArrayList<>();
    private ArrayList<Recording> trialList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void init(){
        initAssessmentInfo();
        initRecordingInfo();
        initTaskProgressBar();
        increaseTrialRun();
        initRecordingRecyclerView();
    }

    @Override
    public void listenBtn() {
        listenBtnStartRecording();
        listenBtnPauseRecording();
        listenBtnEventDetection();
        listenBtnConfirm();
    }

    @Override
    public void setBinding() {
        binding = ActivityRecordingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    private void initAssessmentInfo(){
        assessment = patientInfo.getAssessmentList().get(assessmentNr);
        binding.patientId.setText(patientInfo.getPatientId());
        binding.patientDiagnosis.setText(patientInfo.getDiagnosis());
        taskId = assessment.getTaskId();
    }

    private void initRecordingInfo(){
        microphone = new Microphone(this);
        adapter = new RecordingTrialAdapter(trialList, this);
    }

    private void initTaskProgressBar(){
        float maxProgress = binding.taskProgressBar.getMax();
        float progress = (maxProgress / assessment.getMaxRecordingNr()) * (taskId + 1);
        binding.taskProgressBar.setProgress((int) progress);
        binding.taskProgressNr.setText(getString(R.string.taskProgress, taskId + 1));
        binding.taskName.setText(assessment.getTaskName(taskId));
    }

    private void initRecordingRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerTrial.setHasFixedSize(true);
        binding.recyclerTrial.setLayoutManager(layoutManager);
    }

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(view -> {
            Recording checkedRecording = adapter.getCheckedTrial();
            if (checkedRecording != null) {
                assessment.updateRecordingList(checkedRecording);
                saveRecording(checkedRecording);
                deleteOtherRecordings(adapter.getCheckPosition());
                navigateToNextActivity(this, BoDySSheetActivity.class);
            }
        });
    }

    private void saveRecording(Recording recording) {
        File file = new File(recording.getMp3Filepath());
        File dest = new File(assessment.getFolderPath() + File.separator + "Recordings" +
                File.separator + "recording_" + taskId + ".3gp");

        boolean success = file.renameTo(dest);
        if(success) {
            recording.setMp3Filepath(dest.getPath());
        }

    }

    private void deleteOtherRecordings(int checkPosition) {
        for (int i = 0; i < trialList.size(); i++) {

            if (i != checkPosition) {
                File fileToDelete = new File(trialList.get(i).getMp3Filepath());
                fileToDelete.delete();
            }
        }
    }


    private void listenBtnStartRecording(){
        binding.btnStartRecording.setOnClickListener(view -> {
            Button button = (Button) view;
            String playMode = button.getText().toString();

            updatePlayBtn(button, playMode);
            updateChronometer(playMode);
            updateCardView(playMode);
            updateBtnLayout(playMode);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void listenBtnPauseRecording(){
        binding.btnPauseRecording.setOnTouchListener((view, motionEvent) -> {
            if (!microphone.isActive()) {
                return false;
            }

            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                microphone.pauseRecording();
                binding.patientTimeChrono.pause();
            }else{
                microphone.resumeRecording(binding.waveformView);
                binding.patientTimeChrono.unpause();
            }

            return false;
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    private void listenBtnEventDetection(){
        binding.btnEventDetected.setOnTouchListener((view, motionEvent) -> {
            if (!microphone.isActive() || microphone.isPaused()) {
                eventRegistered = false;
                return false;
            }

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                registerEventStart();
                eventRegistered = true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP && eventRegistered) {
                registerEventEnd();
            }

            return false;
        });
    }

    private void registerEventStart(){
        if (eventList.size() != eventCounter) {
            return;
        }

        long timeStart = binding.patientTimeChrono.getTimeElapsed();
        Event event = new Event(eventCounter, taskId, timeStart);
        eventList.add(event);
    }

    private void registerEventEnd(){
        Objects.requireNonNull(eventList.get(eventCounter)).setTimeEnd(binding.patientTimeChrono.getTimeElapsed());
        eventCounter = eventCounter + 1;
        binding.numEvents.setText(String.valueOf(eventCounter));
    }

    private void updatePlayBtn(Button playBtn, String playMode) {
         if (playMode.equalsIgnoreCase("Stop")){
            playBtn.setText(getString(R.string.retryRecordingBtn));
            microphone.stopRecording();
        } else if (playMode.equalsIgnoreCase("Retry")) {
             playBtn.setText(getString(R.string.startRecordingBtn));
             microphone.resetRecording(binding.waveformView);
             resetEventCounter();
         } else {
            playBtn.setText(getString(R.string.stopRecordingBtn));
            increaseTrialRun();
            microphone.startRecording(binding.waveformView, outputAudio);
        }
    }

    private void updateCardView(String playMode) {
        if (playMode.equalsIgnoreCase("Stop")){
            binding.waveformView.setVisibility(View.GONE);
            binding.recyclerTrial.setVisibility(View.VISIBLE);

            adapter = new RecordingTrialAdapter(trialList, getApplicationContext());
            binding.recyclerTrial.setAdapter(adapter);
            binding.recyclerTrial.smoothScrollToPosition(trialList.size() - 1);
        } else if(playMode.equalsIgnoreCase("Retry")){
            binding.waveformView.setVisibility(View.VISIBLE);
            binding.recyclerTrial.setVisibility(View.GONE);
        }
    }

    private void updateBtnLayout(String playMode) {
        if (playMode.equalsIgnoreCase("Stop")) {
            binding.btnConfirm.setVisibility(View.VISIBLE);
            binding.btnPauseRecording.setVisibility(View.GONE);
            binding.btnEventDetected.setVisibility(View.GONE);
        } else if (playMode.equalsIgnoreCase("Retry")) {
            binding.btnPauseRecordingNotActivated.setVisibility(View.VISIBLE);
            binding.btnEventDetectedNotActivated.setVisibility(View.VISIBLE);
            binding.btnConfirm.setVisibility(View.GONE);
        } else{
            binding.btnPauseRecordingNotActivated.setVisibility(View.GONE);
            binding.btnEventDetectedNotActivated.setVisibility(View.GONE);
            binding.btnPauseRecording.setVisibility(View.VISIBLE);
            binding.btnEventDetected.setVisibility(View.VISIBLE);
        }
    }

    private void updateChronometer(String playMode){

        if (playMode.equalsIgnoreCase("Stop")) {
            binding.totalTimeChrono.stop();
            binding.patientTimeChrono.stop();
            long totalTimeElapsed = binding.totalTimeChrono.getTimeElapsed();
            long patientTimeElapsed = binding.patientTimeChrono.getTimeElapsed();
            trialList.add(new Recording(outputAudio, totalTimeElapsed, patientTimeElapsed, eventList));
        } else if (playMode.equalsIgnoreCase("Start")) {
            long base = SystemClock.elapsedRealtime();
            binding.totalTimeChrono.setBase(base);
            binding.totalTimeChrono.start();
            binding.patientTimeChrono.setBase(base);
            binding.patientTimeChrono.start();
        }
    }

    private void resetEventCounter(){
        eventCounter = 0;
        eventList = new ArrayList<>();
        binding.numEvents.setText(getString(R.string.number0));
    }

    private void increaseTrialRun(){
        int currentTrialId = trialList.size() + 1;
        updateOutputAudioPath(currentTrialId);
        binding.trialNumber.setText(getString(R.string.trialNumber, currentTrialId));
    }

    private void updateOutputAudioPath(int trialId) {
        outputAudio = assessment.getFolderPath() + File.separator + "Recordings" +
                File.separator + "recording" + taskId + "_trial_" + trialId + ".3gp";
    }

    @Override
    public void prepareIntent(Intent intent) {
        intent.putExtra("assessmentNr", assessmentNr);
        intent.putExtra("prefill", true);
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        assessmentNr = intent.getIntExtra("assessmentNr", -1);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem menuItem = menu.findItem(R.id.action_skip_task);
        if(menuItem != null){
            menuItem.setVisible(true);
            menuItem.setOnMenuItemClickListener((item) -> {
                assessment.skipTaskRound();
                if(taskId < assessment.getMaxRecordingNr() - 1) {
                    navigateToNextActivity(this, RecordingActivity.class);
                } else{
                    navigateToNextActivity(this, BoDySCircumstancesActivity.class);
                }
                return true;
            });
        }

        return super.onPrepareOptionsMenu(menu);
    }
}
