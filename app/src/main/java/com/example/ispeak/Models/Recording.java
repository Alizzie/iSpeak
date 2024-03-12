package com.example.ispeak.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Recording implements Parcelable {

    //private String wav_filepath, mp3_filepath;
    private String mp3_filepath;
    private int totalTime, patientTime;
    private int taskId;
    private ArrayList<Event> events;

    public Recording(String mp3_filepath, int totalTime, int patientTime, int taskId, ArrayList<Event> events) {
        this.mp3_filepath = mp3_filepath;
        this.totalTime = totalTime;
        this.patientTime = patientTime;
        this.taskId = taskId;
        this.events = events;
    }

    protected Recording(Parcel in) {
        mp3_filepath = in.readString();
        totalTime = in.readInt();
        patientTime = in.readInt();
        taskId = in.readInt();
        events = in.createTypedArrayList(Event.CREATOR);
    }

    public static final Creator<Recording> CREATOR = new Creator<Recording>() {
        @Override
        public Recording createFromParcel(Parcel in) {
            return new Recording(in);
        }

        @Override
        public Recording[] newArray(int size) {
            return new Recording[size];
        }
    };

    public String getMp3_filepath() {
        return mp3_filepath;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getPatientTime() {
        return patientTime;
    }

    public int getTaskId() {
        return taskId;
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

    public int getTrialId() {
        int lastUnderscoreIndex = mp3_filepath.lastIndexOf("_");
        int secondLastUnderscoreIndex = mp3_filepath.lastIndexOf("_", lastUnderscoreIndex - 1);

        if (secondLastUnderscoreIndex != -1 && lastUnderscoreIndex != -1) {
            String trialIdString = mp3_filepath.substring(secondLastUnderscoreIndex + 1, lastUnderscoreIndex);

            try {
                int extractedTrialId = Integer.parseInt(trialIdString);
                return extractedTrialId;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mp3_filepath);
        parcel.writeInt(totalTime);
        parcel.writeInt(patientTime);
        parcel.writeInt(taskId);
        parcel.writeTypedList(events);
    }
}
