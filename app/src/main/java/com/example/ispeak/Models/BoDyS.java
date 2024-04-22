package com.example.ispeak.Models;

import com.example.ispeak.Utils.AssessmentFactory;
import com.example.ispeak.Utils.BoDySStatus;
import com.example.ispeak.Utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BoDyS extends Assessment{
    public static final HashMap<Integer, String> BODYSDIC, BODYSCRITERIA;
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

        BODYSCRITERIA = new HashMap<>();
        BODYSCRITERIA.put(0, "ATM");
        BODYSCRITERIA.put(1, "STL");
        BODYSCRITERIA.put(2, "STQ");
        BODYSCRITERIA.put(3, "STS");
        BODYSCRITERIA.put(4, "ART");
        BODYSCRITERIA.put(5, "RES");
        BODYSCRITERIA.put(6, "TEM");
        BODYSCRITERIA.put(7, "RDF");
        BODYSCRITERIA.put(8, "MOD");
    }

    public BoDyS(){
        super(AssessmentFactory.AssessmentNames.BoDyS.name(), 8);
        this.boDySSheets = new BoDySSheet[this.MAXRECORDING];
        this.currentSheet = new BoDySSheet();
        this.boDySSheets[taskId] = this.currentSheet;
    }

    public void setNextBoDySTask(int taskId){
        this.taskId = taskId;
        this.currentSheet = new BoDySSheet();
        boDySSheets[taskId] = this.currentSheet;
    }

    @Override
    public void startNewTaskRound(){
        currentSheet.setStatus(BoDySStatus.PREFILL);
        saveTaskResultsInCSV();

        if(taskId+1 < MAXRECORDING) {
            setNextBoDySTask(taskId + 1);
        }
    }

    @Override
    public void skipTaskRound() {
        currentSheet.setStatus(BoDySStatus.SKIPPED);
        saveTaskResultsInCSV();

        if(taskId+1 < MAXRECORDING) {
            setNextBoDySTask(taskId + 1);
        }
    }

    public void saveEvaluationData(){
        currentSheet.setStatus(BoDySStatus.EVALUATED);
        updateTaskResultsInCSV();
    }

    @Override
    public void retrieveConcreteAssessment(ArrayList<String[]> lines) {

        String[] headline = lines.get(3);
        int assessmentDataOffset = 4;

        for(int i = 0; i < getMaxRecordingNr(); i++) {

            if((i + assessmentDataOffset) >= lines.size()){
                boDySSheets[i] = new BoDySSheet();
            } else {
                String[] line = lines.get(i + assessmentDataOffset);
                restoreBoDysSheet(headline, line);
            }
        }


    }
    @Override
    public List<String[]> onWriteCSV() {
        ArrayList<String> csvKeys = new ArrayList<>(Arrays.asList("TaskNr", "Status"));

        ArrayList<String> csvValues = initCSVAssessmentData();
        List<String> mainKeys = sortAlphabeticMainCriteriaList();

        for (int j = 0; j < mainKeys.size(); j++){
            String mainCriteria = mainKeys.get(j);
            appendCSVKeys(csvKeys, mainCriteria);

            appendCSVScore(mainCriteria, csvValues);
            appendCSVNotes(mainCriteria, csvValues);
            appendCSVMarkings(mainCriteria, csvValues, csvKeys);
        }

        return new ArrayList<>(Arrays.asList(csvKeys.toArray(new String[0]), csvValues.toArray(new String[0])));
    }

    private ArrayList<String> initCSVAssessmentData(){
        String statusName = currentSheet.getStatus().getStatusName();
        return new ArrayList<>(Arrays.asList(String.valueOf(taskId), statusName));
    }

    private List<String> sortAlphabeticMainCriteriaList(){
        return currentSheet.getMainCriteriaList().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    private List<String> sortNaturalCriteriaList(String mainCriteria){
        return Objects.requireNonNull(currentSheet.getBoDySCriteria().get(mainCriteria)).keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    private void appendCSVKeys(ArrayList<String> csvKeys, String mainCriteria) {
        csvKeys.add(mainCriteria);
        csvKeys.add(mainCriteria+"Notes");
    }

    private void appendCSVScore(String mainCriteria, ArrayList<String> csvValues){
        csvValues.add(String.valueOf(currentSheet.getBoDySScores().get(mainCriteria)));
    }

    private void appendCSVNotes(String mainCriteria, ArrayList<String> csvValues) {
        String note = currentSheet.getBoDySNotes().get(mainCriteria);

        if(note != null){
            note = note.replaceAll("\n", " / ");
        } else {
            note = "null";
        }

        csvValues.add(note);
    }

    private void appendCSVMarkings(String mainCriteria, ArrayList<String> csvValues, ArrayList<String> csvKeys){
        List<String> keys = sortNaturalCriteriaList(mainCriteria);
        for(int i = 0; i < keys.size(); i++) {
            csvKeys.add(keys.get(i));
            csvValues.add(String.valueOf(Objects.requireNonNull(currentSheet.getBoDySCriteria().get(mainCriteria)).get(keys.get(i))));
        }
    }

    private void restoreBoDysSheet(String[] headline, String[] line){
        BoDySSheet boDySSheet = readBoDySSheetInfos(line);

        String mainCriteriaFocus = "";
        for(int lineIndex = 2; lineIndex < headline.length; lineIndex++){

            if(Utils.containsNumber(headline[lineIndex]) && headline[lineIndex].length() == 4){
                int marking = Integer.parseInt(line[lineIndex]);
                restoreMarking(boDySSheet, mainCriteriaFocus, headline[lineIndex], marking);
            } else if(headline[lineIndex].length() == 8){
                String note = Objects.equals(line[lineIndex], "null") ? null : line[lineIndex];

                if(note != null){
                    note = note.replaceAll("/", "\n");
                }

                boDySSheet.updateNotes(mainCriteriaFocus, note);
            } else {
                mainCriteriaFocus = headline[lineIndex];
                int score = Integer.parseInt(line[lineIndex]);
                boDySSheet.updateScores(headline[lineIndex], score, true);
            }
        }
    }

    private BoDySSheet readBoDySSheetInfos(String[] line){
        BoDySSheet boDySSheet = new BoDySSheet();
        int taskId = Integer.parseInt(line[0]);
        String status = line[1];

        boDySSheet.setStatus(BoDySStatus.fromString(status));
        boDySSheets[taskId] = boDySSheet;

        return boDySSheet;
    }


    private void restoreMarking(BoDySSheet boDySSheet, String mainCriteria, String criteria, int marking){
        boDySSheet.updateMarkings(mainCriteria, criteria, marking);
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
