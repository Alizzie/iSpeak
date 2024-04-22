package com.example.ispeak.Models;

import android.util.Log;
import com.example.ispeak.Utils.BoDySStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class BoDySSheet {
    private HashMap<String, HashMap<String, Integer>> boDySCriteriaMarking = createBoDySCriteria();
    private HashMap<String, Integer> boDySScores = createBoDySScores();
    private HashMap<String, String> boDySNotes = createBoDySNotes();
    private BoDySStatus status;

    public BoDySSheet(){
        this.status = BoDySStatus.UNKNOWN;
    }

    public ArrayList<String> getMainCriteriaWithEmptyMarkings(){
        ArrayList<String> list = new ArrayList<>();

        for (String mainCriteria: getMainCriteriaList()) {
            HashMap<String, Integer> criteriaValues = boDySCriteriaMarking.get(mainCriteria);
            Collection<Integer> values = criteriaValues != null ? criteriaValues.values() : null;

            if(!(values != null && values.contains(1))){
                list.add(mainCriteria);
            }
        }

        return list;
    }

    private boolean checkIfMainCriteriaMarked(String mainCriteria){
        HashMap<String, Integer> criteriaValues = boDySCriteriaMarking.get(mainCriteria);
        Collection<Integer> values = criteriaValues != null ? criteriaValues.values() : null;

        return !(values != null && values.contains(1));
    }


    public void updateMarkings(String mainCriteria, String criteria, int marked) {
        if(!boDySCriteriaMarking.containsKey(mainCriteria)){
            return;
        }

        if(!Objects.requireNonNull(boDySCriteriaMarking.get(mainCriteria)).containsKey(criteria)){
            return;
        }

        Objects.requireNonNull(boDySCriteriaMarking.get(mainCriteria)).put(criteria, marked);
    }

    public void updateScores(String mainCriteria, int score, boolean restoreProcess) {
        if(checkIfMainCriteriaMarked(mainCriteria) && !restoreProcess) {
            boDySScores.put(mainCriteria, 4);
        } else {
            boDySScores.put(mainCriteria, score);
        }
    }

    public void updateNotes(String criteria, String note) {
        if(!boDySNotes.containsKey(criteria)){
            return;
        }

        boDySNotes.put(criteria, note);
    }

    public boolean hasEmptyScores(){
        Collection<Integer> values = boDySScores.values();
        return values.contains(-1);
    }

    public int getTotalScore(){
        Collection<Integer> scores = boDySScores.values();
        return scores.stream().reduce(0, Integer::sum);
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
        HashMap<String, Integer> boDySScores = new LinkedHashMap<>();
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

    public BoDySStatus getStatus() {
        return status;
    }

    public void setStatus(BoDySStatus status) {
        this.status = status;
    }

    public HashMap<String, String> getBoDySNotes() {
        return boDySNotes;
    }

    public List<String> getMainCriteriaList(){
        return new ArrayList<>(this.getBoDySCriteria().keySet());
    }

    public HashMap<String, Integer> getBoDySScores() {
        return boDySScores;
    }
}
