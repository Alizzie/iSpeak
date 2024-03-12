package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ispeak.Adapter.CategoryAdapter;
import com.example.ispeak.Adapter.EventAdapter;
import com.example.ispeak.Interfaces.EventLabelingObserver;
import com.example.ispeak.Interfaces.IntentHandler;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.Event;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.Models.Recording;
import com.example.ispeak.R;
import com.example.ispeak.databinding.ActivityBodysSheetBinding;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BoDySSheetActivity extends AppCompatActivity implements IntentHandler, EventLabelingObserver {

    private ActivityBodysSheetBinding binding;
    private Patient patientInfo;
    private BoDyS assessment;
    private EventAdapter eventAdapter;
    private CategoryAdapter categoryAdapter;
    private int taskId;
    private Recording recording;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBodysSheetBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        listenEventLabelingBtn();
        listenMarkingBtn();
        listenScoringBtn();
        listenSkipBtn();
        listenEditBtn();
        listenEventBookmarkBtn();
    }

    private void init() {
        retrieveIntent(this);
        patientInfo = Patient.getInstance();
        binding.patientId.setText(patientInfo.getPatientId());
        binding.patientDiagnosis.setText(patientInfo.getDiagnosis());

        this.taskId = assessment.getTaskId();
        this.recording = assessment.getRecordings()[taskId];
        ArrayList<Event> events = recording.getEvents();
        initTaskProgressBar(taskId);
        initWaveformBar(recording);
        initEventRecyclerView(events);
        initCategoryListView(events);
        initBoDySCriteria();

        binding.recordingTime.setText(getApplicationContext().getString(R.string.patientRecordingTime, recording.getPatientTime()));
    }

    private void initTaskProgressBar(int taskId){
        float maxProgress = binding.taskProgressBar.getMax();
        float progress = (maxProgress/ assessment.getMaxRecordingNr()) * (taskId + 1);
        binding.taskProgressNr.setText(getResources().getString(R.string.taskProgress, taskId + 1));
        binding.taskProgressBar.setProgress((int) progress);
        binding.taskName.setText(assessment.getTaskName(taskId));
    }

    private void initWaveformBar(Recording recording){
        binding.waveformSeekbar.setSampleFrom(recording.getMp3_filepath());
        binding.waveformSeekbar.setMaxProgress(recording.getPatientTime());
        binding.audioDuration.setText(String.valueOf(binding.waveformSeekbar.getMaxProgress()));

        binding.waveformSeekbar.setOnProgressChanged((waveformSeekBar, progress, fromUser) -> {
            if(fromUser) {

                if(progress < 0){
                    progress = 0;
                }

                if(progress > waveformSeekBar.getMaxProgress()) {
                    progress = waveformSeekBar.getMaxProgress();
                }
                binding.audioTime.setText(String.valueOf(progress));
            }
        });

        int eventId = recording.getEvents().size() == 0 ? (int) RecyclerView.NO_ID : 0;
        setWaveformSeekbarProgress(eventId);
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

    private void initBoDySCriteria(){
        HashMap<String, HashMap<String, Integer>> criteriaMap = assessment.getBoDySCriteria();
        List<String> mainCriteriaList = assessment.getMainCriteriaList();

        for (String mainCriteria: mainCriteriaList) {
            HashMap<String, Integer> criteriaValues = criteriaMap.get(mainCriteria);

            assert criteriaValues != null;
            setClickListenerOnCriteria(mainCriteria, criteriaValues);
            setClickListenerOnCriteriaScores(mainCriteria);
        }
    }

    private void setClickListenerOnCriteria(String mainCriteria, HashMap<String, Integer> criteriaValues){
        for (String criteria : criteriaValues.keySet()) {
            int btnId = getResources().getIdentifier(criteria, "id", this.getPackageName());
            MaterialButton btn = findViewById(btnId);
            btn.setCheckable(true);

            btn.setOnClickListener(view -> {
                toggleBtnBackgroundColor((MaterialButton) view);

                int changeChecked = criteriaValues.get(criteria) == 0? 1: 0;
                Objects.requireNonNull(criteriaValues.put(criteria, changeChecked));
                assessment.updateScores(mainCriteria, -1);
                updateScoringVisuals();
            });
        }
    }

    /*Stack Overflow:
    https://stackoverflow.com/questions/5944987/how-to-create-a-popup-window-popupwindow-in-android
    https://stackoverflow.com/questions/3221488/blur-or-dim-background-when-android-popupwindow-active/29950606#29950606
     */
    private void setClickListenerOnCriteriaScores(String mainCriteria) {
        int btnId = getResources().getIdentifier(mainCriteria+"Score", "id", this.getPackageName());
        MaterialButton mainCriteriaBtn = findViewById(btnId);

        mainCriteriaBtn.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View scorePopupView = inflater.inflate(R.layout.bodys_score_popup_window, null);

            PopupWindow popupWindow = initPopupWindow(scorePopupView, view);
            blurBackground(popupWindow);
            setOnTouchClosePopupListener(popupWindow);
            setOnClickScoreBtnListener(popupWindow, mainCriteria, mainCriteriaBtn);
        });
    }

    private void setOnClickScoreBtnListener(PopupWindow popupWindow, String mainCriteria, MaterialButton mainCriteriaBtn){
        GridLayout scoreBoard = popupWindow.getContentView().findViewById(R.id.scoreBoardPopup);
        for (int i = 0; i < scoreBoard.getChildCount(); i++) {
            MaterialButton scoreBtn = (MaterialButton) scoreBoard.getChildAt(i);

            scoreBtn.setOnClickListener(view -> {
                MaterialButton btn = (MaterialButton) view;
                int score = Integer.valueOf(btn.getText().toString());
                assessment.updateScores(mainCriteria, score);
                updateScoringVisuals();
                dismissPopupWindow(popupWindow);
            });
        }
    }

    private void toggleBtnBackgroundColor(MaterialButton btn){
        if(!btn.isChecked()) {
            btn.setBackgroundColor(getColor(R.color.lightGray));
        } else {
            btn.setBackgroundColor(getColor(R.color.lightBlue));
        }
    }
    private void updateScoringVisuals(){
        HashMap<String, Integer> boDySScores = assessment.getBoDySScores();

        List<String> emptyMarkingsCriteria = assessment.getMainCriteriaWithEmptyMarkings();

        for(String criteria: boDySScores.keySet()) {
            int btnId = getResources().getIdentifier(criteria+"Score", "id", this.getPackageName());
            MaterialButton btn = findViewById(btnId);

            if(emptyMarkingsCriteria.contains(criteria)) {
                btn.setText(String.valueOf(4));
                btn.setClickable(false);
            } else {
                btn.setClickable(true);
                int score = boDySScores.get(criteria);

                if(score == -1) {
                    btn.setText("--");
                } else {
                    btn.setText(String.valueOf(score));
                }
            }
        }
    }

    private void listenEventLabelingBtn(){
        binding.eventModeBtn.setOnClickListener(view -> {
            binding.bodysEventLabeling.getRoot().setVisibility(View.VISIBLE);
            binding.bodysMarkingSheet.getRoot().setVisibility(View.GONE);
            binding.bodysScoringSheet.getRoot().setVisibility(View.GONE);
        });
    }

    private void listenMarkingBtn(){
        binding.markingModeBtn.setOnClickListener(view -> {
            binding.bodysEventLabeling.getRoot().setVisibility(View.GONE);
            binding.bodysMarkingSheet.getRoot().setVisibility(View.VISIBLE);
            binding.bodysScoringSheet.getRoot().setVisibility(View.GONE);
        });
    }

    private void listenScoringBtn() {
        binding.scoringModeBtn.setOnClickListener(view -> {
            binding.bodysEventLabeling.getRoot().setVisibility(View.GONE);
            binding.bodysMarkingSheet.getRoot().setVisibility(View.GONE);
            binding.bodysScoringSheet.getRoot().setVisibility(View.VISIBLE);
            updateScoringVisuals();
        });
    }

    private void listenSkipBtn(){
        binding.skipPrefillBtn.setOnClickListener(view -> {
            if(taskId < 7) {
                navigateToNextActivity(this, RecordingActivity.class);
            } else{
                navigateToNextActivity(this, MenuActivity.class);
            }
        });
    }


    private void listenEditBtn(){
        binding.editBtn.setOnClickListener(view -> {
            eventAdapter.toggleDeleteMode();
            updateCategoryAdapter(recording.getEvents());
        });
    }

    private void listenEventBookmarkBtn(){
        binding.eventBookmarkBtn.setOnClickListener(view -> {
            float eventStart = binding.waveformSeekbar.getProgress();
            int eventId = recording.getEvents().size();
            Event newEvent = new Event(eventId, taskId, eventStart);
            recording.addEvent(newEvent);

            eventAdapter.setDeleteMode(false);
            eventAdapter.notifyAdapterItemInserted(eventId);
        });
    }

    private PopupWindow initPopupWindow(View scorePopupView, View btnView){
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(scorePopupView, width, height, focusable);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.showAtLocation(btnView, Gravity.CENTER, 0, 0);
        return popupWindow;
    }

    private void blurBackground(PopupWindow popupWindow){
        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);
    }

    private void setOnTouchClosePopupListener(PopupWindow popupWindow){
        popupWindow.getContentView().findViewById(R.id.popupCloseBtn).setOnTouchListener((v, e) ->  dismissPopupWindow(popupWindow));
    }

    private boolean dismissPopupWindow(PopupWindow popupWindow){
        popupWindow.dismiss();
        return true;
    }

    private void updateCategoryAdapter(ArrayList<Event> events){
        Event selectedEvent;
        if(events.size() == 0 || eventAdapter.isDeleteMode()){
            selectedEvent = null;
        } else if (eventAdapter.getCheckPosition() == RecyclerView.NO_POSITION){
            selectedEvent = events.get(0);
        } else {
            selectedEvent = events.get(eventAdapter.getCheckPosition());
        }

        categoryAdapter = new CategoryAdapter(new ArrayList<>(assessment.getBoDySCriteria().keySet()), getApplicationContext(), selectedEvent, this);
        binding.bodysEventLabeling.eventCategoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setEventLabels(ArrayList<Event> eventArrayList){
        HashMap<Float, String> events = new HashMap<>();
        for (Event event: eventArrayList) {
            events.put(event.getTimeStart(), event.getEventLabels());
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
        intent.putExtra("assessment", assessment);
        assessment.setTaskId(taskId + 1);
        assessment.startNewTaskRound();
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        assessment = intent.getParcelableExtra("assessment");
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
}
