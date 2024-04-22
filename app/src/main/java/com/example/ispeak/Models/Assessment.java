package com.example.ispeak.Models;

import android.media.MediaMetadataRetriever;

import com.example.ispeak.Interfaces.IFolderStructureCreator;
import com.example.ispeak.Interfaces.IWriteCSV;
import com.example.ispeak.Utils.ReadCSV;
import com.example.ispeak.Utils.Utils;
import com.example.ispeak.Utils.WriteCSV;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Assessment implements IFolderStructureCreator, IWriteCSV {

    protected String assessmentFolderPath, assessmentName;
    protected Recording[] recordings;
    protected int taskId;
    protected int MAXRECORDING;
    protected Set<String> circumstances;
    protected Set<String> notes;
    private boolean isCompleted;

    public Assessment(String assessmentName, int maxRecordingNr){
        this.assessmentName = assessmentName;
        this.taskId = 0;
        this.MAXRECORDING = maxRecordingNr;
        this.recordings = new Recording[maxRecordingNr];
        this.isCompleted = false;
        this.circumstances = new HashSet<>();
        this.notes = new HashSet<>();
        assessmentFolderPath = Patient.getPatient().getPatientFolderPath() +
                File.separator + assessmentName;
        createFolderStructure();
    }

    protected void saveTaskResultsInCSV(){
        WriteCSV writeCSV = new WriteCSV();

        String csvFolder = assessmentFolderPath + File.separator + "CSV" + File.separator;
        List<String> csvFiles = new ArrayList<>(Arrays.asList(csvFolder + "Assessment.csv", csvFolder + "Events.csv"));
        writeCSV.storeAssessmentDataCSVNote(this, csvFiles.get(0));
        writeCSV.storeRecordingEventDataCSVNote(this, csvFiles.get(1));
    }

    protected void updateTaskResultsInCSV(){
        WriteCSV writeCSV = new WriteCSV();

        String csvFolder = assessmentFolderPath + File.separator + "CSV" + File.separator;
        List<String> csvFiles = new ArrayList<>(Arrays.asList(csvFolder + "Assessment.csv", csvFolder + "Events.csv"));
        writeCSV.updateDataCSVNote(this, csvFiles.get(0));
        writeCSV.updateEventDataCSVNote(this, csvFiles.get(1));
    }

    public void updateAssessmentNotesInCSV(){
        WriteCSV writeCSV = new WriteCSV();
        String filepath = assessmentFolderPath + File.separator + "CSV" + File.separator + "Assessment.csv";
        writeCSV.updateAssessmentNotes(filepath, String.valueOf(isCompleted), getFormattedCircumstances(), getFormattedNotes());
    }

    public void finishAssessment(){
        isCompleted = true;
        WriteCSV writeCSV = new WriteCSV();
        String csvFolder = assessmentFolderPath + File.separator + "CSV" + File.separator;
        List<String> csvFiles = new ArrayList<>(Arrays.asList(csvFolder + "Results.csv", csvFolder + "Assessment.csv", csvFolder + "Events.csv"));
        updateAssessmentNotesInCSV();
        writeCSV.sumTotalAssessment(csvFiles);
    }

    public boolean retrieveAssessment(File file) {
        File recordingsDir = new File(file, "Recordings");
        File csvDir = new File(file, "CSV");
        ArrayList<String[]> lines = readLines(new File(csvDir, "Assessment.csv"));

        if (recordingsDir.exists() && recordingsDir.isDirectory() && csvDir.exists() && csvDir.isDirectory()) {
            retrieveRecordings(recordingsDir, csvDir);
            //lines.get(0) = notes headline

            if(lines.size() == 0) {
                return false;
            }

            retrieveAssessmentSideNotes(lines.get(1));
            this.retrieveConcreteAssessment(lines);
        }

        return true;
    }

    private void retrieveAssessmentSideNotes(String[] line) {
        retrieveAssessmentCompletion(line[0]);
        retrieveAssessmentCircumstances(line[1]);
        retrieveAssessmentNotes(line[2]);
    }

    private void retrieveAssessmentCompletion(String lineItem){
        this.isCompleted = lineItem.equals("true");
    }

    private void retrieveAssessmentCircumstances(String lineItem){
        String[] circumstances = lineItem.split("/");
        if(circumstances.length >= 1 && !lineItem.trim().isEmpty() && !lineItem.equals("/")){
            this.circumstances = new HashSet<>(Arrays.asList(circumstances));
        }
    }

    private void retrieveAssessmentNotes(String lineItem){
        String[] comments = lineItem.split("/");
        if(comments.length >= 1 && !lineItem.trim().isEmpty() && !lineItem.equals("/")){
            this.notes = new HashSet<>(Arrays.asList(comments));
        }
    }

    private void retrieveRecordings(File recordingsDir, File recordingEventCsvDir){
        File[] recordingFiles = recordingsDir.listFiles();

        if(recordingFiles == null) {
            return;
        }

        Arrays.sort(recordingFiles);

        for (File recordingFile : recordingFiles) {
            retrieveRecording(recordingFile, recordingEventCsvDir);
        }
    }

    private void retrieveRecording(File recordingFile, File csvDir){
        Recording recording = createRecording(recordingFile);
        int recordingIndex = -1;

        if(recording != null) {
            recordingIndex = calculateRecordingIndex(recording.getMp3Filepath());
            recording.setEvents(retrieveRecordingEvents(csvDir, recording, recordingIndex));
        }

        if(recordingIndex != -1){
            recordings[recordingIndex] = recording;
        } else{
            Utils.deleteFilesFromDir(recordingFile);
        }
    }

    private ArrayList<Event> retrieveRecordingEvents(File csvDir, Recording recording, int taskId){
        File eventFile = new File(csvDir, "Events.csv");

        if(eventFile.exists()){
            ReadCSV readCSV = new ReadCSV();
            return readCSV.readEventDataCSVNote(recording, eventFile.getAbsolutePath(), taskId);
        }

        return new ArrayList<>();
    }

    private Recording createRecording(File recordingFile) {
        if (recordingFile.isFile() && recordingFile.exists()) {
            String mp3Filepath = recordingFile.getAbsolutePath();
            long mp3Duration = calculateRecordingTime(recordingFile);
            return new Recording(mp3Filepath, mp3Duration);
        }

        return null;
    }

    protected ArrayList<String[]> readLines(File dataFile){
        ArrayList<String[]> lines = new ArrayList<>();

        if(dataFile.exists()){
            ReadCSV readCSV = new ReadCSV();
            lines = readCSV.readAssessmentDataCSVNote(dataFile.getAbsolutePath());
        }

        return lines;
    }

    private int calculateRecordingIndex(String input){
        int underscoreIndex = input.indexOf('_');
        if (underscoreIndex != -1) {
            String numberString = input.substring(underscoreIndex + 1, input.lastIndexOf('.'));
            try {
                return Integer.parseInt(numberString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return -1;
            }
        } else {
            return -1;
        }
    }

    private long calculateRecordingTime(File recordingFile){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(recordingFile.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(durationStr);
    }

    @Override
    public void createFolderStructure() {
        Utils.createFolder(assessmentFolderPath + File.separator + "Recordings");
        Utils.createFolder(assessmentFolderPath + File.separator + "CSV");
    }


    protected abstract void retrieveConcreteAssessment(ArrayList<String[]> lines);
    public abstract void skipTaskRound();
    public abstract void startNewTaskRound();

    public void updateRecordingList(Recording recording){
        this.recordings[taskId] = recording;
    }

    public Recording[] getRecordings() {
        return recordings;
    }


    public String getFolderPath() {
        return assessmentFolderPath;
    }
    public int getTaskId(){return taskId;}

    public int getMaxRecordingNr() {
        return MAXRECORDING;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName(int taskId) {
        return "";
    }

    public Set<String> getCircumstances() {
        return circumstances;
    }

    public Set<String> getNotes() {
        return notes;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public String getFormattedCircumstances(){
        if(circumstances.size() == 0) {
            return "/";
        } else {
            return String.join(", ", circumstances);
        }
    }

    public String getFormattedNotes(){
        if(notes.size() == 0) {
            return "/";
        } else {
            return String.join(", ", notes);
        }
    }

    public void addCircumstances(String circumstance) {
        this.circumstances.add(circumstance);
    }

    public void addNotes(String note) {
        this.notes.add(note);
    }
    public void removeCircumstances(String circumstance) {
        this.circumstances.remove(circumstance);
    }

    public void removeNotes(String note) {
        this.notes.remove(note);
    }
}
