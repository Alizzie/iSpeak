package com.example.ispeak.Models;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import com.example.ispeak.Utils.BoDySScoringView;
import com.example.ispeak.Utils.ReadCSV;
import com.example.ispeak.Utils.WriteCSV;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BoDyS extends Assessment{
    public static final HashMap<Integer, String> BODYSDIC;
    private BoDySSheet[] boDySSheets;
    private BoDySSheet currentSheet;


    static {
        BODYSDIC = new HashMap<>();
        BODYSDIC.put(0, "Spont 1");
        BODYSDIC.put(1, "Nach 1");
        BODYSDIC.put(2, "Les 1");
        BODYSDIC.put(3, "Bild 1");
        BODYSDIC.put(4, "Spont 2");
        BODYSDIC.put(5, "Nach 2");
        BODYSDIC.put(6, "Les 2");
        BODYSDIC.put(7, "Bild 2");
    }

    public BoDyS(){
        super("BoDyS", 8);
        this.boDySSheets = new BoDySSheet[this.maxRecordingNr];
        this.currentSheet = new BoDySSheet();
        this.boDySSheets[taskId] = this.currentSheet;
        this.circumstances = new HashSet<>();
        this.notes = new HashSet<>();
    }

    public void continueBoDyS(int taskId){
        this.taskId = taskId;
        this.currentSheet = new BoDySSheet();
        boDySSheets[taskId] = this.currentSheet;
    }

    public void startNewTaskRound(){
        currentSheet.getInfos();
        saveTaskResultsInCSV();

        if(taskId+1 < maxRecordingNr) {
            continueBoDyS(taskId + 1);
        }
    }

    public void saveEvaluationData(){
        currentSheet.setPrefill(false);
        updateTaskResultsInCSV();
    }

    @Override
    public List<String[]> onWriteCSV() {
        ArrayList<String> criteriaArray = new ArrayList<>(Arrays.asList("TaskNr", "Status"));

        String prefill = currentSheet.isPrefill() ? "Prefill" : "Evaluated";
        ArrayList<String> markingsArray = new ArrayList<>(Arrays.asList(String.valueOf(taskId), prefill));


        List<String> mainKeys = currentSheet.getMainCriteriaList().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        for (int j = 0; j < mainKeys.size(); j++){
            String main = mainKeys.get(j);
            criteriaArray.add(main);
            criteriaArray.add(main+"Notes");
            markingsArray.add(String.valueOf(currentSheet.getBoDySScores().get(main)));

            String note = currentSheet.getBoDySNotes().get(main);

            if(note != null){
                note = note.replaceAll("\n", " / ");
            } else {
                note = "null";
            }

            markingsArray.add(note);

            List<String> keys = currentSheet.getBoDySCriteria().get(main).keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());

            for(int i = 0; i < keys.size(); i++) {
                criteriaArray.add(keys.get(i));
                markingsArray.add(String.valueOf(currentSheet.getBoDySCriteria().get(main).get(keys.get(i))));
            }
        }

        return new ArrayList<>(Arrays.asList(criteriaArray.toArray(new String[0]), markingsArray.toArray(new String[0])));
    }

    @Override
    public void retrieveConcreteAssessment(ArrayList<String[]> lines) {

        String[] headline = lines.get(3);

        for(int i = 4; i < lines.size(); i++) {
            String[] line = lines.get(i);
            restoreBoDysSheet(headline, line);
        }
    }

    private void restoreBoDysSheet(String[] headline, String[] line){
        BoDySSheet boDySSheet = new BoDySSheet();
        int taskId = Integer.parseInt(line[0]);
        boolean prefill = Objects.equals(line[1], "Prefill");

        String mainCriteriaFocus = "";
        for(int lineIndex = 2; lineIndex < headline.length; lineIndex++){

            if(containsNumber(headline[lineIndex]) && headline[lineIndex].length() == 4){
                int marking = Integer.parseInt(line[lineIndex]);
                restoreMarking(boDySSheet, mainCriteriaFocus, headline[lineIndex], marking);
            } else if(headline[lineIndex].length() == 8){
                String note = Objects.equals(line[lineIndex], "null") ? null : line[lineIndex];

                if(note != null){
                    note = note.replaceAll("/", "\n");
                }

                boDySSheet.updateBoDySNotes(mainCriteriaFocus, note);
            } else {
                mainCriteriaFocus = headline[lineIndex];
                int score = Integer.parseInt(line[lineIndex]);
                boDySSheet.updateScores(headline[lineIndex], score, true);
            }
        }

        boDySSheet.setPrefill(prefill);
        boDySSheets[taskId] = boDySSheet;
    }

    private void restoreMarking(BoDySSheet boDySSheet, String mainCriteriaFocus, String criteria, int marking){
        boDySSheet.updateMarkings(mainCriteriaFocus, criteria, marking);
    }

    private static boolean containsNumber(String input) {
        Pattern pattern = Pattern.compile(".*\\d.*");
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public BoDySSheet[] getBoDySSheets() {
        return boDySSheets;
    }

    public BoDySSheet getCurrentSheet(){
        return currentSheet;
    }

    public void setCurrentSheet(BoDySSheet currentSheet) {
        this.currentSheet = currentSheet;
    }

    @Override
    public String getTaskName(int taskId){
        return BODYSDIC.get(taskId);
    }

}
