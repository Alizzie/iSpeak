package com.example.ispeak.Models;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.ispeak.Interfaces.FolderStructureCreator;
import com.example.ispeak.Interfaces.WriteCSVInterface;
import com.example.ispeak.Utils.Utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Recording {

    //private String wav_filepath, mp3_filepath;
    private String mp3_filepath;
    private long totalTime, patientTime;
    private int taskId;
    private ArrayList<Event> events;

    public Recording(String mp3_filepath, long totalTime, long patientTime, int taskId, ArrayList<Event> events) {
        this.mp3_filepath = mp3_filepath;
        this.totalTime = totalTime;
        this.patientTime = patientTime;
        this.taskId = taskId;
        this.events = events;
    }

    public String getMp3_filepath() {
        return mp3_filepath;
    }

    public void setMp3_filepath(String mp3_filepath) {
        this.mp3_filepath = mp3_filepath;
    }

    public String getFormattedTotalTime(){
        return Utils.formatTime(totalTime);
    }
    public long getPatientTime() {
        return patientTime;
    }

    public String getFormattedPatientTime(){
        return Utils.formatTime(patientTime);
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event){
        events.add(event);
    }

    public void removeEvent(int position){
        events.remove(position);
    }

}
