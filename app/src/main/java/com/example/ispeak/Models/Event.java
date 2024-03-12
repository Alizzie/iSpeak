package com.example.ispeak.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class Event implements Parcelable {

    private float timeStart, timeEnd, duration;
    private int eventId, taskId;
    private ArrayList<String> eventLabels = new ArrayList<>();

    public Event(int eventId, int taskId, float timeStart, float timeEnd, float duration, String firstLabel){
        this(eventId, taskId, timeStart);
        this.timeEnd = timeEnd;
        this.duration = duration;
        eventLabels.add(firstLabel);
    }

    public Event(int eventId, int taskId, float timeStart){
        this.eventId = eventId;
        this.taskId = taskId;
        this.timeStart = timeStart;
    }

    public void setTimeStart(float timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(float timeEnd) {
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

    public float getTimeStart() {
        return timeStart;
    }

    public ArrayList<String> getEventLabelsList(){
        return eventLabels;
    }

    public String getEventLabels(){
        return eventLabels.toString();
    }

    public void addEventLabel(String label){
        eventLabels.add(label);
    }

    public void removeEventLabel(String label){
        eventLabels.remove(label);
    }

    protected Event(Parcel in) {
        timeStart = in.readFloat();
        timeEnd = in.readFloat();
        duration = in.readFloat();
        eventId = in.readInt();
        taskId = in.readInt();
        eventLabels = in.createStringArrayList();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(timeStart);
        parcel.writeFloat(timeEnd);
        parcel.writeFloat(duration);
        parcel.writeInt(eventId);
        parcel.writeInt(taskId);
        parcel.writeStringList(eventLabels);
    }
}
