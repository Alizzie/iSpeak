package com.example.ispeak.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ispeak.Adapter.RecordingTrialAdapter;
import com.example.ispeak.Interfaces.IntentHandler;
import com.example.ispeak.Models.Assessment;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.Event;
import com.example.ispeak.Models.Microphone;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.Models.Recording;
import com.example.ispeak.R;
import com.example.ispeak.Utils.Utils;
import com.example.ispeak.databinding.ActivityRecordingBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class RecordingActivity extends AppCompatActivity implements IntentHandler {

    private ActivityRecordingBinding binding;
    private RecordingTrialAdapter adapter;
    private String outputAudio;
    private Microphone microphone;
    private int eventCounter = 0;
    private boolean eventRegistered;
    private Patient patientInfo;
    private int assessmentNr;
    private Assessment assessment;
    private ArrayList<Event> eventList = new ArrayList<>();
    private ArrayList<Recording> trialList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        listenBtnStartRecording();
        listenBtnPauseRecording();
        listenBtnEventDetection();
        listenBtnConfirm();
    }

    private void init(){
        retrieveIntent(this);

        patientInfo = Patient.getInstance();
        assessment = Patient.getInstance().getAssessmentList().get(assessmentNr);
        binding.patientId.setText(patientInfo.getPatientId());
        binding.patientDiagnosis.setText(patientInfo.getDiagnosis());

        microphone = new Microphone(this);
        adapter = new RecordingTrialAdapter(trialList, this);

        initTaskProgressBar();
        increaseTrialRun();
        initRecordingRecyclerView();

    }

    private void initTaskProgressBar(){
        int taskId = assessment.getTaskId();
        float maxProgress = binding.taskProgressBar.getMax();
        float progress = (maxProgress / assessment.getMaxRecordingNr()) * (taskId + 1);
        binding.taskProgressBar.setProgress((int) progress);
        binding.taskProgressNr.setText(getResources().getString(R.string.taskProgress, taskId + 1));
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
        File file = new File(recording.getMp3_filepath());
        File dest = new File(assessment.getFolderPath() + File.separator + "Recordings" +
                File.separator + "recording" + assessment.getTaskId() + ".3gp");

        boolean success = file.renameTo(dest);
        if(success) {
            recording.setMp3_filepath(dest.getPath());
        }

    }

    private void deleteOtherRecordings(int checkPosition) {
        for (int i = 0; i < trialList.size(); i++) {

            if (i != checkPosition) {
                // Delete the recording, e.g., using File.delete() or your specific logic
                File fileToDelete = new File(trialList.get(i).getMp3_filepath());
                boolean deleted = fileToDelete.delete();
                if (deleted) {
                    // Handle deletion success
                } else {
                    // Handle deletion failure
                }
            }
        }
    }


    private void listenBtnStartRecording(){
        binding.btnStartRecording.setOnClickListener(view -> {
            Button button = (Button) view;
            String playMode = button.getText().toString();  //get current playmode (start, stop)

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
        Event event = new Event(eventCounter, assessment.getTaskId(), timeStart);
        eventList.add(event);
    }

    private void registerEventEnd(){
        Objects.requireNonNull(eventList.get(eventCounter)).setTimeEnd(binding.patientTimeChrono.getTimeElapsed());
        eventCounter = eventCounter + 1;
        binding.numEvents.setText(String.valueOf(eventCounter));
    }

    private void updatePlayBtn(Button playBtn, String playMode) {
         if (playMode.equalsIgnoreCase("Stop")){
            playBtn.setText(getResources().getString(R.string.retryRecordingBtn));
            microphone.stopRecording();
        } else {
            playBtn.setText(getResources().getString(R.string.stopRecordingBtn));
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
        } else {
            binding.waveformView.setVisibility(View.VISIBLE);
            binding.recyclerTrial.setVisibility(View.GONE);
        }
    }

    private void updateBtnLayout(String playMode) {
        if (playMode.equalsIgnoreCase("Stop")) {
            binding.btnConfirm.setVisibility(View.VISIBLE);
            binding.btnPauseRecording.setVisibility(View.INVISIBLE);
            binding.btnEventDetected.setVisibility(View.INVISIBLE);
        } else {
            binding.btnConfirm.setVisibility(View.INVISIBLE);
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
        } else {
            long base = SystemClock.elapsedRealtime();
            binding.totalTimeChrono.setBase(base);
            binding.totalTimeChrono.start();
            binding.patientTimeChrono.setBase(base);
            binding.patientTimeChrono.start();
            resetEventCounter();
        }
    }

    private void resetEventCounter(){
        eventCounter = 0;
        eventList = new ArrayList<>();
        binding.numEvents.setText(getResources().getString(R.string.number0));
    }

    private void increaseTrialRun(){
        int currentTrialId = trialList.size() + 1;
        updateOutputAudioPath(currentTrialId);
        binding.trialNumber.setText(getResources().getString(R.string.trialNumber, currentTrialId));
    }

    private void updateOutputAudioPath(int trialId) {
        outputAudio = assessment.getFolderPath() + File.separator + "Recordings" +
                File.separator + "recording" + assessment.getTaskId() + "_trial_" + trialId + ".3gp";
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
}
