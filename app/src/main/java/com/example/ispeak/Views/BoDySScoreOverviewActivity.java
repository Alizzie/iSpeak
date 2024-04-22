package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.BoDySSheet;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.R;
import com.example.ispeak.databinding.ActivityBodysScoresOverviewBinding;

public class BoDySScoreOverviewActivity extends BaseApp {
    private ActivityBodysScoresOverviewBinding binding;
    private int assessmentNr;
    private BoDyS assessment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableNavBackArrow();
    }

    @Override
    public void init(){
        this.assessment = (BoDyS) patientInfo.getAssessmentList().get(assessmentNr);
        initScoreItems();
    }

    @Override
    public void listenBtn() {

    }

    @Override
    public void setBinding() {
        binding = ActivityBodysScoresOverviewBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    private void initScoreItems(){
        GridLayout gridLayout = binding.scoresViewWrapper;

        for(int i = 0; i < gridLayout.getChildCount(); i++) {
            CardView child = (CardView) gridLayout.getChildAt(i);
            TextView criteriaTxt = child.findViewById(R.id.criteriaTxt);
            criteriaTxt.setText(BoDyS.BODYSCRITERIA.get(i));
            updateScoreItem(child, BoDyS.BODYSCRITERIA.get(i));
        }
    }

    private void updateScoreItem(CardView cardView, String criteria){
        TextView scoreHistory = cardView.findViewById(R.id.scoreHistory);
        TextView totalCriteriaScore = cardView.findViewById(R.id.totalCriteriaScore);
        Integer totalScore = 0;
        StringBuilder scores = new StringBuilder();

        for(int i = 0; i < assessment.getMaxRecordingNr(); i++){
            BoDySSheet currentSheet = assessment.getBoDySSheets()[i];
            int score = currentSheet.getBoDySScores().get(criteria);
            totalScore = updateScore(scores, score, totalScore, currentSheet);
        }

        scoreHistory.setText(scores);
        totalCriteriaScore.setText(String.valueOf(totalScore));
    }

    private int updateScore(StringBuilder scores, int score, int totalScore, BoDySSheet sheet){

        if(score == -1 || !sheet.getStatus().isEvaluated()){
            scores.append("-");
        } else {
            totalScore = totalScore + score;
            scores.append(score);
        }

        scores.append(" | ");
        return totalScore;
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
