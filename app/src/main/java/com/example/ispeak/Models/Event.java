package com.example.ispeak.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.ispeak.Interfaces.ReadEventCSVInterface;
import com.example.ispeak.Interfaces.WriteCSVInterface;
import com.example.ispeak.Utils.Utils;
import com.opencsv.CSVWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Event implements WriteCSVInterface {

    private long timeStart, timeEnd, duration;
    private int eventId, taskId;
    private ArrayList<String> eventLabels = new ArrayList<>();

    public Event(int eventId, int taskId, long timeStart, long timeEnd, long duration, String firstLabel){
        this.eventId = eventId;
        this.taskId = taskId;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.duration = duration;
        addEventLabel(firstLabel);
    }
    public Event(int eventId, int taskId, long timeStart){
        this(eventId, taskId, timeStart, timeStart, 0, "");
    }

    public Event(int eventId, int taskId, long timeStart, long timeEnd, ArrayList<String> eventLabels){
        this.eventId = eventId;
        this.taskId = taskId;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.eventLabels = eventLabels;
        calculateDuration();
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
        calculateDuration();
        documentEvent();
    }

    private void calculateDuration() {
        this.duration = timeEnd - timeStart;
    }

    private void documentEvent(){
        Log.d("PLAYBACKK", "Starttime " + timeStart + ", Endtime " + timeEnd + ", duration " + duration);
    }

    public long getTimeStart() {
        return timeStart;
    }
    public String getFormattedTimeStart(){return Utils.formatTime(timeStart);}
    public String getFormattedTimeEnd(){return Utils.formatTime(timeEnd);}

    public ArrayList<String> getEventLabelsList(){
        return eventLabels;
    }

    public String getEventLabels(){
        return String.join(",", eventLabels);
    }

    public void addEventLabel(String label){
        if(label == null || label.trim().isEmpty()) {
            return;
        }

        eventLabels.add(label);
    }

    public void removeEventLabel(String label){
        eventLabels.remove(label);
    }

    @Override
    public List<String[]> onWriteCSV() {
        ArrayList<String> headline = new ArrayList<>(Arrays.asList("TaskNr", "TimeStart", "TimeEnd", "Labels"));

        String labels = String.join("/", eventLabels);
        ArrayList<String> data = new ArrayList<>(Arrays.asList(String.valueOf(taskId), getFormattedTimeStart(), getFormattedTimeEnd(), labels));

        return new ArrayList<>(Arrays.asList(headline.toArray(new String[0]), data.toArray(new String[0])));
    }
}
