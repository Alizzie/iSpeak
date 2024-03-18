package com.example.ispeak.Models;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import com.example.ispeak.Utils.WriteCSV;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
        boDySSheets[taskId] = this.currentSheet;
    }

    public void startNewTaskRound(){
        currentSheet.getInfos();
        saveTaskResultsInCSV();
        recordings[taskId].setEvaluationScore(currentSheet.getTotalScore());

        taskId = taskId + 1;
        currentSheet = new BoDySSheet();
        boDySSheets[taskId] = this.currentSheet;
    }

    public BoDySSheet[] getBoDySSheets() {
        return boDySSheets;
    }

    public BoDySSheet getCurrentSheet(){
        return currentSheet;
    }

    @Override
    public List<String[]> onWriteCSV() {
        ArrayList<String> criteriaArray = new ArrayList<>(Arrays.asList("TaskNr"));
        ArrayList<String> markingsArray = new ArrayList<>(Arrays.asList(String.valueOf(taskId)));


        List<String> mainKeys = currentSheet.getMainCriteriaList().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        for (int j = 0; j < mainKeys.size(); j++){
            String main = mainKeys.get(j);
            criteriaArray.add(main);
            markingsArray.add(String.valueOf(currentSheet.getBoDySScores().get(main)));

            List<String> keys = currentSheet.getBoDySCriteria().get(main).keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());

            for(int i = 0; i < keys.size(); i++) {
                criteriaArray.add(keys.get(i));
                markingsArray.add(String.valueOf(currentSheet.getBoDySCriteria().get(main).get(keys.get(i))));
            }
        }

        return new ArrayList<>(Arrays.asList(criteriaArray.toArray(new String[0]), markingsArray.toArray(new String[0])));
    }

    @Override
    public String getTaskName(int taskId){
        return BODYSDIC.get(taskId);
    }
}
