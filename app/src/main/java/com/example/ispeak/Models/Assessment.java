package com.example.ispeak.Models;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.example.ispeak.Interfaces.FolderStructureCreator;
import com.example.ispeak.Interfaces.WriteCSVInterface;
import com.example.ispeak.Utils.ReadCSV;
import com.example.ispeak.Utils.Utils;
import com.example.ispeak.Utils.WriteCSV;

import java.io.File;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Assessment implements FolderStructureCreator, WriteCSVInterface {

    protected String assessmentFolderPath, assessmentName;
    protected Recording[] recordings;
    protected int taskId;
    protected int maxRecordingNr;
    protected Set<String> circumstances;
    protected Set<String> notes;
    private boolean isCompleted;

    public Assessment(String assessmentName, int maxRecordingNr){
        this.assessmentName = assessmentName;
        this.taskId = 0;
        this.maxRecordingNr = maxRecordingNr;
        this.recordings = new Recording[maxRecordingNr];
        this.isCompleted = false;
        assessmentFolderPath = Patient.getInstance().getPatientFolderPath() +
                File.separator + assessmentName;
        createFolderStructure();
    }

    protected void saveTaskResultsInCSV(){
        WriteCSV writeCSV = new WriteCSV();

        String csvFolder = assessmentFolderPath + File.separator + "CSV" + File.separator;
        List<String> csvFiles = new ArrayList<>(Arrays.asList(csvFolder + "Assessment.csv", csvFolder + "Events.csv"));
        writeCSV.storeDataCSVNote(this, csvFiles.get(0));
        writeCSV.storeEventDataCSVNote(this, csvFiles.get(1));
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

    @Override
    public void createFolderStructure() {
        Utils.createFolder(assessmentFolderPath + File.separator + "Recordings");
        Utils.createFolder(assessmentFolderPath + File.separator + "CSV");
    }


    public void retrieveAssessment(File file) {
        File recordingsDir = new File(file, "Recordings");
        File csvDir = new File(file, "CSV");
        ArrayList<String[]> lines = readLines(new File(csvDir, "Assessment.csv"));

        if (recordingsDir.exists() && recordingsDir.isDirectory() && csvDir.exists() && csvDir.isDirectory()) {
            retrieveRecordings(recordingsDir, csvDir);
            //lines.get(0) = notes headline
            retrieveNotes(lines.get(1));
            this.retrieveConcreteAssessment(lines);
        }
    }

    private void retrieveNotes(String[] notes) {
        boolean isCompleted = notes[0].equals("true");
        String[] circumstances = notes[1].split("/");
        String[] comments = notes[2].split("/");

        if(circumstances.length == 1 && circumstances[0].equals("/")){
            return;
        }

        if(comments.length == 1 && comments[0].equals("/")){
            return;
        }

        this.isCompleted = isCompleted;
        this.circumstances = new HashSet<>(Arrays.asList(circumstances));
        this.notes = new HashSet<>(Arrays.asList(comments));
    }

    protected ArrayList<String[]> readLines(File dataFile){
        ArrayList<String[]> lines = new ArrayList<>();

        if(dataFile.exists()){
            ReadCSV readCSV = new ReadCSV();
            lines = readCSV.readAssessmentDataCSVNote(dataFile.getAbsolutePath());
        }

        return lines;
    }

    private void retrieveRecordings(File recordingsDir, File csvDir){
        File[] recordingFiles = recordingsDir.listFiles();

        if (recordingFiles != null) {
            Arrays.sort(recordingFiles);
            for (int i = 0; i < recordingFiles.length; i++) {
                File recordingFile = recordingFiles[i];
                Recording recording = retrieveRecording(recordingFile);
                recording.setEvents(retrieveRecordingEvents(csvDir, recording, i));

                int recordingIndex = calculateRecordingIndex(recording.getMp3_filepath());
                recordings[recordingIndex] = recording;
            }
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

    private Recording retrieveRecording(File recordingFile) {
        if (recordingFile.isFile() && recordingFile.exists()) {
            String mp3Filepath = recordingFile.getAbsolutePath();
            long mp3Duration = calculateRecordingTime(recordingFile);
            return new Recording(mp3Filepath, mp3Duration);
        }

        return null;
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

    public abstract void retrieveConcreteAssessment(ArrayList<String[]> lines);
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
        return maxRecordingNr;
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
