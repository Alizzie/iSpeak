package com.example.ispeak.Models;

import com.example.ispeak.Interfaces.IReadCSV;
import com.example.ispeak.Utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;

public class Recording implements IReadCSV {
    private String mp3Filepath;
    private final long totalTime;
    private final long patientTime;
    private ArrayList<Event> events;

    public Recording(String mp3Filepath, long time){
        this(mp3Filepath, time, time, new ArrayList<>());
    }

    public Recording(String mp3Filepath, long totalTime, long patientTime, ArrayList<Event> events) {
        this.mp3Filepath = mp3Filepath;
        this.totalTime = totalTime;
        this.patientTime = patientTime;
        this.events = events;
    }

    public String getMp3Filepath() {
        return mp3Filepath;
    }

    public void setMp3Filepath(String mp3Filepath) {
        this.mp3Filepath = mp3Filepath;
    }

    public String getFormattedTotalTime(){
        return Utils.formatAudioTimeToStringPresentation(totalTime);
    }
    public long getPatientTime() {
        return patientTime;
    }

    public String getFormattedPatientTime(){
        return Utils.formatAudioTimeToStringPresentation(patientTime);
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
    public Event onReadEventsCSV(String[] line, int eventId, int taskId) {
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
