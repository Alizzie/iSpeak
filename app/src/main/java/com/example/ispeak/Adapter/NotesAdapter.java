package com.example.ispeak.Adapter;

import com.example.ispeak.Models.Assessment;

import java.util.List;
import java.util.Set;

public class NotesAdapter extends SimpleCheckboxAdapter{
    public NotesAdapter(Assessment assessment, List<String> options, Set<String> checkedOptions) {
        super(assessment, options, checkedOptions);
    }

    @Override
    protected void addCheckboxListener(String circumstance, boolean isChecked) {
        if (isChecked){
            this.assessment.addNotes(circumstance);
        } else {
            this.assessment.removeNotes(circumstance);
        }
    }
}
