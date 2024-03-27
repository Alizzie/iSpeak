package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ispeak.Adapter.CategoryAdapter;
import com.example.ispeak.Adapter.EventAdapter;
import com.example.ispeak.Interfaces.EventLabelingObserver;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.BoDySSheet;
import com.example.ispeak.Models.Event;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.Models.Recording;
import com.example.ispeak.R;
import com.example.ispeak.Utils.BoDySMarkingView;
import com.example.ispeak.Utils.BoDySScoringView;
import com.example.ispeak.Utils.BoDySStatus;
import com.example.ispeak.Utils.WaveformSeekbar;
import com.example.ispeak.databinding.ActivityBodysSheetBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class BoDySSheetActivity extends BaseApp implements EventLabelingObserver {

    private ActivityBodysSheetBinding binding;
    private Patient patientInfo;
    private BoDyS assessment;
    private int assessmentNr;
    private EventAdapter eventAdapter;
    private CategoryAdapter categoryAdapter;
    private int taskId;
    private Recording recording;
    private BoDySSheet boDySSheet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBodysSheetBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        listenEventLabelingBtn();
        listenMarkingBtn();
        listenSkipBtn();

        if(!boDySSheet.getStatus().isUnknown()) {
            showFullFunctionalities();
            listenScoringBtn();
            listenPlayAudioBtn();
            listenBackwardAudioBtn();
            listenForwardAudioBtn();
            enableNavBackArrow();
        }

        if(!assessment.isCompleted()){
            listenEventBookmarkBtn();
            listenEditBtn();
        }
    }

    private void showFullFunctionalities(){
        binding.playBtn.setVisibility(View.VISIBLE);
        binding.forwardBtn.setVisibility(View.VISIBLE);
        binding.backwardBtn.setVisibility(View.VISIBLE);
        binding.scoringModeBtn.setVisibility(View.VISIBLE);
    }

    private void init() {
        retrieveIntent(this);
        initPatientData();
        initBoDySSheet();

        initWaveformSeekbar();
        initTaskProgressBar(taskId);

        ArrayList<Event> events = recording.getEvents();
        initEventRecyclerView(events);
        initCategoryListView(events);

        binding.recordingTime.setText(getApplicationContext().getString(R.string.patientRecordingTime, recording.getFormattedPatientTime()));
    }

    private void initPatientData(){
        patientInfo = Patient.getInstance();
        assessment = (BoDyS) Patient.getInstance().getAssessmentList().get(assessmentNr);
        binding.patientId.setText(patientInfo.getPatientId());
        binding.patientDiagnosis.setText(patientInfo.getDiagnosis());
    }

    private void initBoDySSheet(){
        taskId = assessment.getTaskId();

        this.recording = assessment.getRecordings()[taskId];
        this.boDySSheet = assessment.getBoDySSheets()[taskId];
        assessment.setCurrentSheet(this.boDySSheet);
    }

    private void initWaveformSeekbar() {
        WaveformSeekbar waveformSeekbar = binding.waveformSeekbar;
        waveformSeekbar.init(recording.getMp3_filepath(), binding.audioTime, binding.audioDuration, binding.playBtn);
        setWaveformSeekbarProgress((int) RecyclerView.NO_ID);
    }

    private void initTaskProgressBar(int taskId){
        float maxProgress = binding.taskProgressBar.getMax();
        float progress = (maxProgress/ assessment.getMaxRecordingNr()) * (taskId + 1);
        binding.taskProgressNr.setText(getResources().getString(R.string.taskProgress, taskId + 1));
        binding.taskProgressBar.setProgress((int) progress);
        binding.taskName.setText(assessment.getTaskName(taskId));
    }

    private void initEventRecyclerView(ArrayList<Event> events){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.bodysEventLabeling.eventRecyclerView.setHasFixedSize(true);
        binding.bodysEventLabeling.eventRecyclerView.setLayoutManager(layoutManager);

        eventAdapter = new EventAdapter(events, getApplicationContext(), this);
        binding.bodysEventLabeling.eventRecyclerView.setAdapter(eventAdapter);

        setEventLabels(events);
    }

    private void initCategoryListView(ArrayList<Event> events){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.bodysEventLabeling.eventCategoryRecyclerView.setHasFixedSize(true);
        binding.bodysEventLabeling.eventCategoryRecyclerView.setLayoutManager(layoutManager);
        updateCategoryAdapter(events);
    }

    private void listenEventLabelingBtn(){
        binding.eventModeBtn.setOnClickListener(view -> {
            binding.bodysEventLabeling.getRoot().setVisibility(View.VISIBLE);
            binding.bodysMarkingSheet.getRoot().setVisibility(View.GONE);
            binding.bodysScoringSheet.getRoot().setVisibility(View.GONE);
        });
    }

    private void listenMarkingBtn(){
        BoDySMarkingView boDySMarkingView = binding.bodysMarkingSheet.getRoot();
        boDySMarkingView.setBoDySSheet(boDySSheet, assessment.isCompleted());
        binding.markingModeBtn.setOnClickListener(view -> {
            binding.bodysEventLabeling.getRoot().setVisibility(View.GONE);
            binding.bodysMarkingSheet.getRoot().setVisibility(View.VISIBLE);
            binding.bodysScoringSheet.getRoot().setVisibility(View.GONE);
        });
    }

    private void listenScoringBtn() {
        BoDySScoringView boDySScoringView = binding.bodysScoringSheet.getRoot();
        boDySScoringView.setBoDySSheet(boDySSheet, assessment.isCompleted());
        binding.scoringModeBtn.setOnClickListener(view -> {
            binding.bodysEventLabeling.getRoot().setVisibility(View.GONE);
            binding.bodysMarkingSheet.getRoot().setVisibility(View.GONE);
            binding.bodysScoringSheet.getRoot().setVisibility(View.VISIBLE);
            boDySScoringView.updateScoringVisuals();
        });
    }

    private void listenSkipBtn(){
        binding.skipPrefillBtn.setOnClickListener(view -> {
            if(!boDySSheet.getStatus().isUnknown()) {
                notPrefillNavigation();
            } else {
                prefillNavigation();
            }
        });
    }

    private void prefillNavigation(){
        if(taskId < assessment.getMaxRecordingNr() - 1) {
            assessment.startNewTaskRound();
            navigateToNextActivity(this, RecordingActivity.class);
        } else{
            assessment.startNewTaskRound();
            navigateToNextActivity(this, BoDySNotesActivity.class);
        }
    }

    private void notPrefillNavigation(){
        if(boDySSheet.hasEmptyScores()) {
            showScoringMissingDialog();
            return;
        }

        assessment.saveEvaluationData();
        binding.waveformSeekbar.stopWaveformAudio();
        navigateToNextActivity(this, BoDySOverviewPageActivity.class);
    }

    private void listenEditBtn(){
        binding.bodysEventLabeling.editEventBtn.setOnClickListener(view -> {
            eventAdapter.toggleDeleteMode();
            updateCategoryAdapter(recording.getEvents());
        });
    }

    private void listenPlayAudioBtn() {
        binding.playBtn.setOnClickListener(view -> binding.waveformSeekbar.startWaveformAudio());
    }

    private void listenBackwardAudioBtn() {
        binding.backwardBtn.setOnClickListener(view ->
            binding.waveformSeekbar.changeAudioPlayback(-1, binding.playBtn)
        );
    }

    private void listenForwardAudioBtn() {
        binding.forwardBtn.setOnClickListener(view ->
            binding.waveformSeekbar.changeAudioPlayback(1, binding.playBtn));
    }
    private void listenEventBookmarkBtn(){
        binding.eventBookmarkBtn.setOnClickListener(view -> {
            long eventStart = (long) binding.waveformSeekbar.getProgress();
            int eventId = recording.getEvents().size();
            Event newEvent = new Event(eventId, taskId, eventStart);
            recording.addEvent(newEvent);

            eventAdapter.setDeleteMode(false);
            eventAdapter.notifyAdapterItemInserted(eventId);

            binding.eventModeBtn.performClick();
        });
    }
    private void showScoringMissingDialog(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Fehlende Bewertungen")
                .setMessage("Bitte gebe alle Kriterien eine Bewertung.")
                .setPositiveButton("Ok", null)
                .show();
    }

    private void updateCategoryAdapter(ArrayList<Event> events){
        Event selectedEvent;
        if(events.size() == 0 || eventAdapter.isDeleteMode()){
            selectedEvent = null;
        } else if (eventAdapter.getCheckPosition() == RecyclerView.NO_POSITION){
            selectedEvent = null;
        } else {
            selectedEvent = events.get(eventAdapter.getCheckPosition());
        }

        categoryAdapter = new CategoryAdapter(new ArrayList<>(assessment.getCurrentSheet().getBoDySCriteria().keySet()), getApplicationContext(), selectedEvent, this, assessment.isCompleted());
        binding.bodysEventLabeling.eventCategoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setEventLabels(ArrayList<Event> eventArrayList){
        HashMap<Float, String> events = new HashMap<>();
        for (Event event: eventArrayList) {
            events.put((float) event.getTimeStart(), event.getEventLabels());
        }

        if(events.size() == 0){
            binding.bodysEventLabeling.noEventTitle.setVisibility(View.VISIBLE);
            binding.bodysEventLabeling.eventRecyclerView.setVisibility(View.GONE);
            setDividerLayoutParams(R.id.noEventTitle);
            eventAdapter.setDeleteMode(false);
        } else {
            binding.bodysEventLabeling.noEventTitle.setVisibility(View.GONE);
            binding.bodysEventLabeling.eventRecyclerView.setVisibility(View.VISIBLE);
            setDividerLayoutParams(R.id.eventRecyclerView);
        }

        binding.waveformSeekbar.setMarker(events);
    }

    private void setDividerLayoutParams(int viewId){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.bodysEventLabeling.divider.getLayoutParams();
        layoutParams.addRule(RelativeLayout.END_OF, viewId);
        binding.bodysEventLabeling.divider.setLayoutParams(layoutParams);
    }

    private void setWaveformSeekbarProgress(int eventId){
        if(eventId == RecyclerView.NO_ID){
            binding.waveformSeekbar.setProgress(0);
            return;
        }

        Event event = recording.getEvents().get(eventId);
        binding.waveformSeekbar.setProgress(event.getTimeStart());
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
    public void onListItemInserted(int position) {
        setEventLabels(recording.getEvents());
        updateCategoryAdapter(recording.getEvents());
    }

    @Override
    public void onListItemRemoved(int position) {
        recording.removeEvent(position);
        setEventLabels(recording.getEvents());
        updateCategoryAdapter(recording.getEvents());
    }

    @Override
    public void onEventClick(int position) {
        setWaveformSeekbarProgress(position);
        updateCategoryAdapter(recording.getEvents());
    }

    @Override
    public void onCategoryClick() {
        setEventLabels(recording.getEvents());
        eventAdapter.notifyItemChanged(eventAdapter.getCheckPosition());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        if(itemId == R.id.action_return_home) {
            binding.waveformSeekbar.stopWaveformAudio();
        } else if (itemId == android.R.id.home) {
            navigateToNextActivity(this, BoDySOverviewPageActivity.class);
        }

        return super.onOptionsItemSelected(item);
    }
}
