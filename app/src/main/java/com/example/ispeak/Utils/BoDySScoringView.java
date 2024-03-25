package com.example.ispeak.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ispeak.Interfaces.ScoreBoardObserver;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.BoDySSheet;
import com.example.ispeak.Models.Patient;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;

public class BoDySScoringView extends ConstraintLayout implements ScoreBoardObserver {
    private BoDySSheet assessmentSheet;
    public BoDySScoringView(@NonNull Context context) {
        super(context);
    }

    public BoDySScoringView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BoDySScoringView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBoDySSheet(BoDySSheet sheet, boolean readOnly) {
        this.assessmentSheet = sheet;

        if(!readOnly){
            initBoDySCriteria();
        }
    }

    private void initBoDySCriteria(){
        HashMap<String, HashMap<String, Integer>> criteriaMap = assessmentSheet.getBoDySCriteria();
        List<String> mainCriteriaList = assessmentSheet.getMainCriteriaList();

        for (String mainCriteria: mainCriteriaList) {
            HashMap<String, Integer> criteriaValues = criteriaMap.get(mainCriteria);

            assert criteriaValues != null;
            setClickListenerOnCriteriaScores(mainCriteria);
        }

        updateScoringVisuals();
    }

    private void setClickListenerOnCriteriaScores(String mainCriteria) {
        int btnId = getResources().getIdentifier(mainCriteria+"Score", "id", getContext().getPackageName());
        MaterialButton mainCriteriaBtn = findViewById(btnId);

        mainCriteriaBtn.setOnClickListener(view -> {
            ScoreBoardPopup popup = new ScoreBoardPopup(this);
            popup.showPopup(this.getContext(), view, mainCriteria);
        });
    }

    public void updateScoringVisuals(){
        HashMap<String, Integer> boDySScores = assessmentSheet.getBoDySScores();

        List<String> emptyMarkingsCriteria = assessmentSheet.getMainCriteriaWithEmptyMarkings();
        Log.d("TESTSMARKINGS", emptyMarkingsCriteria.toString());

        for(String criteria: boDySScores.keySet()) {
            int btnId = getResources().getIdentifier(criteria+"Score", "id", getContext().getPackageName());
            MaterialButton btn = findViewById(btnId);

            if(emptyMarkingsCriteria.contains(criteria)) {
                btn.setText(String.valueOf(4));
                btn.setClickable(false);
            } else {
                btn.setClickable(true);
                int score = boDySScores.get(criteria);
                Log.d("TESTSMARKINGS", String.valueOf(score));

                if(score == -1) {
                    btn.setText("--");
                } else {
                    btn.setText(String.valueOf(score));
                }
            }
        }
    }


    @Override
    public void onScoreBoardClicked(String criteria, int score) {
        assessmentSheet.updateScores(criteria, score, false);
        updateScoringVisuals();
    }
}
