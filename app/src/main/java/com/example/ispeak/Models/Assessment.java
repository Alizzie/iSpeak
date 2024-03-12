package com.example.ispeak.Models;

import android.os.Parcelable;
import android.util.Log;

import com.example.ispeak.Interfaces.FolderStructureCreator;

import java.io.File;

public abstract class Assessment implements FolderStructureCreator, Parcelable {

    protected String parentPath, folderPath;
    protected Recording[] recordings;
    protected int taskId = 0;
    protected int maxRecordingNr;

    public void updateRecordingList(Recording recording){
        this.recordings[taskId] = recording;
    }

    public Recording[] getRecordings() {
        return recordings;
    }

    public String getFolderPath() {
        return folderPath;
    }
    public int getTaskId(){return taskId;}

    public int getMaxRecordingNr() {
        return maxRecordingNr;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public void createFolderStructure() {
        folderPath = defineFolderPath();
        File directory = new File(folderPath);

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                Log.d("DIRECTORY", "SUCCESS: " + directory.getAbsolutePath());
            } else {
                Log.e("DIRECTORY", "FAIL");
            }
        } else {
            Log.d("DIRECTORY", "ALREADY EXISTS: " + directory.getAbsolutePath());
        }
    }

    protected abstract String defineFolderPath();

}
