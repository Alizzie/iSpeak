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

import java.util.Iterator;

public class BoDySScoreOverviewActivity extends BaseApp {
    private ActivityBodysScoresOverviewBinding binding;
    private int assessmentNr;
    private BoDyS assessment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBodysScoresOverviewBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
    }

    private void init(){
        this.assessment = (BoDyS) Patient.getInstance().getAssessmentList().get(assessmentNr);
        enableNavBackArrow();
        initScoreItems();
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
        int totalScore = 0;
        StringBuilder scores = new StringBuilder();

        for(int i = 0; i < assessment.getMaxRecordingNr(); i++){
            BoDySSheet currentSheet = assessment.getBoDySSheets()[i];
            int score = currentSheet.getBoDySScores().get(criteria);

            if(score == -1 || !currentSheet.getStatus().isEvaluated()){
                scores.append("-");
            } else {
                totalScore += score;
                scores.append(score);
            }

            scores.append(" | ");
        }

        scoreHistory.setText(scores);
        totalCriteriaScore.setText(String.valueOf(totalScore));
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
