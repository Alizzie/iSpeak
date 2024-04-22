package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ispeak.Adapter.BoDySMainCriteriaAdapter;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.databinding.ActivityBodysFrequencyObservationBinding;

public class BoDySFrequencyObservationActivity extends BaseApp {
    private ActivityBodysFrequencyObservationBinding binding;
    private int assessmentNr;
    private BoDyS assessment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void init(){
        this.assessment = (BoDyS) patientInfo.getAssessmentList().get(assessmentNr);
        enableNavBackArrow();
        initFrequencyObservationRecyclerView();
    }

    @Override
    public void listenBtn() {

    }

    @Override
    public void setBinding() {
        binding = ActivityBodysFrequencyObservationBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    private void initFrequencyObservationRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.criteriaFrequencyRecyclerView.setHasFixedSize(true);
        binding.criteriaFrequencyRecyclerView.setLayoutManager(layoutManager);


        BoDySMainCriteriaAdapter adapter = new BoDySMainCriteriaAdapter(assessment, binding.criteriaFrequencyRecyclerView, getApplicationContext());
        binding.criteriaFrequencyRecyclerView.setAdapter(adapter);
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
            navigateToNextActivity(this, BoDySOverviewPageActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }
}
