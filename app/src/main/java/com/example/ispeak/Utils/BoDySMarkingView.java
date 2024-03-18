package com.example.ispeak.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ispeak.Interfaces.NotesObserver;
import com.example.ispeak.Models.BoDyS;
import com.example.ispeak.Models.BoDySSheet;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.R;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BoDySMarkingView extends ConstraintLayout implements NotesObserver {
    private BoDySSheet assessmentSheet;
    public BoDySMarkingView(@NonNull Context context) {
        super(context);
    }

    public BoDySMarkingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BoDySMarkingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBoDySSheet(BoDySSheet sheet) {
        this.assessmentSheet = sheet;
        assessmentSheet.getInfos();
        initBoDySCriteria();
    }

    private void initBoDySCriteria(){
        HashMap<String, HashMap<String, Integer>> criteriaMap = assessmentSheet.getBoDySCriteria();
        List<String> mainCriteriaList = assessmentSheet.getMainCriteriaList();

        for (String mainCriteria: mainCriteriaList) {
            setClickListenerOnMainCriteria(mainCriteria);

            HashMap<String, Integer> criteriaValues = criteriaMap.get(mainCriteria);

            assert criteriaValues != null;
            setClickListenerOnCriteria(mainCriteria, criteriaValues);
        }
    }

    private void setClickListenerOnMainCriteria(String mainCriteria) {
        int btnId = getContext().getResources().getIdentifier(mainCriteria, "id", getContext().getPackageName());
        MaterialButton btn = findViewById(btnId);

        btn.setOnClickListener(view -> {
            BoDySNotesPopup popup = new BoDySNotesPopup(this);
            popup.showPopup(this.getContext(), view, mainCriteria, assessmentSheet.getBoDySNotes().get(mainCriteria));
        });
    }

    private void setClickListenerOnCriteria(String mainCriteria, HashMap<String, Integer> criteriaValues){
        for (String criteria : criteriaValues.keySet()) {
            int btnId = getContext().getResources().getIdentifier(criteria, "id", getContext().getPackageName());
            MaterialButton btn = findViewById(btnId);
            btn.setCheckable(true);

            if(criteriaValues.get(criteria) == 1) {
                btn.setChecked(true);
                toggleBtnBackgroundColor(btn);
            }

            btn.setOnClickListener(view -> {
                toggleBtnBackgroundColor((MaterialButton) view);

                int changeChecked = criteriaValues.get(criteria) == 0? 1: 0;
                Objects.requireNonNull(criteriaValues.put(criteria, changeChecked));
                assessmentSheet.updateScores(mainCriteria, -1);
            });
        }
    }

    private void toggleBtnBackgroundColor(MaterialButton btn){
        if(!btn.isChecked()) {
            btn.setBackgroundColor(getContext().getColor(R.color.lightGray));
        } else {
            btn.setBackgroundColor(getContext().getColor(R.color.lightBlue));
        }
    }

    @Override
    public void onSaveNote(String criteria, String note) {
        assessmentSheet.updateBoDySNotes(criteria, note);
    }
}
