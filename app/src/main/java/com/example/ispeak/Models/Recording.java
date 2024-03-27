package com.example.ispeak.Models;

import com.example.ispeak.Interfaces.ReadEventCSVInterface;
import com.example.ispeak.Utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;

public class Recording implements ReadEventCSVInterface {

    //private String wav_filepath, mp3_filepath;
    private String mp3_filepath;
    private final long totalTime;
    private final long patientTime;
    private ArrayList<Event> events;

    public Recording(String mp3_filepath, long time){
        this(mp3_filepath, time, time, new ArrayList<>());
    }

    public Recording(String mp3_filepath, long totalTime, long patientTime, ArrayList<Event> events) {
        this.mp3_filepath = mp3_filepath;
        this.totalTime = totalTime;
        this.patientTime = patientTime;
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

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    @Override
    public Event onReadCSV(String[] line, int eventId, int taskId) {
        long timeStart = Utils.parseTimeToMilliseconds(line[1]);
        long timeEnd = Utils.parseTimeToMilliseconds(line[2]);

        String labels = line[3];
        ArrayList<String> eventLabels;

        if(labels.trim().isEmpty()) {
            eventLabels = new ArrayList<>();
        } else {
            String[] formattedLabels = labels.split("/");
            eventLabels = new ArrayList<>(Arrays.asList(formattedLabels));
        }

        return new Event(eventId, taskId, timeStart, timeEnd, eventLabels);
    }

}
