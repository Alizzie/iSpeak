package com.example.ispeak.Models;

import com.example.ispeak.Interfaces.FolderStructureCreator;
import com.example.ispeak.Interfaces.WriteCSVInterface;
import com.example.ispeak.Utils.Utils;
import com.example.ispeak.Utils.WriteCSV;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Assessment implements FolderStructureCreator, WriteCSVInterface {

    protected String assessmentFolderPath, assessmentName;
    protected Recording[] recordings;
    protected int taskId;
    protected int maxRecordingNr;

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
        List<String> csvFiles = new ArrayList<>(Arrays.asList(csvFolder+"Results.csv", csvFolder + "Assessment.csv", csvFolder + "Events.csv"));
        writeCSV.storeDataCSVNote(this, csvFiles.get(1));
        writeCSV.storeEventDataCSVNote(this, csvFiles.get(2));

        if(taskId == 7) {
            writeCSV.sumTotalAssessment(csvFiles);
        }
    }



    @Override
    public void createFolderStructure() {
        Utils.createFolder(assessmentFolderPath + File.separator + "Recordings");
        Utils.createFolder(assessmentFolderPath + File.separator + "CSV");
    }


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
}
