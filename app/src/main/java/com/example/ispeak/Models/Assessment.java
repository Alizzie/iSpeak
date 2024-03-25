package com.example.ispeak.Models;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.example.ispeak.Interfaces.FolderStructureCreator;
import com.example.ispeak.Interfaces.WriteCSVInterface;
import com.example.ispeak.Utils.ReadCSV;
import com.example.ispeak.Utils.Utils;
import com.example.ispeak.Utils.WriteCSV;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public abstract class Assessment implements FolderStructureCreator, WriteCSVInterface {

    protected String assessmentFolderPath, assessmentName;
    protected Recording[] recordings;
    protected int taskId;
    protected int maxRecordingNr;
    protected Set<String> circumstances;
    protected Set<String> notes;

    public Assessment(String assessmentName, int maxRecordingNr){
        this.assessmentName = assessmentName;
        this.taskId = 0;
        this.maxRecordingNr = maxRecordingNr;
        this.recordings = new Recording[maxRecordingNr];
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

    @Override
    public void createFolderStructure() {
        Utils.createFolder(assessmentFolderPath + File.separator + "Recordings");
        Utils.createFolder(assessmentFolderPath + File.separator + "CSV");
    }


    public void retrieveAssessment(File file) {
        File recordingsDir = new File(file, "Recordings");
        File csvDir = new File(file, "CSV");

        if (recordingsDir.exists() && recordingsDir.isDirectory() && csvDir.exists() && csvDir.isDirectory()) {
            retrieveRecordings(recordingsDir, csvDir);
            this.retrieveConcreteAssessment(csvDir);
        }
    }

    private void retrieveRecordings(File recordingsDir, File csvDir){
        File[] recordingFiles = recordingsDir.listFiles();

        if (recordingFiles != null) {
            Arrays.sort(recordingFiles);
            for (int i = 0; i < recordingFiles.length; i++) {
                File recordingFile = recordingFiles[i];
                Recording recording = retrieveRecording(recordingFile);
                recording.setEvents(retrieveRecordingEvents(csvDir, recording, i));
                recordings[i] = recording;
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

    private long calculateRecordingTime(File recordingFile){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(recordingFile.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(durationStr);
    }

    public abstract void retrieveConcreteAssessment(File csvDir);

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
