package com.example.ispeak.Models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class BoDySSheet {
    private HashMap<String, HashMap<String, Integer>> boDySCriteriaMarking = createBoDySCriteria();
    private HashMap<String, Integer> boDySScores = createBoDySScores();
    private HashMap<String, String> boDySNotes = createBoDySNotes();
    private boolean prefill;
    private int totalScore;

    public HashMap<String, String> getBoDySNotes() {
        return boDySNotes;
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

    private boolean checkUnmarkedMainCriteria(String criteria){
        HashMap<String, Integer> criteriaValues = boDySCriteriaMarking.get(criteria);
        Collection<Integer> values = criteriaValues.values();

        if(!values.contains(1)){
            return true;
        }

        return false;
    }


    public void updateMarkings(String mainKey, String key, int marked) {
        if(!boDySCriteriaMarking.keySet().contains(mainKey)){
            return;
        }

        if(!boDySCriteriaMarking.get(mainKey).keySet().contains(key)){
            return;
        }

        boDySCriteriaMarking.get(mainKey).put(key, marked);
    }

    public void updateScores(String key, int score, boolean restoreProcess) {
        if(checkUnmarkedMainCriteria(key) && !restoreProcess) {
            boDySScores.put(key, 4);
        } else {
            boDySScores.put(key, score);
            boDySScores.put(key, score);
        }
    }

    public void updateBoDySNotes(String criteria, String note) {
        if(!boDySNotes.keySet().contains(criteria)){
            return;
        }
        boDySNotes.put(criteria, note);
    }

    public boolean hasEmptyScores(){
        Collection<Integer> values = boDySScores.values();
        if(values.contains(-1)){
            return true;
        }

        return false;
    }

    public int getTotalScore(){
        Collection<Integer> scores = boDySScores.values();
        totalScore = scores.stream().reduce(0,(x, y) -> x + y);
        return totalScore;
    }

    public HashMap<String, HashMap<String, Integer>> getBoDySCriteria() {
        return boDySCriteriaMarking;
    }

    public void getInfos() {
        for (String main : boDySScores.keySet()) {
            Log.d("TESTBODYSSCORES", main + ": " + boDySScores.get(main).toString());
        }

        for (String main : boDySCriteriaMarking.keySet()) {
            for (String crit : boDySCriteriaMarking.get(main).keySet()) {
                Log.d("TESTBODYSMARKING", crit + ": " + boDySCriteriaMarking.get(main).get(crit).toString());
            }
        }

        for (String main : boDySNotes.keySet()) {
            Log.d("TESTBODYSNOTES", main + ": " + boDySNotes.get(main));
        }
    }

    public boolean isPrefill() {
        return prefill;
    }

    public void setPrefill(boolean prefill) {
        this.prefill = prefill;
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

    private HashMap<String, String> createBoDySNotes() {
        HashMap<String, String> boDySNotes = new HashMap<>();
        boDySNotes.put("ATM", null);
        boDySNotes.put("STL", null);
        boDySNotes.put("STQ", null);
        boDySNotes.put("STS", null);
        boDySNotes.put("ART", null);
        boDySNotes.put("RES", null);
        boDySNotes.put("TEM", null);
        boDySNotes.put("RDF", null);
        boDySNotes.put("MOD", null);
        return boDySNotes;
    }



}
