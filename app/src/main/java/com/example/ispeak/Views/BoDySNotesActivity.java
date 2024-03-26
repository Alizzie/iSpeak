package com.example.ispeak.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ispeak.Adapter.CircumstancesAdapter;
import com.example.ispeak.Adapter.NotesAdapter;
import com.example.ispeak.Adapter.SimpleCheckboxAdapter;
import com.example.ispeak.Models.Assessment;
import com.example.ispeak.Models.Patient;
import com.example.ispeak.databinding.ActivityBodysNotesBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoDySNotesActivity extends BaseApp {

    private ActivityBodysNotesBinding binding;
    private int assessmentNr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBodysNotesBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        initCircumstancesListView();
        initCommentsListView();
        listenContinueBtn();
    }

    private void initCircumstancesListView(){
        Assessment assessment = Patient.getInstance().getAssessmentList().get(assessmentNr);
        List<String> preOptions = new ArrayList<>(Arrays.asList("Erkältung", "Starkes Rauchen", "Vorbestehender Sigmatismus", "Antriebsminderung", "Pathologisches Lachen/ Weinen"));
        Set<String> options = assessment.getCircumstances();
        List<String> combinedList = getCombinedList(preOptions, options);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.circumstancesListView.setHasFixedSize(true);
        binding.circumstancesListView.setLayoutManager(layoutManager);

        CircumstancesAdapter adapter = new CircumstancesAdapter(assessment, combinedList, options);
        binding.circumstancesListView.setAdapter(adapter);

        addCustomInputs(combinedList, adapter, binding.circumstancesTxtInput, binding.circumstancesListView);
    }

    private void initCommentsListView(){
        Assessment assessment = Patient.getInstance().getAssessmentList().get(assessmentNr);
        List<String> preOptions = new ArrayList<>(Arrays.asList("Fremdsprachenakzent", "Dialekt"));
        Set<String> options = assessment.getNotes();
        List<String> combinedList = getCombinedList(preOptions, options);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.commentsListView.setHasFixedSize(true);
        binding.commentsListView.setLayoutManager(layoutManager);

        NotesAdapter adapter = new NotesAdapter(assessment, combinedList, options);
        binding.commentsListView.setAdapter(adapter);

        addCustomInputs(combinedList, adapter, binding.commentsTxtInput, binding.commentsListView);
    }

    private List<String> getCombinedList(List<String> list1, Set<String> set1){
        Set<String> combinedSet = new HashSet<>(list1);
        combinedSet.addAll(set1);
        return new ArrayList<>(combinedSet);
    }

    private void addCustomInputs(List<String> combinedList, SimpleCheckboxAdapter adapter, EditText editText, RecyclerView recyclerView){
        editText.setOnEditorActionListener((view, actionId, event) -> {

            if(actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String newText = view.getText().toString();
                combinedList.add(0, newText);
                adapter.addCheckedOptions(newText);
                adapter.notifyItemInserted(0);
                recyclerView.smoothScrollToPosition(0);
                editText.getText().clear();
                return true;
            }
            return false;
        });
    }

    private void listenContinueBtn(){
        binding.confirmBtn.setOnClickListener(view -> {
            Patient.getInstance().getAssessmentList().get(assessmentNr).updateAssessmentNotesInCSV();
            navigateToNextActivity(this, BoDySOverviewPageActivity.class);
        });
    }

    @Override
    public void prepareIntent(Intent intent) {
        intent.putExtra("assessmentNr", assessmentNr);
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        assessmentNr = intent.getIntExtra("assessmentNr",  -1);
    }
}
