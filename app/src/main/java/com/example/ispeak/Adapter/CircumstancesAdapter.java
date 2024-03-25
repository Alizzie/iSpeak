package com.example.ispeak.Adapter;

import android.util.Log;

import com.example.ispeak.Models.Assessment;
import com.example.ispeak.Models.BoDyS;
import java.util.List;
import java.util.Set;

public class CircumstancesAdapter extends SimpleCheckboxAdapter{

    public CircumstancesAdapter(Assessment assessment, List<String> options, Set<String> checkedOptions) {
        super(assessment, options, checkedOptions);
    }

    @Override
    protected void addCheckboxListener(String circumstance, boolean isChecked) {
        if (isChecked){
            this.assessment.addCircumstances(circumstance);
        } else {
            this.assessment.removeCircumstances(circumstance);
        }
    }
}