package com.example.ispeak.Models;

import android.util.Log;

import com.example.ispeak.Interfaces.IWriteCSV;
import com.example.ispeak.Utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Event implements IWriteCSV {

    private long timeStart, timeEnd, duration;
    private final int EVENTID, TASKID;
    private ArrayList<String> eventLabels;

    public Event(int EVENTID, int TASKID, long timeStart, long timeEnd, String firstLabel){
        this.EVENTID = EVENTID;
        this.TASKID = TASKID;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.eventLabels = new ArrayList<>();
        calculateDuration();
        addEventLabel(firstLabel);
    }
    public Event(int EVENTID, int TASKID, long timeStart){
        this(EVENTID, TASKID, timeStart, timeStart, "");
    }

    public Event(int EVENTID, int TASKID, long timeStart, long timeEnd, ArrayList<String> eventLabels){
        this.EVENTID = EVENTID;
        this.TASKID = TASKID;
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
    public String getFormattedTimeStart(){return Utils.formatAudioTimeToStringPresentation(timeStart);}
    public String getFormattedTimeEnd(){return Utils.formatAudioTimeToStringPresentation(timeEnd);}

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
        ArrayList<String> data = new ArrayList<>(Arrays.asList(String.valueOf(TASKID), getFormattedTimeStart(), getFormattedTimeEnd(), labels));

        return new ArrayList<>(Arrays.asList(headline.toArray(new String[0]), data.toArray(new String[0])));
    }
}
