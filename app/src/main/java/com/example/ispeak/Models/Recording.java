package com.example.ispeak.Models;

import com.example.ispeak.Utils.Utils;
import java.util.ArrayList;

public class Recording {

    //private String wav_filepath, mp3_filepath;
    private String mp3_filepath;
    private final long totalTime;
    private final long patientTime;
    private final ArrayList<Event> events;
    private boolean isEvaluated;
    private int evaluationScore;

    public Recording(String mp3_filepath, long totalTime, long patientTime, ArrayList<Event> events) {
        this.mp3_filepath = mp3_filepath;
        this.totalTime = totalTime;
        this.patientTime = patientTime;
        this.events = events;
        this.isEvaluated = false;
        this.evaluationScore = 0;
    }

    public boolean isEvaluated() {
        return isEvaluated;
    }

    public int getEvaluationScore() {
        return evaluationScore;
    }

    public void setEvaluated(boolean evaluated) {
        isEvaluated = evaluated;
    }

    public void setEvaluationScore(int evaluationScore) {
        this.evaluationScore = evaluationScore;
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
