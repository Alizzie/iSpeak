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
    private static final HashMap<Integer, String> BODYSDIC;
    private HashMap<String, HashMap<String, Integer>> boDySCriteriaMarking = createBoDySCriteria();
    private HashMap<String, Integer> boDySScores = createBoDySScores();


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
    }

    public HashMap<String, HashMap<String, Integer>> getBoDySCriteria() {
        return boDySCriteriaMarking;
    }

    public void startNewTaskRound(){
        getInfos();
        saveTaskResultsInCSV();
        boDySCriteriaMarking = createBoDySCriteria();
        boDySScores = createBoDySScores();
    }

    private void getInfos(){
        for (String main : boDySScores.keySet()) {
            Log.d("TESTBODYSSCORES", main + ": " + boDySScores.get(main).toString());
        }

        for (String main : boDySCriteriaMarking.keySet()) {
            for (String crit : boDySCriteriaMarking.get(main).keySet()){
                Log.d("TESTBODYSMARKING", crit + ": " + boDySCriteriaMarking.get(main).get(crit).toString());
            }
        }
    }

    @Override
    public List<String[]> onWriteCSV() {
        ArrayList<String> criteriaArray = new ArrayList<>(Arrays.asList("TaskNr"));
        ArrayList<String> markingsArray = new ArrayList<>(Arrays.asList(String.valueOf(taskId)));


        List<String> mainKeys = getMainCriteriaList().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        for (int j = 0; j < mainKeys.size(); j++){
            String main = mainKeys.get(j);
            criteriaArray.add(main);
            markingsArray.add(String.valueOf(boDySScores.get(main)));

            List<String> keys = boDySCriteriaMarking.get(main).keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());

            for(int i = 0; i < keys.size(); i++) {
                criteriaArray.add(keys.get(i));
                markingsArray.add(String.valueOf(boDySCriteriaMarking.get(main).get(keys.get(i))));
            }
        }

        return new ArrayList<>(Arrays.asList(criteriaArray.toArray(new String[0]), markingsArray.toArray(new String[0])));
    }

    @Override
    public String getTaskName(int taskId){
        return BODYSDIC.get(taskId);
    }

    private HashMap<String, HashMap<String, Integer>> createBoDySCriteria(){
        boDySCriteriaMarking = new HashMap<>();
        boDySCriteriaMarking.put("ATM", new HashMap(){{
            put("ATM1", 0);
            put("ATM2", 0);
            put("ATM3", 0);
        }});

        boDySCriteriaMarking.put("STL", new HashMap(){{
            put("STL1", 0);
            put("STL2", 0);
            put("STL3", 0);
            put("STL4", 0);
        }});

        boDySCriteriaMarking.put("STQ", new HashMap(){{
            put("STQ1", 0);
            put("STQ2", 0);
            put("STQ3", 0);
        }});

        boDySCriteriaMarking.put("STS", new HashMap(){{
            put("STS1", 0);
            put("STS2", 0);
            put("STS3", 0);
            put("STS4", 0);
            put("STS5", 0);
        }});

        boDySCriteriaMarking.put("ART", new HashMap(){{
            put("ART1", 0);
            put("ART2", 0);
            put("ART3", 0);
            put("ART4", 0);
            put("ART5", 0);
        }});

        boDySCriteriaMarking.put("RES", new HashMap(){{
            put("RES1", 0);
            put("RES2", 0);
        }});

        boDySCriteriaMarking.put("TEM", new HashMap(){{
            put("TEM1", 0);
            put("TEM2", 0);
        }});

        boDySCriteriaMarking.put("RDF", new HashMap(){{
            put("RDF1", 0);
            put("RDF2", 0);
        }});

        boDySCriteriaMarking.put("MOD", new HashMap(){{
            put("MOD1", 0);
            put("MOD2", 0);
        }});

        return boDySCriteriaMarking;
    }

    private HashMap<String, Integer> createBoDySScores(){
        HashMap<String, Integer> boDySScores = new HashMap<>();
        boDySScores.put("ATM", 4);
        boDySScores.put("STL", 4);
        boDySScores.put("STQ", 4);
        boDySScores.put("STS", 4);
        boDySScores.put("ART", 4);
        boDySScores.put("RES", 4);
        boDySScores.put("TEM", 4);
        boDySScores.put("RDF", 4);
        boDySScores.put("MOD", 4);
        return boDySScores;
    }

    public List<String> getMainCriteriaList(){
        return new ArrayList<>(this.getBoDySCriteria().keySet());
    }

    public HashMap<String, Integer> getBoDySScores() {
        return boDySScores;
    }

    public ArrayList<String> getMainCriteriaWithEmptyMarkings(){
        ArrayList<String> list = new ArrayList<>();

        for (String mainCriteria: getMainCriteriaList()) {
            HashMap<String, Integer> criteriaValues = boDySCriteriaMarking.get(mainCriteria);
            Collection<Integer> values = criteriaValues.values();

            if(!values.contains(1)){
                list.add(mainCriteria);
            }
        }

        return list;
    }

    public void updateScores(String key, int score) {
        boDySScores.put(key, score);
    }
}
